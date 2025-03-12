package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarmLine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class StockAlarmLineEntity {
    @Id
    private String id;
    private Article article;
    private Integer warning;
    private Integer critical;

    public StockAlarmLineEntity(StockAlarmLine stockAlarmLine) {
        BeanUtils.copyProperties(stockAlarmLine, this);
    }

    public StockAlarmLine toStockAlarmLine() {
        StockAlarmLine stockAlarmLine = new StockAlarmLine();
        BeanUtils.copyProperties(this, stockAlarmLine);
        return stockAlarmLine;
    }
}