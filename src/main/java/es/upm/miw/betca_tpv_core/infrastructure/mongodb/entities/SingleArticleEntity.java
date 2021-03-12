package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyView;
import es.upm.miw.betca_tpv_core.domain.model.TreeType;
import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Document(collection = "articlesTree")
public class SingleArticleEntity extends ArticlesTreeEntity {
    @DBRef
    private ArticleEntity articleEntity;

    public SingleArticleEntity(ArticleEntity articleEntity) {
        super(articleEntity.getReference(), TreeType.ARTICLE);
        /*En el super si se pasa bien la referencia, pero cuando intentamos acceder
        a algún atributo de this.articleEntity obtenemos que this.articleEntity
        es null. Lo más raro es que esto en los test no sucede (ver test testReadByReference
        de ArticleFamilyViewPersistenceMongodbIT y test testFindByReferenceThenReturnArticlesFamily
        de ArticleFamilyViewResourceIT
        */
        this.articleEntity = articleEntity;
    }

    @Override
    public String getDescription() {
        return this.articleEntity.getDescription();
    }

    @Override
    public void add(ArticlesTreeEntity familyComponent) {
        // Do nothing
    }

    @Override
    public void remove(ArticlesTreeEntity familyComponent) {
        // Do nothing
    }

    @Override
    public List< ArticlesTreeEntity > contents() {
        return Collections.emptyList();
    }

    @Override
    public ArticleFamilyCrud toDto() {
        ArticleFamilyCrud articleFamilyCrud = new ArticleFamilyCrud();
        BeanUtils.copyProperties(this, articleFamilyCrud);
        return articleFamilyCrud;
    }

    /*
    Tal y como está en el método de abajo debería funcionar correctamente, pero
    no se está asignando correctamente la entidad ArticleEntity en el constructor
    de la hoja(SingleArticleEntity)
     */
    @Override
    public ArticleFamilyView toArticleFamilyViewDto() {
        return ArticleFamilyView.builder()
                .reference(this.getReference())
                //.description("zz-falda-T2")
                .description(this.getDescription())
                .treeType(this.getTreeType())
                //.barcode("8400000000017")
                .barcode(this.articleEntity.getBarcode())
                //.retailPrice(new BigDecimal(10))
                .retailPrice(this.articleEntity.getRetailPrice())
                .build();
    }

}
