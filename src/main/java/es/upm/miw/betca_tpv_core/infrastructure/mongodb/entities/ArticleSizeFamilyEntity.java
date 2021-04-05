package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.ArticleSizeFamily;
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
public class ArticleSizeFamilyEntity {

    @Id
    private String id;
    @Indexed(unique = true)
    private String barcode;
    private String description;
    private Tax tax;
    private BigDecimal retailPrice;
//    private Integer type;
//    private Map<String, Integer> size;
    private LocalDateTime registrationDate;
    private String providerCompany;

    @DBRef(lazy = true)
    private ProviderEntity providerEntity;

    public ArticleSizeFamilyEntity(ArticleSizeFamily articleSizeFamily, ProviderEntity providerEntity) {
        BeanUtils.copyProperties(articleSizeFamily, this);
        this.providerEntity = providerEntity;
    }


    public ArticleSizeFamily toArticleSizeFamily() {
        ArticleSizeFamily articleSizeFamily = new ArticleSizeFamily();
        BeanUtils.copyProperties(this, articleSizeFamily);
        if (Objects.nonNull(this.getProviderEntity())) {
            articleSizeFamily.setProviderCompany(this.getProviderEntity().getCompany());
        }
        return articleSizeFamily;
    }
}