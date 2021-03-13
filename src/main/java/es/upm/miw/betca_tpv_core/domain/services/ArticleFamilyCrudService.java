package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticleFamilyCrudPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ArticleFamilyCrudService {

    private ArticleFamilyCrudPersistence articleFamilyCrudPersistence;

    @Autowired
    public ArticleFamilyCrudService(ArticleFamilyCrudPersistence articleFamilyCrudPersistence) {
        this.articleFamilyCrudPersistence = articleFamilyCrudPersistence;
    }
    public Mono<ArticleFamilyCrud> read(String reference) {
        return this.articleFamilyCrudPersistence.readByReference(reference);
    }

    public Mono<Void> delete(String reference) {
        return this.articleFamilyCrudPersistence.deleteByReference(reference);
    }
    public Mono<ArticleFamilyCrud> createCompose(ArticleFamilyCrud articleFamilyCrud) {
        return this.articleFamilyCrudPersistence.createCompose(articleFamilyCrud);
    }
}
