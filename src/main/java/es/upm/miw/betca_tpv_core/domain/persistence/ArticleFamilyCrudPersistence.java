package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ArticleFamilyCrudPersistence {
    Mono<ArticleFamilyCrud> readByReference(String reference);

}