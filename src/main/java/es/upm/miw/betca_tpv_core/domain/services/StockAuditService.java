package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.*;
import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.persistence.*;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockAuditService {
    private final StockAuditPersistence stockAuditPersistence;
    private final ArticlePersistence articlePersistence;

    @Autowired
    public StockAuditService(StockAuditPersistence stockAuditPersistence,
                             ArticlePersistence articlePersistence) {
        this.stockAuditPersistence = stockAuditPersistence;
        this.articlePersistence = articlePersistence;
    }

    public Flux<StockAudit> readAll() {
        return stockAuditPersistence.findAll();
    }

    public Mono<StockAudit> readOne(String id) {
        return stockAuditPersistence.read(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Stock Audit not found: " + id)));
    }

    public Mono<StockAuditCreateDto> create() {
        return this.createStockAudit()
                .flatMap(stockAudit -> stockAuditPersistence.create(stockAudit)
                        .map(savedAudit -> new StockAuditCreateDto(savedAudit.getId())));
    }

    private Mono<StockAudit> createStockAudit() {
        return articlePersistence.findByDiscontinuedIsFalse()
                .collectList()
                .map(articles -> {
                    List<ArticleAudit> articleAudits = articles.stream()
                            .map(article -> ArticleAudit.builder()
                                    .barcode(article.getBarcode())
                                    .description(article.getDescription())
                                    .stock(article.getStock())
                                    .real(null)
                                    .retailPrice(article.getRetailPrice())
                                    .build())
                            .collect(Collectors.toList());

                    return StockAudit.builder()
                            .id("AUDIT" + System.currentTimeMillis())
                            .creationDate(LocalDateTime.now())
                            .updateDate(LocalDateTime.now())
                            .closeDate(null)
                            .lossValue(BigDecimal.ZERO)
                            .losses(new ArrayList<>())
                            .articlesAudited(new ArrayList<>())
                            .articlesWithoutAudit(articleAudits)
                            .build();
                });
    }

    public Mono<Void> close(String id) {
        return stockAuditPersistence.read(id)
                .flatMap(stockAudit -> {
                    if (stockAudit.getCloseDate() != null) {
                        return Mono.error(new IllegalStateException("Stock audit already closed"));
                    }

                    if (stockAudit.getArticlesWithoutAudit() != null
                            && !stockAudit.getArticlesWithoutAudit().isEmpty()) {
                        int pendingCount = stockAudit.getArticlesWithoutAudit().size();
                        String barcodes = stockAudit.getArticlesWithoutAudit().stream()
                                .map(ArticleAudit::getBarcode)
                                .limit(5)
                                .collect(Collectors.joining(", "));
                        return Mono.error(new IllegalStateException(
                                String.format("No se puede cerrar. Hay %d artículos pendientes: %s", pendingCount, barcodes)
                        ));
                    }

                    List<ArticleLoss> losses = this.calculateLosses(stockAudit);
                    BigDecimal lossValue = this.calculateLossValue(losses, stockAudit);

                    stockAudit.setCloseDate(LocalDateTime.now());
                    stockAudit.setLosses(losses);
                    stockAudit.setLossValue(lossValue);
                    stockAudit.setUpdateDate(LocalDateTime.now());

                    return stockAuditPersistence.update(stockAudit);
                })
                .then();
    }


    public Mono<Void> update(String id, StockAuditUpdateDto updateDto) {
        return stockAuditPersistence.read(id)
                .flatMap(stockAudit -> {
                    if (stockAudit.getCloseDate() != null) {
                        return Mono.error(new IllegalStateException("Stock audit already closed"));
                    }

                    // Copias defensivas para editar las listas
                    List<ArticleAudit> updatedAudited = new ArrayList<>(stockAudit.getArticlesAudited());
                    List<ArticleAudit> updatedWithoutAudit = new ArrayList<>(stockAudit.getArticlesWithoutAudit());

                    for (ArticleAuditDto auditDto : updateDto.getArticlesAudited()) {
                        // Busca el artículo en los pendientes
                        ArticleAudit articleToUpdate = updatedWithoutAudit.stream()
                                .filter(a -> a.getBarcode().equals(auditDto.getBarcode()))
                                .findFirst()
                                .orElseThrow(() -> new NotFoundException("Article not found: " + auditDto.getBarcode()));

                        validateRealStock(auditDto, articleToUpdate);

                        // Actualiza campos
                        articleToUpdate.setReal(auditDto.getReal());
                        articleToUpdate.setRetailPrice(auditDto.getRetailPrice());

                        // Mueve el artículo a auditados
                        updatedAudited.add(articleToUpdate);
                        updatedWithoutAudit.remove(articleToUpdate);
                    }

                    // Actualiza el estado en la entidad
                    stockAudit.setArticlesAudited(updatedAudited);
                    stockAudit.setArticlesWithoutAudit(updatedWithoutAudit);
                    stockAudit.setUpdateDate(LocalDateTime.now());

                    return stockAuditPersistence.update(stockAudit);
                })
                .then();
    }

    private List<ArticleLoss> calculateLosses(StockAudit stockAudit) {
        return stockAudit.getArticlesAudited().stream()
                .filter(art -> art.getReal() != null && !art.getReal().equals(art.getStock()))
                .map(art -> new ArticleLoss(
                        art.getBarcode(),
                        Double.valueOf(art.getStock() - art.getReal())
                ))
                .collect(Collectors.toList());
    }

    private BigDecimal calculateLossValue(List<ArticleLoss> losses, StockAudit stockAudit) {
        return losses.stream()
                .map(loss -> stockAudit.getArticlesAudited().stream()
                        .filter(a -> a.getBarcode().equals(loss.getBarcode()))
                        .findFirst()
                        .map(art -> art.getRetailPrice().multiply(BigDecimal.valueOf(loss.getAmount())))
                        .orElse(BigDecimal.ZERO)
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateRealStock(ArticleAuditDto auditDto, ArticleAudit article) {
        if (auditDto.getReal() == null) {
            throw new IllegalArgumentException("Real stock cannot be null for article: " + article.getBarcode());
        }
        if (auditDto.getReal() < 0) {
            throw new IllegalArgumentException("Real stock cannot be negative for article: " + article.getBarcode());
        }
        if (auditDto.getReal() > article.getStock()) {
            throw new IllegalArgumentException("Real stock cannot be greater than theoretical stock for article: " + article.getBarcode());
        }
    }
}