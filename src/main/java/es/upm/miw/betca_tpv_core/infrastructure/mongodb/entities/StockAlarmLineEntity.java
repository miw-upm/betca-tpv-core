package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.StockAlarmLine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAlarmLineEntity {
    @DBRef(lazy = true)
    private ArticleEntity articleEntity;
    private Integer warning;
    private Integer critical;

    public StockAlarmLineEntity(StockAlarmLine stockAlarmLine) {
        BeanUtils.copyProperties(stockAlarmLine, this);
    }

    public StockAlarmLine toStockAlarmLine() {
        StockAlarmLine stockAlarmLine = new StockAlarmLine();
        BeanUtils.copyProperties(this, stockAlarmLine);
        stockAlarmLine.setBarcode(this.articleEntity.getBarcode());
        stockAlarmLine.setStock(this.articleEntity.getStock());
        return stockAlarmLine;
    }
}
