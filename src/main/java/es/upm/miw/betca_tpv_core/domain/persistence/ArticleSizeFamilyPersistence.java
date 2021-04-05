package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.ArticleSizeFamily;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ArticleSizeFamilyPersistence {

    Mono<ArticleSizeFamily> create(ArticleSizeFamily articleSizeFamily);

}
