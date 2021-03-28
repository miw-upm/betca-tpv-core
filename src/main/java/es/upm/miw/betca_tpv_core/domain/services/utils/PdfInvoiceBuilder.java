package es.upm.miw.betca_tpv_core.domain.services.utils;

import es.upm.miw.betca_tpv_core.domain.model.*;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

public class PdfInvoiceBuilder {
    private static final String[] TABLE_COLUMNS_HEADERS = {"Desc.", "Ud.", "Dto.%", "Price €", "E."};
    private static final float[] TABLE_COLUMNS_SIZES_INVOICES = {90, 15, 25, 35, 15};
    private static final String PATH = "/tpv-pdfs/invoices/";
    private static final String FILE = "invoice-";


    public byte[] generateInvoice(Invoice invoice) {

        Ticket ticket = invoice.getTicket();
        PdfCoreBuilder pdf = new PdfCoreBuilder(PATH, FILE + invoice.getNumber());
        pdf.head();

        User user = ticket.getUser();
        pdf.paragraphEmphasized("User.")
                .paragraph("Complete name: " + user.getFirstName() + " " + user.getFamilyName())
                .paragraph("DNI: " + user.getDni() + "    -   " + "Mobile: " + user.getMobile())
                .paragraph("Address: " + user.getAddress() + "    -   " + "Email: " + user.getEmail())
                .line();

        pdf.paragraphEmphasized("INVOICE");
        pdf.paragraphEmphasized(invoice.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        PdfTableBuilder table = pdf.table(TABLE_COLUMNS_SIZES_INVOICES).tableColumnsHeader(TABLE_COLUMNS_HEADERS);
        ticket.getShoppingList().forEach(shopping -> {
            String state = (shopping.getState() != ShoppingState.COMMITTED && shopping.getAmount() > 0) ? "N" : "";
            String discount = "";
            if ((shopping.getDiscount().doubleValue() > 0.04) && !shopping.getBarcode().equals("1")) {
                discount = "" + shopping.getDiscount().setScale(1, RoundingMode.HALF_UP);
            }
            table.tableCells(shopping.getDescription(), "" + shopping.getAmount(), discount,
                    shopping.totalShopping().setScale(2, RoundingMode.HALF_UP) + "€", state);
        });

        table.tableColspanRight("Base tax: " + invoice.getBaseTax().setScale(2, RoundingMode.HALF_UP) + "€").buildTable();
        table.tableColspanRight("Tax Value: " + invoice.getTaxValue().setScale(2, RoundingMode.HALF_UP) + "€").buildTable();
        table.tableColspanRight("Total: " + ticket.total().setScale(2, RoundingMode.HALF_UP) + "€").buildTable();

        pdf.barcode(invoice.getNumber()).line();

        return pdf.foot().build();
    }
}
