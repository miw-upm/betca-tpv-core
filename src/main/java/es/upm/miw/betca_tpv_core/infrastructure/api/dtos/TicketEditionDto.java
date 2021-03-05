package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketEditionDto {
    private String id;
    private List<Shopping> shoppingList;

    public TicketEditionDto(Ticket ticket) {
        this.id = ticket.getId();
        this.shoppingList = ticket.getShoppingList();
    }
}
