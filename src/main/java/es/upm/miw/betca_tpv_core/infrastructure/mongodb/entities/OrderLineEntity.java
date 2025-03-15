package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.OrderLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineEntity {
    private String articleBarcode;
    private Integer requiredAmount;
    private Integer finalAmount;

    public OrderLine toOrderLine(){
        OrderLine orderLine = new OrderLine();
        orderLine.setArticleBarcode(articleBarcode);
        orderLine.setRequiredAmount(requiredAmount);
        orderLine.setFinalAmount(finalAmount);
        return orderLine;
    }

}
