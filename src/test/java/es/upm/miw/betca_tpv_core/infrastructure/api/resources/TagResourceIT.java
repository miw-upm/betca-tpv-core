package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.Provider;
import es.upm.miw.betca_tpv_core.domain.model.Tag;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.TagResource.SEARCH;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.TagResource.TAGS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RestTestConfig
public class TagResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {
        Provider provider = Provider.builder().company("companyTestTag").nif("qwe1").phone("333333333").build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ProviderResource.PROVIDERS)
                .body(Mono.just(provider), Provider.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Provider.class)
                .value(Assertions::assertNotNull)
                .value(returnProvider -> {
                    assertEquals("companyTestTag", returnProvider.getCompany());
                    assertEquals("qwe1", returnProvider.getNif());
                    assertEquals("333333333", returnProvider.getPhone());
                    assertTrue(returnProvider.getActive());
                });
        Article article1 = Article.builder().barcode("tagTest001").description("description").retailPrice(BigDecimal.ONE)
                .providerCompany("companyTestTag").tax(null).build();
        Article article2 = Article.builder().barcode("tagTest002").description("description").retailPrice(BigDecimal.ONE)
                .providerCompany("companyTestTag").tax(null).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ArticleResource.ARTICLES)
                .body(Mono.just(article1), Article.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Article.class)
                .value(Assertions::assertNotNull)
                .value(returnArticle -> {
                    assertEquals("tagTest001", returnArticle.getBarcode());
                    assertEquals("description", returnArticle.getDescription());
                    assertEquals("companyTestTag", returnArticle.getProviderCompany());
                });
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ArticleResource.ARTICLES)
                .body(Mono.just(article2), Article.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Article.class)
                .value(Assertions::assertNotNull)
                .value(returnArticle -> {
                    assertEquals("tagTest002", returnArticle.getBarcode());
                    assertEquals("description", returnArticle.getDescription());
                    assertEquals("companyTestTag", returnArticle.getProviderCompany());
                });
        Tag tag = Tag.builder().name("tagTest1").group("tagGroup1").description("description").articleList(List.of(article1, article2)).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(TAGS)
                .body(Mono.just(tag), Tag.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Tag.class)
                .value(Assertions::assertNotNull)
                .value(returnTag -> {
                    System.out.println(">>>>> Test:: returnTag:" + returnTag);
                    assertEquals("tagTest1", returnTag.getName());
                    assertEquals("tagGroup1", returnTag.getGroup());
                    assertEquals("description", returnTag.getDescription());
                    assertTrue(returnTag.getArticleList().size() == 2);
                });
    }

    @Test
    void testUpdate() {
        Provider provider = Provider.builder().company("companyTestTag2").nif("qwe2").phone("333333333").build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ProviderResource.PROVIDERS)
                .body(Mono.just(provider), Provider.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Provider.class)
                .value(Assertions::assertNotNull)
                .value(returnProvider -> {
                    assertEquals("companyTestTag2", returnProvider.getCompany());
                    assertEquals("qwe2", returnProvider.getNif());
                    assertEquals("333333333", returnProvider.getPhone());
                    assertTrue(returnProvider.getActive());
                });
        Article article1 = Article.builder().barcode("tagTest0012").description("description").retailPrice(BigDecimal.ONE)
                .providerCompany("companyTestTag2").tax(null).build();
        Article article2 = Article.builder().barcode("tagTest0022").description("description").retailPrice(BigDecimal.ONE)
                .providerCompany("companyTestTag2").tax(null).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ArticleResource.ARTICLES)
                .body(Mono.just(article1), Article.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Article.class)
                .value(Assertions::assertNotNull)
                .value(returnArticle -> {
                    assertEquals("tagTest0012", returnArticle.getBarcode());
                    assertEquals("description", returnArticle.getDescription());
                    assertEquals("companyTestTag2", returnArticle.getProviderCompany());
                });
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ArticleResource.ARTICLES)
                .body(Mono.just(article2), Article.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Article.class)
                .value(Assertions::assertNotNull)
                .value(returnArticle -> {
                    assertEquals("tagTest0022", returnArticle.getBarcode());
                    assertEquals("description", returnArticle.getDescription());
                    assertEquals("companyTestTag2", returnArticle.getProviderCompany());
                });
        Tag tag = Tag.builder().name("tagTest2").group("tagGroup2").description("description").articleList(List.of(article1, article2)).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(TAGS + "/name2")
                .body(Mono.just(tag), Tag.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Tag.class)
                .value(Assertions::assertNotNull)
                .value(returnTag -> {
                    System.out.println(">>>>> Test:: returnTag:" + returnTag);
                    assertEquals("tagTest2", returnTag.getName());
                    assertEquals("tagGroup2", returnTag.getGroup());
                    assertEquals("description", returnTag.getDescription());
                });
    }

    @Test
    void testRead() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(TAGS + "/name1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Tag.class)
                .value(Assertions::assertNotNull)
                .value(returnTag -> {
                    assertEquals("name1", returnTag.getName());
                    assertEquals("group1", returnTag.getGroup());
                });
    }

    @Test
    void testSearch() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TAGS + SEARCH)
                        .queryParam("group1", "group1")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Tag.class)
                .value(Assertions::assertNotNull)
                .value(tags -> {
                    assertTrue(tags.stream()
                            .anyMatch(tag ->
                                    tag.getGroup().equals("group1")
                            ));
                });
    }
}
