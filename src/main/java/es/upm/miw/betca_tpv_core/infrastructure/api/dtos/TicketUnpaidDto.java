package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketUnpaidDto {
    private String reference;
    private BigDecimal amount;
    private String creationDate;

    public TicketUnpaidDto(Ticket ticket) {
        this.reference = ticket.getReference();
        this.amount = ticket.total();
        this.creationDate = ticket.getCreationDate().toString();
    }
}
