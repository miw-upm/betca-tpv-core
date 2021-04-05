package es.upm.miw.betca_tpv_core.infrastructure.api.resources;


import es.upm.miw.betca_tpv_core.domain.model.ArticleSizeFamily;
import es.upm.miw.betca_tpv_core.domain.services.ArticleSizeFamilyService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(ArticleSizeFamilyResource. ARTICLES_SIZE_FAMILY)
public class ArticleSizeFamilyResource {
    public static final String  ARTICLES_SIZE_FAMILY = "/article-size";

    private ArticleSizeFamilyService articleSizeFamilyService;


    @Autowired
    public void setArticleSizeService(ArticleSizeFamilyService articleSizeFamilyService) {
        this.articleSizeFamilyService = articleSizeFamilyService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<ArticleSizeFamily> create(@Valid @RequestBody ArticleSizeFamily articleSizeFamily) {
        articleSizeFamily.doDefault();
        return this.articleSizeFamilyService.create(articleSizeFamily);
    }

}
