package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketSelectedDto {
    private String id;
    private String reference;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;
    private UserSelectedDto user;

    public TicketSelectedDto(Ticket ticket) {
        this.id = ticket.getId();
        this.reference = ticket.getReference();
        this.creationDate = ticket.getCreationDate();
        this.user = new UserSelectedDto(ticket.getUser());
    }
}
