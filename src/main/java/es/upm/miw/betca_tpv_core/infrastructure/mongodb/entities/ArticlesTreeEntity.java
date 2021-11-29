package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.TreeType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "articlesTree")
public abstract class ArticlesTreeEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String reference;
    private TreeType treeType;

    protected ArticlesTreeEntity(String reference, TreeType treeType) {
        this.reference = reference;
        this.treeType = treeType;
    }

    public abstract String getDescription();

    public abstract void add(ArticlesTreeEntity articlesTreeEntity);

    public abstract void remove(ArticlesTreeEntity articlesTreeEntity);

    public abstract List< ArticlesTreeEntity > contents();
}
