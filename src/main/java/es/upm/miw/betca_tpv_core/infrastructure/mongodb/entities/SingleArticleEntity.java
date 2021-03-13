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
        articleFamilyCrud.setDescription(this.getDescription());
        articleFamilyCrud.setTreeType(this.getTreeType());
        articleFamilyCrud.setReference(this.getReference());
        return articleFamilyCrud;
    }

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
