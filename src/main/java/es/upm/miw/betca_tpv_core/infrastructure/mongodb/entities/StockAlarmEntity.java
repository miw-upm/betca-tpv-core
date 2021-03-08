package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @Singular("alarmLine")
    private List<StockAlarmLineEntity> stockAlarmLineEntityList;

    public StockAlarmEntity(StockAlarm stockAlarm) {
        BeanUtils.copyProperties(stockAlarm, this);
        this.stockAlarmLineEntityList = new ArrayList<>();
    }

    public void add(StockAlarmLineEntity stockAlarmLineEntity) {
        this.stockAlarmLineEntityList.add(stockAlarmLineEntity);
    }

    public StockAlarm toStockAlarm() {
        StockAlarm stockAlarm = new StockAlarm();
        BeanUtils.copyProperties(this, stockAlarm);
        stockAlarm.setStockAlarmLines(this.getStockAlarmLineEntityList().stream()
                .map(StockAlarmLineEntity::toStockAlarmLine)
                .collect(Collectors.toList())
        );
        return stockAlarm;
    }

}
