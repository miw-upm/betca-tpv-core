package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.ComplaintPersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.TicketPersistence;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class ArticleService {

    private final ArticlePersistence articlePersistence;

    private final TicketPersistence ticketPersistence;

    private final ComplaintPersistence complaintPersistence;

    @Autowired
    public ArticleService(ArticlePersistence articlePersistence,TicketPersistence ticketPersistence,ComplaintPersistence complaintPersistence) {
        this.articlePersistence = articlePersistence;
        this.ticketPersistence = ticketPersistence;
        this.complaintPersistence = complaintPersistence;
    }

    public Mono<Article> create(Article article) {
        article.setRegistrationDate(LocalDateTime.now());
        return this.articlePersistence.create(article);
    }

    public Mono<Article> read(String barcode) {
        return this.articlePersistence.readByBarcode(barcode);
    }

    public Mono<Article> update(String barcode, Article article) {
        return this.articlePersistence.readByBarcode(barcode)
                .map(dataArticle -> {
                    BeanUtils.copyProperties(article, dataArticle, "registrationDate");
                    return dataArticle;
                }).flatMap(dataArticle -> this.articlePersistence.update(barcode, dataArticle));
    }

    public Flux<Article> findByBarcodeAndDescriptionAndReferenceAndStockLessThanAndDiscontinuedNullSafe(
            String barcode, String description, String reference, Integer stock, Boolean discontinued) {
        return this.articlePersistence.findByBarcodeAndDescriptionAndReferenceAndStockLessThanAndDiscontinuedNullSafe(
                barcode, description, reference, stock, discontinued);
    }

    public Flux<Article> findByUnfinished() {
        return this.articlePersistence.findByAnyNullField();
    }

    public Flux<String> findByBarcodeAndNotDiscontinuedNullSafe(String barcode) {
        return this.articlePersistence.findByBarcodeAndNotDiscontinuedNullField(barcode);
    }

    public Flux<Article> findByProviderCompany(String company) {
        return this.articlePersistence.findByProviderCompany(company);
    }

    public Flux<String> findByUserLoggedPurchasedBarcodesWithoutComplaints(String userMobile){
        return this.ticketPersistence.findByUserMobile(userMobile)
                .flatMap(ticket -> Flux.fromIterable(ticket.getShoppingList()))
                .map(Shopping::getBarcode)
                .distinct()
                .filterWhen(barcode -> this.complaintPersistence.findByUserMobileAndBarcode(userMobile,barcode.toString())
                        .hasElement()
                        .map(hasComplaint -> Boolean.FALSE.equals(hasComplaint))
                );
    }
}
