package es.upm.miw.betca_tpv_core.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class
StockAlarm {
    @NotBlank
    private String name;
    private String description;
    private Integer warning;
    private Integer critical;
    private List<StockAlarmLine> stockAlarmLines;

    public static StockAlarm ofNameDescriptionWarningCritical(StockAlarm stockAlarm) {
        return StockAlarm.builder()
                .name(stockAlarm.getName())
                .description(stockAlarm.getDescription())
                .warning(stockAlarm.getWarning())
                .critical(stockAlarm.getCritical())
                .build();
    }

    public void doDefault() {
        if(Objects.isNull(stockAlarmLines)) {
            this.stockAlarmLines = new ArrayList<>();
        }
    }
}