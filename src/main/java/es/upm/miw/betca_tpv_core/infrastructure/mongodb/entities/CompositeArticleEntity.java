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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Document(collection = "articlesTree")
public class CompositeArticleEntity extends ArticlesTreeEntity {
    private String description;
    @DBRef(lazy = true)
    private List< ArticlesTreeEntity > articlesTreeEntityList;

    public CompositeArticleEntity(String reference, TreeType treeType, String description) {
        super(reference, treeType);
        this.description = description;
        this.articlesTreeEntityList = new ArrayList<>();
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void add(ArticlesTreeEntity articlesTreeEntityList) {
        this.articlesTreeEntityList.add(articlesTreeEntityList);
    }


    @Override
    public void remove(ArticlesTreeEntity articlesTreeEntityList) {
        this.articlesTreeEntityList.remove(articlesTreeEntityList);
    }

    @Override
    public ArticleFamilyCrud toDto() {
        ArticleFamilyCrud articleFamilyCrud = new ArticleFamilyCrud();

        List<ArticleFamilyCrud> articleFamilyCrudList = this.articlesTreeEntityList.stream()
                .map(ArticlesTreeEntity::toDto)
                .collect(Collectors.toList());

        BeanUtils.copyProperties(this, articleFamilyCrud);
        articleFamilyCrud.setArticleFamilyCrudList(articleFamilyCrudList);
        return articleFamilyCrud;

    }

    @Override
    public ArticleFamilyView toArticleFamilyViewDto() {
        return ArticleFamilyView.builder()
                .reference(this.getReference())
                .description(this.getDescription())
                .treeType(this.getTreeType())
                .build();
    }

    @Override
    public List< ArticlesTreeEntity > contents() {
        return this.articlesTreeEntityList;
    }

    @Override
    public String toString() {
        return "CompositeArticleEntity{" +
                "description='" + description + '\'' +
                ", articlesTreeEntityList=" + articlesTreeEntityList +
                "} " + super.toString();
    }
}
