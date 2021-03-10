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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private LocalDate creationDate;
    private LocalDate expiryDate;
    private BigDecimal discount;

    @DBRef(lazy = true)
    private List<ArticleEntity> articleEntityList;

    public OfferEntity(Offer offer) {
        BeanUtils.copyProperties(offer, this);
        this.articleEntityList = new ArrayList<>();
    }

    public void add(ArticleEntity articleEntity) {
        this.articleEntityList.add(articleEntity);
    }

    public Offer toOffer() {
        Offer offer = new Offer();
        BeanUtils.copyProperties(this, offer);
        offer.setArticleBarcodes(this.getArticleEntityList().stream()
                .map(ArticleEntity::getBarcode)
                .toArray(String[]::new)
        );
        return offer;
    }
}
