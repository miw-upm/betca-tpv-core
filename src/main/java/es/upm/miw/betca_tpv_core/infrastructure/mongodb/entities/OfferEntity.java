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
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Document
@Builder
@AllArgsConstructor
public class OfferEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String reference;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime expiryDate;
    private BigDecimal discount;

    @DBRef(lazy = true)
    private List<ArticleEntity> articleEntityList;

    public OfferEntity(Offer offer, List<ArticleEntity> articleEntityList) {
        BeanUtils.copyProperties(offer, this);
        this.articleEntityList = articleEntityList;
    }

    public void add(ArticleEntity articleEntity) {
        this.articleEntityList.add(articleEntity);
    }

    public Offer toOffer() {
        Offer offer = new Offer();
        BeanUtils.copyProperties(this, offer);
        offer.setArticleBarcodeList(this.getArticleEntityList().stream()
                .map(ArticleEntity::getBarcode)
                .collect(Collectors.toList()));
        return offer;
    }
}
