package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Login {
    private String id;
    private String phone;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime logoutDate;

    public Double getTotal() {
        LocalDateTime logoutDateIfNotExist = logoutDate == null ? loginDate : logoutDate;
        Double val = (double)(logoutDateIfNotExist.toEpochSecond(ZoneOffset.UTC) - loginDate.toEpochSecond(ZoneOffset.UTC)) / 3600;
        return round2(val);
    }
    Double round2(Double val) {
        return new BigDecimal(val.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
