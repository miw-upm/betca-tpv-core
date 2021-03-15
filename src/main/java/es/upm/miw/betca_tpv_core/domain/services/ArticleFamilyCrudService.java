package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticleFamilyCrudPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ArticleBarcodeWithParentReferenceDto;
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

    public Mono<Void> deleteComposeArticleFamily(String reference) {
        return this.articleFamilyCrudPersistence.deleteComposeArticleFamily(reference);
    }
    public Mono<ArticleFamilyCrud> createComposeArticleFamily(ArticleFamilyCrud articleFamilyCrud) {
        return this.articleFamilyCrudPersistence.createComposeArticleFamily(articleFamilyCrud);
    }

    public Mono<ArticleFamilyCrud> addArticleToArticleFamily(ArticleBarcodeWithParentReferenceDto articleBarcodeWithParentReferenceDto) {
        return this.articleFamilyCrudPersistence.addArticleToArticleFamily(articleBarcodeWithParentReferenceDto);
    }

    public Mono<Void> deleteSingleArticle(ArticleBarcodeWithParentReferenceDto articleBarcodeWithParentReferenceDto) {
        return this.articleFamilyCrudPersistence.deleteSingleArticle(articleBarcodeWithParentReferenceDto);
    }
}
