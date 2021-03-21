package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.upm.miw.betca_tpv_core.domain.model.Invoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
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
    private String number;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;
    private BigDecimal baseTax;
    private BigDecimal taxValue;
    @DBRef
    private TicketEntity ticketEntity;

    public InvoiceEntity(Invoice invoice) {
        BeanUtils.copyProperties(invoice, this);
    }

    public void addTicket(TicketEntity ticketEntity) {
        this.setTicketEntity(ticketEntity);
    }

    public Invoice toInvoice() {
        Invoice invoice = new Invoice();
        BeanUtils.copyProperties(this, invoice);
        if (this.ticketEntity != null) {
            invoice.setTicket(this.ticketEntity.toTicket());
        }
        return invoice;
    }
}
