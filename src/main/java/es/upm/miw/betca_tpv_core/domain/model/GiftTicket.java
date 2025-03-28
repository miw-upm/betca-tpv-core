package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GiftTicket {
    @Id
    private String id;
    @Indexed(unique = true)
    private String reference;
    private String message;
    @DBRef(lazy = true)
    private Ticket ticket;

    public GiftTicket(String message, Ticket ticket) {
        this.message = message;
        this.ticket = ticket;
    }
}