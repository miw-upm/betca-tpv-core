package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyView;
import es.upm.miw.betca_tpv_core.domain.model.TreeType;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticleFamilyViewPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticlesTreeReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ProviderReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticleEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticlesTreeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ArticleFamilyViewPersistenceMongodb implements ArticleFamilyViewPersistence {

    private ArticlesTreeReactive articlesTreeReactive;

    @Autowired
    public ArticleFamilyViewPersistenceMongodb(ArticlesTreeReactive articlesTreeReactive) {
        this.articlesTreeReactive = articlesTreeReactive;
    }

    @Override
    public Flux<ArticleFamilyView> readByReference(String reference) {
        //return Flux.just( ArticleFamilyView.builder().reference(reference).description("hola").type(TreeType.ARTICLES).price(null).build());

        String newReference = reference;
        if(reference.equals("undefined")){
            newReference = "root";
        }

        return this.articlesTreeReactive.findFirstByReference(newReference)
                .flatMapIterable(ArticlesTreeEntity::contents)
                .map(ArticlesTreeEntity::toArticleFamilyViewDto);
    }



}

