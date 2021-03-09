package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockAlarm {
    @NotBlank
    private String name;
    private String description;
    private Integer warning;
    private Integer critical;
    @Singular("alarmLine")
    private List<StockAlarmLine> stockAlarmLines;

    public void doDefault() {
        if (Objects.isNull(warning)) {
            this.warning = 5;
        }
        if (Objects.isNull(critical)) {
            this.critical = 5;
        }
    }
}
