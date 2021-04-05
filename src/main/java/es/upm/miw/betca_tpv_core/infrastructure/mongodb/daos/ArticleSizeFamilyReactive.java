package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticleSizeFamilyEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface ArticleSizeFamilyReactive extends ReactiveSortingRepository<ArticleSizeFamilyEntity, String > {


}
