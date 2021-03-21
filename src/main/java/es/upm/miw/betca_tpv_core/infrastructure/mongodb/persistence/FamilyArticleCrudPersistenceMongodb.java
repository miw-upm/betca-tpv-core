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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

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
    public Mono<Void> delete(String id) {
        return this.assertIdExist(id)
                .then(this.articlesTreeReactive.deleteById(id));
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
                            if(this.isSingleInComposite(compositeArticle, new SingleArticleEntity(articleEntity))){
                                return Mono.error(new NotFoundException("Already existent article with barcode: " + articleEntity.getBarcode() + " in "+ compositeArticle.getReference()));
                            }
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
    public Mono<ArticleFamilyCrud> editComposeArticleFamily(ArticleFamilyCrud articleFamilyCrud) {
        return this.assertReferenceNotExist(articleFamilyCrud.getReference(),articleFamilyCrud.getId())
                .then(this.articlesTreeReactive.findById(articleFamilyCrud.getId()))
                .flatMap(articlesTreeEntity -> {
                    this.myCopyProperties(articleFamilyCrud,articlesTreeEntity);
                    return this.articlesTreeReactive.save(articlesTreeEntity);
                })
                .map(ArticlesTreeEntity::toDto);
    }

    private Mono<ArticlesTreeEntity> assertReferenceExist(String reference) {
        return this.articlesTreeReactive.findFirstByReference(reference)
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Non existent article-family with reference: " + reference))
                );
    }

    private Mono<ArticlesTreeEntity> assertIdExist(String id) {
        return this.articlesTreeReactive.findById(id)
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Non existent article-family"))
                );
    }

    private Mono<Void> assertReferenceNotExist(String reference) {
        return this.articlesTreeReactive.findFirstByReference(reference)
                .flatMap(articleEntity -> Mono.error(
                        new ConflictException("Article-Family reference already exists : " + reference)
                ));
    }

    private Mono<Void> assertReferenceNotExist(String reference, String id) {
        return this.articlesTreeReactive.findFirstByReference(reference)
                .flatMap(articleEntity -> {
                    if(!articleEntity.getId().equals(id))
                    return Mono.error(
                            new ConflictException("Article-Family reference already exists : " + reference)
                    );
                    else return Mono.empty();
                });
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

    private String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    private void myCopyProperties(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

}
