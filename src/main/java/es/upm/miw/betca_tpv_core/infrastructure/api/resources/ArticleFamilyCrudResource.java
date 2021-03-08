package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import es.upm.miw.betca_tpv_core.domain.services.ArticleFamilyCrudService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(ArticleFamilyCrudResource.ARTICLES)
public class ArticleFamilyCrudResource {
    public static final String ARTICLES = "/article-family-crud";
    public static final String REFERENCE = "/{reference}";

    private ArticleFamilyCrudService articleFamilyCrudService;

    @Autowired
    public ArticleFamilyCrudResource(ArticleFamilyCrudService articleFamilyCrudService) {
        this.articleFamilyCrudService = articleFamilyCrudService;
    }

    @GetMapping(REFERENCE)
    public Mono<ArticleFamilyCrud> read(@PathVariable String reference) {
        return this.articleFamilyCrudService.read(reference);
    }

    /*
    @GetMapping(SEARCH)
    public Flux< Article > findByBarcodeAndDescriptionAndReferenceAndStockLessThanAndDiscontinuedNullSafe(
            @RequestParam(required = false) String barcode, @RequestParam(required = false) String description, @
            RequestParam(required = false) String reference, @RequestParam(required = false) Integer stock,
            @RequestParam(required = false) Boolean discontinued) {
        return this.articleService.findByBarcodeAndDescriptionAndReferenceAndStockLessThanAndDiscontinuedNullSafe(
                barcode, description, reference, stock, discontinued)
                .map(Article::ofBarcodeDescriptionStock);
    }

    @GetMapping(BARCODE)
    public Mono< ArticleBarcodesDto > findByBarcodeNullSafe(@RequestParam(required = false) String barcode) {
        return this.articleService.findByBarcodeAndNotDiscontinuedNullSafe(barcode)
                .collectList()
                .map(ArticleBarcodesDto::new);
    }

    @GetMapping(UNFINISHED)
    public Flux< Article > findByUnfinished() {
        return this.articleService.findByUnfinished();
    }
*/
}

