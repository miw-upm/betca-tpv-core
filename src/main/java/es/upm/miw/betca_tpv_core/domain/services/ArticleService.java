package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class ArticleService {

    private final ArticlePersistence articlePersistence;

    @Autowired
    public ArticleService(ArticlePersistence articlePersistence) {
        this.articlePersistence = articlePersistence;
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
}
