package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarmLine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class StockAlarmEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private String description;
    private Integer warning;
    private Integer critical;
    private List<StockAlarmLineEntity> stockAlarmLineEntities;

    public StockAlarmEntity(StockAlarm stockAlarm) {
        BeanUtils.copyProperties(stockAlarm, this);
    }

    public StockAlarm toStockAlarm() {
        StockAlarm stockAlarm = new StockAlarm();
        BeanUtils.copyProperties(this, stockAlarm);
        if(Objects.nonNull(this.stockAlarmLineEntities) && !this.stockAlarmLineEntities.isEmpty()) {
            stockAlarm.setStockAlarmLines(this.stockAlarmLineEntities.stream()
                    .map(StockAlarmLineEntity::toStockAlarmLine)
                    .toList());
        }
        return stockAlarm;
    }
}
