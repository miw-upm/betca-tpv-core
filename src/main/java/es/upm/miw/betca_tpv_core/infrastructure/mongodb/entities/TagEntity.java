package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class TagEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private String group;
    private String description;

    @DBRef(lazy = true)
    private List<ArticleEntity> articleEntityList;

    public TagEntity(Tag tag) {
        BeanUtils.copyProperties(tag, this);
        this.articleEntityList = new ArrayList<ArticleEntity>();
    }

    public Tag toTag() {
        Tag tag = new Tag();
        BeanUtils.copyProperties(this, tag);
        tag.setArticleList(this.getArticleEntityList().stream().map(ArticleEntity::toArticle).collect(Collectors.toList()));
        return tag;
    }
}
