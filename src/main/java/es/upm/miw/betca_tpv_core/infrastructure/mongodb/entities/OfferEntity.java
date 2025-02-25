package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Offer;
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
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class OfferEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String reference;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime expiryDate;
    private BigDecimal discount;
    @DBRef
    private List<ArticleEntity> articleEntities;

    public OfferEntity(Offer offer, List<ArticleEntity> articleEntities) {
        BeanUtils.copyProperties(offer, this);
        this.articleEntities = articleEntities;
    }

    public Offer toOffer() {
        Offer offer = new Offer();
        BeanUtils.copyProperties(this, offer);
        if (Objects.nonNull(this.articleEntities) && !this.articleEntities.isEmpty()) {
            offer.setArticleList(this.articleEntities.stream()
                    .map(ArticleEntity::toArticle)
                    .toList());
        }
        return offer;
    }

    public Offer toOfferWithoutArticles() {
        Offer offer = new Offer();
        BeanUtils.copyProperties(this, offer);
        return offer;
    }
}
