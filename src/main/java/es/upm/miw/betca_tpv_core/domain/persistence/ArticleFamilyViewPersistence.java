package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyView;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ArticleFamilyViewPersistence {
    Flux<ArticleFamilyView> readByReference(String reference);
}

