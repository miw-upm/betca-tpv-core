package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticleFamilyCrudPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticlesTreeReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticlesTreeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class FamilyArticleCrudPersistenceMongodb implements ArticleFamilyCrudPersistence {
    ArticlesTreeReactive articlesTreeReactive;

    @Autowired
    public FamilyArticleCrudPersistenceMongodb(ArticlesTreeReactive articlesTreeReactive) {
        this.articlesTreeReactive = articlesTreeReactive;
    }

    @Override
    public Mono<ArticleFamilyCrud> readByReference(String reference) {
        return this.articlesTreeReactive.findByReference(reference)
                .map(ArticlesTreeEntity::toDto);
    }
    private Mono<List<ArticlesTreeEntity>> getListOfArticleFamily(String reference){
        return this.articlesTreeReactive.findByReference(reference)
                .switchIfEmpty(Mono.error(new NotFoundException("Not existent article family with this reference")))
                .map(ArticlesTreeEntity::contents);
    }
}
