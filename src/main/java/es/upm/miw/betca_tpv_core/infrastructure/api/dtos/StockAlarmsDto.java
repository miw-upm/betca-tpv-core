package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import es.upm.miw.betca_tpv_core.domain.model.StockAlarmLine;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class StockAlarmsDto {
    private List<StockAlarmLine> warningAlarms;
    private List<StockAlarmLine> criticalAlarms;

    public StockAlarmsDto() {
        this.warningAlarms = new ArrayList<>();
        this.criticalAlarms = new ArrayList<>();
    }

    public void addWarning(StockAlarmLine stockAlarmLine) {
        this.warningAlarms.add(stockAlarmLine);
    }

    public void addCritical(StockAlarmLine stockAlarmLine) {
        this.criticalAlarms.add(stockAlarmLine);
    }

}
