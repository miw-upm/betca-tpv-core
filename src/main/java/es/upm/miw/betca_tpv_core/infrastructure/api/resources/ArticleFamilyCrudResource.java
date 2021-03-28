package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import es.upm.miw.betca_tpv_core.domain.services.ArticleFamilyCrudService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ArticleBarcodeWithParentReferenceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(ArticleFamilyCrudResource.ARTICLE_FAMILY_CRUD)
public class ArticleFamilyCrudResource {
    public static final String ARTICLE_FAMILY_CRUD = "/article-family-crud";
    public static final String SINGLE = "/single";
    public static final String REFERENCE = "/{reference}";
    public static final String ID = "/{id}";
    public static final String BARCODE = "/{barcode}";
    public static final String PARENT = "/parent";


    private final ArticleFamilyCrudService articleFamilyCrudService;

    @Autowired
    public ArticleFamilyCrudResource(ArticleFamilyCrudService articleFamilyCrudService) {
        this.articleFamilyCrudService = articleFamilyCrudService;
    }

    @GetMapping(REFERENCE)
    public Mono<ArticleFamilyCrud> read(@PathVariable String reference) {
        return this.articleFamilyCrudService.read(reference);
    }

    @DeleteMapping(ID)
    public Mono<Void> delete(@PathVariable String id) {
        return this.articleFamilyCrudService.delete(id);
    }


    @PostMapping(produces = {"application/json"})
    public Mono<ArticleFamilyCrud> createComposeArticleFamily(@Valid @RequestBody ArticleFamilyCrud articleFamilyCrud) {
        articleFamilyCrud.doDefault();
        return this.articleFamilyCrudService.createComposeArticleFamily(articleFamilyCrud);
    }

    @PutMapping(produces = {"application/json"})
    public Mono<ArticleFamilyCrud> editComposeArticleFamily(@Valid @RequestBody ArticleFamilyCrud articleFamilyCrud) {
        articleFamilyCrud.doDefault();
        return this.articleFamilyCrudService.editComposeArticleFamily(articleFamilyCrud);
    }

    @PostMapping(value = SINGLE, produces = {"application/json"})
    public Mono<ArticleFamilyCrud> addArticleToArticleFamily(@Valid @RequestBody ArticleBarcodeWithParentReferenceDto articleBarcodeWithParentReferenceDto) {
        return this.articleFamilyCrudService.addArticleToArticleFamily(articleBarcodeWithParentReferenceDto);
    }


}

