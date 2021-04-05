package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Invoice {
    private String id;
    private String number;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;
    private BigDecimal baseTax;
    private BigDecimal taxValue;
    private Ticket ticket;

    public String getPhoneUser(){
        return (ticket != null && ticket.getUser() != null) ? ticket.getUser().getMobile() : null;
    }

    public String getTicketId(){
        return (ticket != null) ? ticket.getId() : null;
    }

    public void setUserTicket(User user){
        if(getTicket() != null){
            getTicket().setUser(user);
        }
    }
}
