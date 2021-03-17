package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ArticleBarcodeWithParentReferenceDto;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ArticleFamilyCrudPersistence {
    Mono<ArticleFamilyCrud> readByReference(String reference);
    Mono<Void> deleteComposeArticleFamily(String reference);
    Mono<ArticleFamilyCrud> createComposeArticleFamily(ArticleFamilyCrud articleFamilyCrud);
    Mono<ArticleFamilyCrud> addArticleToArticleFamily(ArticleBarcodeWithParentReferenceDto articleBarcodeWithParentReferenceDto);
    Mono<Void> deleteSingleArticle(ArticleBarcodeWithParentReferenceDto articleBarcodeWithParentReferenceDto);
}
