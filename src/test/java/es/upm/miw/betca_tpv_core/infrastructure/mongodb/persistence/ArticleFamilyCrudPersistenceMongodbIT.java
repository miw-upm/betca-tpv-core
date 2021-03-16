package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import es.upm.miw.betca_tpv_core.domain.model.TreeType;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ArticleBarcodeWithParentReferenceDto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ArticleFamilyCrudPersistenceMongodbIT {

    @Autowired
    private FamilyArticleCrudPersistenceMongodb familyArticleCrudPersistenceMongodb;

    @Test
    @Order(1)
    void testWhenAddArticleToParentReferenceThenReturnParentWithNewArticle() {
        StepVerifier
                .create(this.familyArticleCrudPersistenceMongodb.addArticleToArticleFamily(ArticleBarcodeWithParentReferenceDto.builder().barcode("8400000000017").parentReference("Zz Polo").build()))
                .expectNextMatches(articleFamilyCrud -> {
                    assertEquals("Zz Polo", articleFamilyCrud.getReference());
                    assertNotNull(articleFamilyCrud.getArticleFamilyCrudList()
                            .stream()
                            .filter(articleFamily -> articleFamily.getReference().equals("zz-falda-T2"))
                            .findAny()
                            .orElse(null));
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    @Order(2)
    void testWhenDeleteArticleToParentReferenceThenReturn() {
        StepVerifier
                .create(this.familyArticleCrudPersistenceMongodb.deleteSingleArticle(ArticleBarcodeWithParentReferenceDto.builder().barcode("8400000000017").parentReference("Zz Polo").build()))
                .expectComplete()
                .verify();
    }

    @Test
    @Order(3)
    void testGivenNewCompositeArticleFamilyWhenAddToParentReferenceThenReturnParentWithNewComposite() {
        StepVerifier
                .create(this.familyArticleCrudPersistenceMongodb.createComposeArticleFamily(ArticleFamilyCrud.builder().reference("newCompose").treeType(TreeType.SIZES).parentReference("Zz").build()))
                .expectNextMatches(articleFamilyCrud -> {
                    assertEquals("Zz", articleFamilyCrud.getReference());
                    assertNotNull(articleFamilyCrud.getArticleFamilyCrudList()
                            .stream()
                            .filter(articleFamily -> articleFamily.getReference().equals("newCompose"))
                            .findAny()
                            .orElse(null));
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    @Order(4)
    void testWhenDeleteArticleFamilyByReferenceThenReturn() {
        StepVerifier
                .create(this.familyArticleCrudPersistenceMongodb.deleteComposeArticleFamily("newCompose"))
                .expectComplete()
                .verify();
    }

    @Test
    void readSingleArticleByReference() {
        StepVerifier
                .create(this.familyArticleCrudPersistenceMongodb.readByReference("zz-falda-T2"))
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
    void readComposeArticleByReference(){
        StepVerifier
                .create(this.familyArticleCrudPersistenceMongodb.readByReference("Zz Falda"))
                .expectNextMatches(articleFamilyDto -> {
                    assertEquals(2,articleFamilyDto.getArticleFamilyCrudList().size());
                    assertEquals("zz-falda-T2",articleFamilyDto.getArticleFamilyCrudList().get(0).getReference());
                    assertEquals("zz-falda-T4", articleFamilyDto.getArticleFamilyCrudList().get(1).getReference());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testGivenNewCompositeArticleFamilyWithExistentReferenceWhenAddToParentReferenceThenReturnConflictMonoError() {
        StepVerifier
                .create(this.familyArticleCrudPersistenceMongodb.createComposeArticleFamily(ArticleFamilyCrud.builder().reference("Zz").treeType(TreeType.SIZES).parentReference("Zz").build()))
                .expectErrorMatches(throwable -> throwable instanceof ConflictException &&
                        throwable.getMessage().equals("Conflict Exception. Article-Family reference already exists : Zz"))
                .verify();
    }

    @Test
    void testGivenNewCompositeArticleFamilyWhenAddToNotExistentParentReferenceThenReturnNotFoundMonoError() {
        StepVerifier
                .create(this.familyArticleCrudPersistenceMongodb.createComposeArticleFamily(ArticleFamilyCrud.builder().reference("reference").treeType(TreeType.SIZES).parentReference("notExistent").build()))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Not Found Exception. Non existent article-family with reference: notExistent"))
                .verify();
    }

}
