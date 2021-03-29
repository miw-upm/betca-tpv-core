package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import es.upm.miw.betca_tpv_core.domain.model.TreeType;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ArticleBarcodeWithParentReferenceDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
class ArticleFamilyCrudPersistenceMongodbIT {

    @Autowired
    private ArticleFamilyCrudPersistenceMongodb articleFamilyCrudPersistenceMongodb;

    @Test
    void testWhenAddArticleToParentReferenceThenReturnParentWithNewArticle() {
        AtomicReference<String> idSingleArticle = new AtomicReference<>("");
        StepVerifier
                .create(this.articleFamilyCrudPersistenceMongodb.addArticleToArticleFamily(ArticleBarcodeWithParentReferenceDto.builder().barcode("8400000000017").parentReference("Zz Polo").build()))
                .expectNextMatches(articleFamilyCrud -> {
                    assertEquals("Zz Polo", articleFamilyCrud.getReference());
                    assertNotNull(getArticleByBarcode(articleFamilyCrud, "8400000000017"));
                    idSingleArticle.set(getArticleByBarcode(articleFamilyCrud, "8400000000017").getId());
                    return true;
                })
                .expectComplete()
                .verify();

        StepVerifier
                .create(this.articleFamilyCrudPersistenceMongodb.delete(idSingleArticle.get()))
                .expectComplete()
                .verify();
    }


    @Test
    void testGivenNewCompositeArticleFamilyWhenAddToParentReferenceThenReturnParentWithNewComposite() {
        AtomicReference<String> idComposeArticle = new AtomicReference<>("");

        StepVerifier
                .create(this.articleFamilyCrudPersistenceMongodb.createComposeArticleFamily(ArticleFamilyCrud.builder().reference("newCompose").treeType(TreeType.SIZES).parentReference("Zz").build()))
                .expectNextMatches(articleFamilyCrud -> {
                    assertEquals("Zz", articleFamilyCrud.getReference());
                    assertNotNull(getArticleByReference(articleFamilyCrud, "newCompose"));
                    idComposeArticle.set(getArticleByReference(articleFamilyCrud, "newCompose").getId());
                    return true;
                })
                .expectComplete()
                .verify();

        StepVerifier
                .create(this.articleFamilyCrudPersistenceMongodb.editComposeArticleFamily(ArticleFamilyCrud.builder().id(idComposeArticle.get()).description("New Description").build()))
                .expectNextMatches(articleFamilyCrud -> {
                    assertEquals("newCompose", articleFamilyCrud.getReference());
                    assertEquals("New Description", articleFamilyCrud.getDescription());
                    return true;
                })
                .expectComplete()
                .verify();

        StepVerifier
                .create(this.articleFamilyCrudPersistenceMongodb.delete(idComposeArticle.get()))
                .expectComplete()
                .verify();
    }

    @Test
    void readSingleArticleByReference() {
        StepVerifier
                .create(this.articleFamilyCrudPersistenceMongodb.readByReference("zz-falda-T2"))
                .expectNextMatches(articleFamilyDto -> {
                    assertEquals("zz-falda-T2", articleFamilyDto.getReference());
                    assertEquals("Zarzuela - Falda T2", articleFamilyDto.getDescription());
                    assertNotNull(articleFamilyDto.getArticleFamilyCrudList());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void readComposeArticleByReference() {
        StepVerifier
                .create(this.articleFamilyCrudPersistenceMongodb.readByReference("Zz Falda"))
                .expectNextMatches(articleFamilyDto -> {
                    assertEquals(2, articleFamilyDto.getArticleFamilyCrudList().size());
                    assertEquals("zz-falda-T2", articleFamilyDto.getArticleFamilyCrudList().get(0).getReference());
                    assertEquals("zz-falda-T4", articleFamilyDto.getArticleFamilyCrudList().get(1).getReference());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testGivenNewCompositeArticleFamilyWithExistentReferenceWhenAddToParentReferenceThenReturnConflictMonoError() {
        StepVerifier
                .create(this.articleFamilyCrudPersistenceMongodb.createComposeArticleFamily(ArticleFamilyCrud.builder().reference("Zz").treeType(TreeType.SIZES).parentReference("Zz").build()))
                .expectErrorMatches(throwable -> throwable instanceof ConflictException &&
                        throwable.getMessage().equals("Conflict Exception. Article-Family reference already exists : Zz"))
                .verify();
    }

    @Test
    void testGivenNewCompositeArticleFamilyWhenAddToNotExistentParentReferenceThenReturnNotFoundMonoError() {
        StepVerifier
                .create(this.articleFamilyCrudPersistenceMongodb.createComposeArticleFamily(ArticleFamilyCrud.builder().reference("reference").treeType(TreeType.SIZES).parentReference("notExistent").build()))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Not Found Exception. Non existent article-family with reference: notExistent"))
                .verify();
    }

    private ArticleFamilyCrud getArticleByBarcode(ArticleFamilyCrud articleFamilyCrud, String barcode) {
        return articleFamilyCrud.getArticleFamilyCrudList()
                .stream()
                .filter(articleFamily -> articleFamily.getBarcode().equals(barcode))
                .findAny()
                .orElse(null);
    }

    private ArticleFamilyCrud getArticleByReference(ArticleFamilyCrud articleFamilyCrud, String reference) {
        return articleFamilyCrud.getArticleFamilyCrudList()
                .stream()
                .filter(articleFamily -> articleFamily.getReference().equals(reference))
                .findAny()
                .orElse(null);
    }
}
