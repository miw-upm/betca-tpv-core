package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticlesTreeEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface ArticlesTreeReactive extends ReactiveSortingRepository< ArticlesTreeEntity, String > {

}
