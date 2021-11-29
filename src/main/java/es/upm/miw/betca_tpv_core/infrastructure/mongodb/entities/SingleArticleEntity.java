package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.TreeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

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

}
