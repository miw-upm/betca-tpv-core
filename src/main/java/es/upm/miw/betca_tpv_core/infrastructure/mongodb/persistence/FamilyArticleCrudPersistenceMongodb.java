package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticleFamilyCrudPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticlesTreeReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous.ArticlesTreeDao;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticlesTreeEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CompositeArticleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class FamilyArticleCrudPersistenceMongodb implements ArticleFamilyCrudPersistence {
    ArticlesTreeReactive articlesTreeReactive;
    ArticlesTreeDao articlesTreeDao;

    @Autowired
    public FamilyArticleCrudPersistenceMongodb(ArticlesTreeReactive articlesTreeReactive, ArticlesTreeDao articlesTreeDao) {
        this.articlesTreeReactive = articlesTreeReactive;
        this.articlesTreeDao = articlesTreeDao;
    }

    @Override
    public Mono<ArticleFamilyCrud> readByReference(String reference) {
        return this.articlesTreeReactive.findFirstByReference(reference)
                .map(ArticlesTreeEntity::toDto);
    }

    @Override
    public Mono<Void> deleteByReference(String reference) {
        return this.articlesTreeReactive.deleteByReference(reference);
    }

    @Override
    public Mono<ArticleFamilyCrud> createCompose(ArticleFamilyCrud articleFamilyCrud) {
        return this.assertReferenceNotExist(articleFamilyCrud.getReference())
                .then(this.assertReferenceExist(articleFamilyCrud.getParentReference()))
                .flatMap(parentArticlesTreeEntity -> {
                    CompositeArticleEntity compositeArticleEntity = new CompositeArticleEntity(articleFamilyCrud.getReference(), articleFamilyCrud.getTreeType(), articleFamilyCrud.getDescription());
                    return this.articlesTreeReactive.save(compositeArticleEntity)
                            .flatMap(x -> {
                                parentArticlesTreeEntity.add(x);
                                return this.articlesTreeReactive.save(parentArticlesTreeEntity);
                            });
                })
                .map(ArticlesTreeEntity::toDto);
    }

    private Mono< ArticlesTreeEntity > assertReferenceExist(String reference) {
        return this.articlesTreeReactive.findFirstByReference(reference)
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Non existent article-family with reference: " + reference))
                );
    }

    private Mono< Void > assertReferenceNotExist(String reference) {
        return this.articlesTreeReactive.findFirstByReference(reference)
                .flatMap(articleEntity -> Mono.error(
                        new ConflictException("Article-Family reference already exists : " + reference)
                ));
    }
}
