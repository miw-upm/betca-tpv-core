package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Article;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ArticlePersistence {

    Mono< Article > create(Article article);

    Mono< Article > readByBarcode(String barcode);

    Flux< Article > findByAnyNullField();

    Flux< Article > findByBarcodeAndDescriptionAndReferenceAndStockLessThanAndDiscontinuedNullSafe(
            String barcode, String description, String reference, Integer stock, Boolean discontinued);

    Mono< Article > update(String barcode, Article article);

    Mono< Article > readAndWriteStockByBarcodeAssured(String barcode, Integer stockIncrement);

    Flux< String > findByBarcodeAndNotDiscontinuedNullField(String barcode);
}
