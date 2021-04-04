package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.TicketPersistence;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class ArticleService {

    private ArticlePersistence articlePersistence;
    private TicketPersistence ticketPersistence;

    @Autowired
    public ArticleService(ArticlePersistence articlePersistence, TicketPersistence ticketPersistence) {
        this.articlePersistence = articlePersistence;
        this.ticketPersistence = ticketPersistence;
    }

    public Mono< Article > create(Article article) {
        article.setRegistrationDate(LocalDateTime.now());
        return this.articlePersistence.create(article);
    }

    public Mono< Article > read(String barcode) {
        return this.articlePersistence.readByBarcode(barcode);
    }

    public Mono< Article > update(String barcode, Article article) {
        return this.articlePersistence.readByBarcode(barcode)
                .map(dataArticle -> {
                    BeanUtils.copyProperties(article, dataArticle, "registrationDate");
                    return dataArticle;
                }).flatMap(dataArticle -> this.articlePersistence.update(barcode, dataArticle));
    }

    public Flux< Article > findByBarcodeAndDescriptionAndReferenceAndStockLessThanAndDiscontinuedNullSafe(
            String barcode, String description, String reference, Integer stock, Boolean discontinued) {
        return this.articlePersistence.findByBarcodeAndDescriptionAndReferenceAndStockLessThanAndDiscontinuedNullSafe(
                barcode, description, reference, stock, discontinued);
    }

    public Flux< Article > findByUnfinished() {
        return this.articlePersistence.findByAnyNullField();
    }

    public Flux< String > findByBarcodeAndNotDiscontinuedNullSafe(String barcode) {
        return this.articlePersistence.findByBarcodeAndNotDiscontinuedNullField(barcode);
    }

    public Flux < Article > findArticleEntitiesByRegistrationDateAfter(LocalDateTime localDateTime){
        return this.articlePersistence.findArticleEntitiesByRegistrationDateAfter(localDateTime);
    }

    public Flux < Article > findTop5ArticleSalesLastWeek(){
        return this.findTop5BarcodeSalesLastWeek()
                .switchIfEmpty(Flux.error(new NotFoundException("There have been no articles sold in the last week")))
                .flatMap(barcode -> this.articlePersistence.findArticlesByBarcode(barcode));
    }

    private Flux<String> findTop5BarcodeSalesLastWeek(){
        Map<String, Integer> articleBarcodes = new HashMap<>();
        return this.ticketPersistence.findByRegistrationDateAfter(LocalDateTime.now().minusDays(7))
                .flatMap(ticket -> Flux.fromStream(ticket.getShoppingList().stream()))
                .doOnNext(shopping -> articleBarcodes
                        .merge(shopping.getBarcode(), shopping.getAmount(), (oldValue, newValue) -> newValue = oldValue + shopping.getAmount()))
                .thenMany(Flux.fromStream(articleBarcodes.entrySet().stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .limit(5)
                        .map(Map.Entry::getKey)));
    }
}
