package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticleFamilyCrudPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ArticleBarcodeWithParentReferenceDto;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticlesTreeReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous.ArticlesTreeDao;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticleEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticlesTreeEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CompositeArticleEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.SingleArticleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class FamilyArticleCrudPersistenceMongodb implements ArticleFamilyCrudPersistence {
    ArticlesTreeReactive articlesTreeReactive;
    ArticleReactive articleReactive;
    ArticlesTreeDao articlesTreeDao;

    @Autowired
    public FamilyArticleCrudPersistenceMongodb(ArticlesTreeReactive articlesTreeReactive, ArticleReactive articleReactive, ArticlesTreeDao articlesTreeDao) {
        this.articlesTreeReactive = articlesTreeReactive;
        this.articleReactive = articleReactive;
        this.articlesTreeDao = articlesTreeDao;
    }

    @Override
    public Mono<ArticleFamilyCrud> readByReference(String reference) {
        return this.articlesTreeReactive.findFirstByReference(reference)
                .map(ArticlesTreeEntity::toDto);
    }

    @Override
    public Mono<Void> deleteComposeArticleFamily(String reference) {
        return this.assertReferenceExist(reference)
                .then(this.articlesTreeReactive.deleteByReference(reference));
    }

    @Override
    public Mono<ArticleFamilyCrud> createComposeArticleFamily(ArticleFamilyCrud articleFamilyCrud) {
        return this.assertReferenceNotExist(articleFamilyCrud.getReference())
                .then(this.assertReferenceExist(articleFamilyCrud.getParentReference()))
                .flatMap(parentArticlesTreeEntity -> {
                    CompositeArticleEntity compositeArticleEntity = new CompositeArticleEntity(articleFamilyCrud.getReference(), articleFamilyCrud.getTreeType(), articleFamilyCrud.getDescription());
                    return this.articlesTreeReactive.save(compositeArticleEntity)
                            .flatMap(compositeArticleEntityFromBd -> {
                                parentArticlesTreeEntity.add(compositeArticleEntityFromBd);
                                return this.articlesTreeReactive.save(parentArticlesTreeEntity);
                            });
                })
                .map(ArticlesTreeEntity::toDto);
    }

    @Override
    public Mono<ArticleFamilyCrud> addArticleToArticleFamily(ArticleBarcodeWithParentReferenceDto articleBarcodeWithParentReferenceDto) {
        return this.assertReferenceExist(articleBarcodeWithParentReferenceDto.getParentReference())
                .flatMap(compositeArticle -> this.assertBarcodeNotExist(articleBarcodeWithParentReferenceDto.getBarcode())
                        .flatMap(articleEntity -> {
                            SingleArticleEntity singleArticleEntity = new SingleArticleEntity(articleEntity);
                            return this.articlesTreeReactive.save(singleArticleEntity);
                        })
                        .flatMap(articleSaveEntity -> {
                            compositeArticle.add(articleSaveEntity);
                            return this.articlesTreeReactive.save(compositeArticle);
                        }))
                .map(ArticlesTreeEntity::toDto);



    }

    @Override
    public Mono<Void> deleteSingleArticle(ArticleBarcodeWithParentReferenceDto articleBarcodeWithParentReferenceDto) {
        return this.assertReferenceExist(articleBarcodeWithParentReferenceDto.getParentReference())
                .flatMap(parentArticlesTreeEntity -> this.assertBarcodeNotExist(articleBarcodeWithParentReferenceDto.getBarcode())
                        .flatMap(articleEntity -> {
                            if(!this.isSingleInComposite(parentArticlesTreeEntity, new SingleArticleEntity(articleEntity))){
                                return Mono.error(new NotFoundException("Non existent article with barcode: " + articleEntity.getBarcode() + "dentro de "+ parentArticlesTreeEntity.getReference()));
                            }
                            parentArticlesTreeEntity.remove(new SingleArticleEntity(articleEntity));
                            return this.articlesTreeReactive.save(parentArticlesTreeEntity);
                        }))
                .then();
    }

    private Mono<ArticlesTreeEntity> assertReferenceExist(String reference) {
        return this.articlesTreeReactive.findFirstByReference(reference)
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Non existent article-family with reference: " + reference))
                );
    }

    private Mono<Void> assertReferenceNotExist(String reference) {
        return this.articlesTreeReactive.findFirstByReference(reference)
                .flatMap(articleEntity -> Mono.error(
                        new ConflictException("Article-Family reference already exists : " + reference)
                ));
    }

    private Mono<ArticleEntity> assertBarcodeNotExist(String barcode) {
        return this.articleReactive.findByBarcode(barcode)
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Non existent article with barcode: " + barcode))
                );
    }

    private Boolean isSingleInComposite(ArticlesTreeEntity compositeArticleEntity, SingleArticleEntity singleArticleEntity) {
        return compositeArticleEntity
                .contents()
                .stream()
                .anyMatch(element -> element.getReference().equals(singleArticleEntity.getReference()));
    }

}
