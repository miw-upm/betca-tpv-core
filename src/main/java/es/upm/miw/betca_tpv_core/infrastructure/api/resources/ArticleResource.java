package es.upm.miw.betca_tpv_core.infrastructure.api.resources;


import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.services.ArticleService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ArticleBarcodesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(ArticleResource.ARTICLES)
public class ArticleResource {
    public static final String ARTICLES = "/articles";

    public static final String BARCODE_ID = "/{barcode}";
    public static final String SEARCH = "/search";
    public static final String UNFINISHED = "/unfinished";
    public static final String BARCODE = "/barcode";

    private final ArticleService articleService;

    @Autowired
    public ArticleResource(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono< Article > create(@Valid @RequestBody Article article) {
        article.doDefault();
        return this.articleService.create(article);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(BARCODE_ID)
    public Mono< Article > read(@PathVariable String barcode) {
        return this.articleService.read(barcode);
    }

    @PutMapping(BARCODE_ID)
    public Mono< Article > update(@PathVariable String barcode, @Valid @RequestBody Article article) {
        article.doDefault();
        return this.articleService.update(barcode, article);
    }

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

}
