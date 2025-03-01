package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Invoice;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class InvoiceEntity {
    @Id
    private String id;

    @Indexed(unique = true)
    private Integer identity;

    @Indexed(unique = true)
    private String ticketId;

    @Indexed(unique = true)
    private String ticketReference;

    private String userMobile;
    private LocalDateTime creationDate;
    private BigDecimal baseTax;
    private BigDecimal taxValue;

    public Invoice toInvoice() {
        Invoice invoice = new Invoice();
        BeanUtils.copyProperties(this, invoice);

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .build();
        invoice.setTicket(ticket);

        User user = User.builder()
                .mobile(userMobile)
                .build();
        invoice.setUser(user);
        return invoice;
    }
}