package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Salespeople;
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
public class SalespeopleEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String salesperson;
    private LocalDate salesDate;
    private Integer numArticle;
    private BigDecimal finalValue;

    @DBRef(lazy = true)
    private List<ArticleEntity> articleEntityList;
    @DBRef(lazy = true)
    private List<TicketEntity> ticketEntityList;

    public SalespeopleEntity(Salespeople salespeople, List<ArticleEntity> articleEntityList, List<TicketEntity> ticketEntityList) {
        BeanUtils.copyProperties(salespeople, this);
        this.articleEntityList = articleEntityList;
        this.ticketEntityList = ticketEntityList;
    }

    public Salespeople toSalespeople() {
        Salespeople salespeople = new Salespeople();
        BeanUtils.copyProperties(this, salespeople);
        salespeople.setArticleBarcodes(this.getArticleEntityList().stream()
                .map(ArticleEntity::getBarcode)
                .toArray(String[]::new));
        salespeople.setTicketBarcodes(this.getTicketEntityList().stream()
                .map(TicketEntity::getId)
                .toArray(String[]::new));
        return salespeople;
    }
}
