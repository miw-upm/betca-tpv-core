package es.upm.miw.betca_tpv_core.domain.services.utils;

import es.upm.miw.betca_tpv_core.domain.model.Property;
import es.upm.miw.betca_tpv_core.domain.model.ShoppingState;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;


public class PdfTicketBuilder {

    private static final String[] TABLE_COLUMNS_HEADERS = {"Desc.", "Ud.", "Dto.%", "€", "E."};
    private static final float[] TABLE_COLUMNS_SIZES_TICKETS = {90, 15, 25, 35, 15};
    private static final String PATH = "/tpv-pdfs/tickets/";
    private static final String FILE = "ticket-";
    private static final String BOOKINGS = "/home/bookings/";

    public byte[] generateTicket(Ticket ticket) {

        PdfCoreBuilder pdf = new PdfCoreBuilder(PATH, FILE + ticket.getId());
        pdf.head();
        if (ticket.hasDebt()) {
            pdf.paragraphEmphasized("BOOKING")
                    .paragraphEmphasized("Paid: " + ticket.pay().setScale(2, RoundingMode.HALF_UP) + "€")
                    .paragraphEmphasized("Owed: " + ticket.debt().setScale(2, RoundingMode.HALF_UP) + "€");
        } else {
            pdf.paragraphEmphasized("TICKET");
        }
        pdf.barcode(ticket.getId()).line();

        pdf.paragraphEmphasized(ticket.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        PdfTableBuilder table = pdf.table(TABLE_COLUMNS_SIZES_TICKETS).tableColumnsHeader(TABLE_COLUMNS_HEADERS);
        ticket.getShoppingList().forEach(shopping -> {
            String state = (shopping.getState() != ShoppingState.COMMITTED && shopping.getAmount() > 0) ? "N" : "";
            String discount = "";
            if ((shopping.getDiscount().doubleValue() > 0.04) && !shopping.getBarcode().equals("1")) {
                discount = "" + shopping.getDiscount().setScale(1, RoundingMode.HALF_UP);
            }
            table.tableCells(shopping.getDescription(), "" + shopping.getAmount(), discount,
                    shopping.totalShopping().setScale(2, RoundingMode.HALF_UP) + "€", state);
        });
        table.tableColspanRight(ticket.total().setScale(2, RoundingMode.HALF_UP) + "€").buildTable();

        pdf.paragraph(ticket.getNote());
        if (ticket.itemsNotCommitted() > 0) {
            pdf.paragraphEmphasized("Items pending delivery: " + ticket.itemsNotCommitted());
            if (ticket.getUser() != null) {
                pdf.paragraph("Contact phone: " + ticket.getUser().getMobile() + " - " + ticket.getUser().getFirstName());
            }
            pdf.qrCode(Property.getProperty().getMiwTpv() + BOOKINGS + ticket.getReference());
        }
        return pdf.foot().build();
    }

}
