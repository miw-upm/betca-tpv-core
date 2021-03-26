package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.OrderLine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class OrderLineEntity {
    @Id
    private String id;
    @DBRef(lazy = true)
    private ArticleEntity articleEntity;
    private Integer requireAmount;
    private Integer finalAmount;

    public OrderLineEntity(OrderLine orderLine, ArticleEntity articleEntity) {
        BeanUtils.copyProperties(orderLine, this);
        this.articleEntity = articleEntity;
    }

    public OrderLine toOrderLine() {
        OrderLine orderLine = new OrderLine();
        BeanUtils.copyProperties(this, orderLine);
        if (Objects.nonNull(this.getArticleEntity())) {
            orderLine.setArticleBarcode(this.articleEntity.getReference());
        }
        return orderLine;
    }
}
