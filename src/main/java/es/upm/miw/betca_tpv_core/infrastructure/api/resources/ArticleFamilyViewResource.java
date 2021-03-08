package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.TreeType;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ArticleFamilyViewDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

@Rest
@RequestMapping(ArticleFamilyViewResource.ARTICLE_FAMILY)
public class ArticleFamilyViewResource {
    public static final String ARTICLE_FAMILY = "/article-family-view";

    public static final String REFERENCE_ID = "/{reference}";

    @GetMapping(REFERENCE_ID)
    public Flux<ArticleFamilyViewDto> read(@PathVariable String reference) {
        if(reference==null) {
            return Flux.just(new ArticleFamilyViewDto("1", "Zarzuela", TreeType.ARTICLES, null),
                    new ArticleFamilyViewDto("2", "Varios", TreeType.ARTICLES, null));

        } else return Flux.just(new ArticleFamilyViewDto("3", "No", TreeType.SIZES, null),
                new ArticleFamilyViewDto("4", "Si", TreeType.ARTICLE, new BigDecimal(20)));

    }

}
