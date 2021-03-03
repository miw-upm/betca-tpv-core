package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketBasicDto {
    private String id;
    private String reference;
    private String mobile;
}
