package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ProviderReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.TagsReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticleEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ProviderEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TagsEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ArticlePersistenceMongodb implements ArticlePersistence {

    private final ProviderReactive providerReactive;
    private final ArticleReactive articleReactive;
    private final TagsReactive tagsReactive;

    @Autowired
    public ArticlePersistenceMongodb(ProviderReactive providerReactive, ArticleReactive articleReactive, TagsReactive tagsReactive) {
        this.providerReactive = providerReactive;
        this.articleReactive = articleReactive;
        this.tagsReactive = tagsReactive;
    }

    @Override
    public Mono<Article> create(Article article) {
        // Buscar proveedor por nombre de compañía
        Mono<ProviderEntity> providerMono = providerReactive.findByCompany(article.getProviderCompany())
                .switchIfEmpty(Mono.error(new NotFoundException("Provider not found")));

        // Procesar y crear los tags si no existen
        Mono<List<TagsEntity>> tagsMono = Flux.fromIterable(article.getTagIds())
                .flatMap(tagName ->
                        tagsReactive.findByName(tagName)
                                .switchIfEmpty(tagsReactive.save(
                                        TagsEntity.builder()
                                                .name(tagName)
                                                .group("default")
                                                .description("default")
                                                .build()
                                ))
                )
                .collectList();

        // Combinar proveedor y tags para construir y guardar el artículo
        return Mono.zip(providerMono, tagsMono)
                .map(tuple -> {
                    ProviderEntity provider = tuple.getT1();
                    List<TagsEntity> tags = tuple.getT2();

                    ArticleEntity articleEntity = new ArticleEntity();
                    BeanUtils.copyProperties(article, articleEntity);
                    articleEntity.setProviderEntity(provider);
                    articleEntity.setTags(tags);
                    return articleEntity;
                })
                .flatMap(articleReactive::save)
                .map(savedEntity -> {
                    Article result = new Article();
                    BeanUtils.copyProperties(savedEntity, result);

                    result.setProviderCompany(savedEntity.getProviderEntity().getCompany());

                    // Asignamos los nombres de los tags en orden inverso
                    List<String> tagNames = savedEntity.getTags().stream()
                            .map(TagsEntity::getName)
                            .collect(Collectors.toList());
                    Collections.reverse(tagNames); // Invertimos el orden
                    result.setTags(tagNames);

                    return result;
                });
    }


    @Override
    public Mono<Article> readByBarcode(String barcode) {
        return this.articleReactive.findByBarcode(barcode)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent article barcode: " + barcode)))
                .map(ArticleEntity::toArticle);
    }

    @Override
    public Mono<Article> update(String barcode, Article article) {
        Mono<ArticleEntity> articleEntityMono;
        if (!barcode.equals(article.getBarcode())) {
            articleEntityMono = this.assertBarcodeNotExist(article.getBarcode())
                    .then(this.articleReactive.findByBarcode(barcode));
        } else {
            articleEntityMono = this.articleReactive.findByBarcode(barcode);
        }
        return articleEntityMono
                .switchIfEmpty(Mono
                        .error(new NotFoundException("Non existent article barcode: " + barcode)))
                .flatMap(articleEntity -> {
                    BeanUtils.copyProperties(article, articleEntity);
                    return this.providerReactive.findByCompany(article.getProviderCompany())
                            .switchIfEmpty(Mono.error(
                                    new NotFoundException("Non existent company: " + article.getProviderCompany()))
                            )
                            .map(providerEntity -> {
                                articleEntity.setProviderEntity(providerEntity);
                                return articleEntity;
                            });
                })
                .flatMap(this.articleReactive::save)
                .map(ArticleEntity::toArticle);
    }

    @Override
    public Flux<Article> findByAnyNullField() {
        return this.articleReactive.findByProviderEntityIsNull()
                .map(ArticleEntity::toArticle);
    }

    @Override
    public Flux<Article> findByBarcodeAndDescriptionAndReferenceAndStockLessThanAndDiscontinuedNullSafe(
            String barcode, String description, String reference, Integer stock, Boolean discontinued) {
        return this.articleReactive.findByBarcodeAndDescriptionAndReferenceAndStockLessThanAndDiscontinuedNullSafe(
                        barcode, description, reference, stock, discontinued)
                .map(ArticleEntity::toArticle);
    }

    @Override
    public Mono<Article> readAndWriteStockByBarcodeAssured(String barcode, Integer stockIncrement) {
        return this.articleReactive.findByBarcode(barcode)
                .switchIfEmpty(Mono.error(new NotFoundException("Article: " + barcode)))
                .flatMap(article -> {
                    article.setStock(article.getStock() + stockIncrement);
                    return this.articleReactive.save(article);
                }).map(ArticleEntity::toArticle);
    }

    @Override
    public Flux<String> findByBarcodeAndNotDiscontinuedNullField(String barcode) {
        return this.articleReactive.findByBarcodeLikeAndNotDiscontinuedNullSafe(barcode)
                .map(ArticleEntity::getBarcode);
    }


    private Mono<Void> assertBarcodeNotExist(String barcode) {
        return this.articleReactive.findByBarcode(barcode)
                .flatMap(articleEntity -> Mono.error(
                        new ConflictException("Article Barcode already exists : " + barcode)
                ));
    }
}