package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticleFamilyCrudPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticlesTreeReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticleEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticlesTreeEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CompositeArticleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
public class FamilyArticleCrudPersistenceMongodb implements ArticleFamilyCrudPersistence {
    ArticlesTreeReactive articlesTreeReactive;

    @Autowired
    public FamilyArticleCrudPersistenceMongodb(ArticlesTreeReactive articlesTreeReactive) {
        this.articlesTreeReactive = articlesTreeReactive;
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
    public Mono<ArticleFamilyCrud> createCompose(ArticleFamilyCrud articleFamilyCrud,String parentReference) {
        return this.assertReferenceNotExist(articleFamilyCrud.getReference())
                .then(this.assertReferenceExist(parentReference))
                .map(articlesTreeEntity -> {
                    CompositeArticleEntity compositeArticleEntity = new CompositeArticleEntity(articleFamilyCrud.getReference()
                            , articleFamilyCrud.getTreeType()
                            , articleFamilyCrud.getDescription());
                    compositeArticleEntity.setId(UUID.randomUUID().toString());
                    articlesTreeEntity.add(compositeArticleEntity);
                    return compositeArticleEntity;
                })
                .flatMap(this.articlesTreeReactive::save)
                .map(ArticlesTreeEntity::toDto);




    }

    /*@Override
    public Mono<Article> create(Article article) {
        return this.assertBarcodeNotExist(article.getBarcode())
                .then(Mono.justOrEmpty(article.getProviderCompany()))
                .flatMap(providerCompany -> this.providerReactive.findByCompany(article.getProviderCompany())
                        .switchIfEmpty(Mono.error(
                                new NotFoundException("Non existent company: " + article.getProviderCompany())
                        ))
                )
                .map(providerEntity -> new ArticleEntity(article, providerEntity))
                .switchIfEmpty(Mono.just(new ArticleEntity(article, null)))
                .flatMap(this.articleReactive::save)
                .map(ArticleEntity::toArticle);
    }*/

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
