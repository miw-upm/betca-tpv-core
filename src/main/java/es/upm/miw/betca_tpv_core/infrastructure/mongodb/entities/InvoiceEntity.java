package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.upm.miw.betca_tpv_core.domain.model.Invoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class InvoiceEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String number;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;
    private BigDecimal baseTax;
    private BigDecimal taxValue;
    @DBRef(lazy = true)
    private TicketEntity ticketEntity;

    public InvoiceEntity(Invoice invoice) {
        BeanUtils.copyProperties(invoice, this);
        if(invoice.getTicket() != null){
            setTicketEntity(new TicketEntity(invoice.getTicket()));
        }
    }

    public void addTicket(TicketEntity ticketEntity) {
        this.setTicketEntity(ticketEntity);
    }

    public Invoice toInvoice() {
        Invoice invoice = new Invoice();
        BeanUtils.copyProperties(this, invoice);
        if ( Objects.nonNull(this.getTicketEntity())) {
            invoice.setTicket(this.getTicketEntity().toTicket());
        }
        return invoice;
    }
}
