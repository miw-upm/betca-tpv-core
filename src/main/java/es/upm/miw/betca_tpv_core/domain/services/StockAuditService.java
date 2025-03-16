package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.ArticleLoss;
import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAuditPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.StockAuditCreateDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class StockAuditService {

    private final StockAuditPersistence stockAuditPersistence;

    private final ArticlePersistence articlePersistence;

    public StockAuditService(StockAuditPersistence stockAuditPersistence, ArticlePersistence articlePersistence) {
        this.stockAuditPersistence = stockAuditPersistence;
        this.articlePersistence = articlePersistence;
    }

    public Flux<StockAudit> findAll() {
        return stockAuditPersistence.findAll();
    }

    public Mono<StockAudit> read(String id) {
        return stockAuditPersistence.read(id);
    }

    public Mono<StockAuditCreateDto> create() {
        return this.crearStockAudit()
                .flatMap(stockAudit ->
                        stockAuditPersistence.save(stockAudit)
                                .map(StockAuditCreateDto::new)
                );
    }

    private Mono<StockAudit> crearStockAudit() {
        StockAudit stockAudit = new StockAudit();
        stockAudit.setId("AUDIT" + System.currentTimeMillis());
        stockAudit.setCreationDate(LocalDateTime.now());
        stockAudit.setCloseDate(null);
        stockAudit.setLossValue(BigDecimal.valueOf(0));
        stockAudit.setLosses(List.of());
        return Mono.just(stockAudit);
    }

    public Mono<Void> close(String auditId) {
        return Mono.zip(
                        stockAuditPersistence.read(auditId)
                                .onErrorResume(NotFoundException.class, e -> Mono.empty()),
                        articlePersistence.findByDiscontinuedIsFalse().collectList()
                )
                .flatMap(tuple -> {
                    StockAudit stockAudit = tuple.getT1();
                    List<Article> currentArticles = tuple.getT2();

                    if (stockAudit.getCloseDate() != null) {
                        return Mono.error(new IllegalStateException("La auditoría ya está cerrada."));
                    }

                    List<Article> articlesWithoutAudit = getArticlesWithoutAudit(currentArticles, stockAudit);
                    List<ArticleLoss> losses = getArticleLosses(stockAudit, currentArticles);

                    BigDecimal lossValue = getLossValue(losses,currentArticles);

                    stockAudit.setCloseDate(LocalDateTime.now());
                    stockAudit.setLossValue(lossValue);
                    stockAudit.setLosses(losses);
                    stockAudit.setArticlesWithoutAudit(articlesWithoutAudit);
                    return stockAuditPersistence.close(stockAudit);
                })
                .then();
    }

    private List<ArticleLoss> getArticleLosses(StockAudit stockAudit, List<Article> currentArticles) {
        return stockAudit.getArticlesAudited().stream()
                .flatMap(auditArticle -> currentArticles.stream()
                        .filter(currentArticle -> currentArticle.getBarcode().equals(auditArticle.getBarcode()))
                        .map(currentArticle -> {
                            int difference = currentArticle.getStock() - auditArticle.getStock();
                            return difference != 0 ? new ArticleLoss(auditArticle.getBarcode(), (double) Math.abs(difference)) : null;
                        })
                        .filter(Objects::nonNull))
                .toList();
    }

    private BigDecimal getLossValue(List<ArticleLoss> losses, List<Article> currentArticles) {
        return losses.stream()
                .map(loss -> currentArticles.stream()
                        .filter(article -> article.getBarcode().equals(loss.getBarcode()))
                        .findFirst()
                        .map(Article::getRetailPrice)
                        .orElse(BigDecimal.valueOf(0))
                        .multiply(BigDecimal.valueOf(loss.getAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Article> getArticlesWithoutAudit(List<Article> currentArticles, StockAudit stockAudit) {
        List<Article> articlesWithoutAudit = new ArrayList<>(currentArticles.stream()
                .filter(article -> stockAudit.getArticlesAudited().stream()
                        .noneMatch(a -> a.getBarcode().equals(article.getBarcode())))
                .toList());

        articlesWithoutAudit.addAll(stockAudit.getArticlesAudited().stream()
                .filter(auditArticle -> currentArticles.stream()
                        .noneMatch(currentArticle -> currentArticle.getBarcode().equals(auditArticle.getBarcode())))
                .toList());
        return articlesWithoutAudit;
    }

    public Mono<Void> update(String id) {
        return this.stockAuditPersistence.update(id);
    }
}
