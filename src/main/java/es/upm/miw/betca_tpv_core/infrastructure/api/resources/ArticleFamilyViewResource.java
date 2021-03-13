package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyView;
import es.upm.miw.betca_tpv_core.domain.services.ArticleFamilyViewService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;

@Rest
@RequestMapping(ArticleFamilyViewResource.ARTICLE_FAMILY)
public class ArticleFamilyViewResource {
    public static final String ARTICLE_FAMILY = "/article-family-view";

    public static final String REFERENCE_ID = "/{reference}";

    private ArticleFamilyViewService articleFamilyViewService;

    @Autowired
    public ArticleFamilyViewResource(ArticleFamilyViewService articleFamilyViewService){
        this.articleFamilyViewService = articleFamilyViewService;
    }

    @GetMapping(REFERENCE_ID)
    public Flux<ArticleFamilyView> read(@PathVariable String reference) {
        return this.articleFamilyViewService.read(reference);
    }
}
