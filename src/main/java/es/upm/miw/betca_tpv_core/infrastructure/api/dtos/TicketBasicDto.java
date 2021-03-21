package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketBasicDto {
    private String id;
    private String reference;
    private int mobile;

    public TicketBasicDto(Ticket ticket) {
        this.id = ticket.getId();
        this.reference = ticket.getReference();
        this.mobile = Integer.parseInt(ticket.getUser().getMobile());
    }
}
