package es.upm.miw.betca_tpv_core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class StaffTimeOrder {
    String phone;
    LocalDate startDate;
    LocalDate endDate;
}
