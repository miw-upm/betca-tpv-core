package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import es.upm.miw.betca_tpv_core.domain.model.TreeType;
import es.upm.miw.betca_tpv_core.domain.services.ArticleFamilyCrudService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(ArticleFamilyCrudResource.ARTICLE_FAMILY_CRUD)
public class ArticleFamilyCrudResource {
    public static final String ARTICLE_FAMILY_CRUD = "/article-family-crud";
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

    @DeleteMapping(REFERENCE)
    public Mono<Void> delete(@PathVariable String reference) {
        return this.articleFamilyCrudService.delete(reference);
    }

    @PostMapping(produces = {"application/json"})
    public Mono<ArticleFamilyCrud> create(@RequestBody ArticleFamilyCrud articleFamilyCrud) {
        articleFamilyCrud.doDefault();
        if(articleFamilyCrud.getTreeType().equals(TreeType.ARTICLES) || articleFamilyCrud.getTreeType().equals(TreeType.SIZES)){
            return this.articleFamilyCrudService.createCompose(articleFamilyCrud, articleFamilyCrud.getParentReference());
        }
        else return Mono.just(ArticleFamilyCrud.builder().reference("SINGLE").build());
    }

}

