package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
