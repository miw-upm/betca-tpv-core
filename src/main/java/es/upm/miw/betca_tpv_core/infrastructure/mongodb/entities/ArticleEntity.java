package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.Tax;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class ArticleEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String barcode;
    @Indexed(unique = true)
    private String reference;
    private String description;
    private BigDecimal retailPrice;
    private Integer stock;
    private Tax tax;
    private LocalDateTime registrationDate;
    private Boolean discontinued;

    @DBRef(lazy = true)
    private ProviderEntity providerEntity;

    public ArticleEntity(Article article, ProviderEntity providerEntity) {
        BeanUtils.copyProperties(article, this);
        this.providerEntity = providerEntity;
    }

    public Article toArticle() {
        Article article = new Article();
        BeanUtils.copyProperties(this, article);
        if (Objects.nonNull(this.getProviderEntity())) {
            article.setProviderCompany(this.getProviderEntity().getCompany());
        }
        return article;
    }

}
