package es.upm.miw.betca_tpv_core.domain.services.utils;

import es.upm.miw.betca_tpv_core.domain.model.Invoice;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

public class PdfInvoiceBuilder {
    private static final String PATH = "/tpv-pdfs/invoices/";
    private static final String FILE = "invoice-";
    private static final String[] TABLE_COLUMNS_HEADERS = {"Desc.", "Ud.", "€/Ud.", "Total €"};
    private static final float[] TABLE_COLUMNS_SIZES_INVOICES = {95, 20, 25, 40};

    public byte[] generateInvoice(Invoice invoice) {
        PdfCoreBuilder pdf = new PdfCoreBuilder(PATH, FILE + invoice.getIdentity());
        pdf.head();
        pdf.paragraphEmphasized("INVOICE");
        pdf.line();
        pdf.paragraphEmphasized(invoice.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        PdfTableBuilder table = pdf.table(TABLE_COLUMNS_SIZES_INVOICES).tableColumnsHeader(TABLE_COLUMNS_HEADERS);
        invoice.getTicket()
                .getShoppingList()
                .forEach(shopping ->
                        table.tableCells(shopping.getDescription(), "" + shopping.getAmount(), "" + shopping.getRetailPrice(),
                                shopping.totalShopping().setScale(2, RoundingMode.HALF_UP) + "€"));
        table.tableColspanRight("Base tax: " + invoice.getBaseTax().setScale(2, RoundingMode.HALF_UP) + "€");
        table.tableColspanRight("Tax value: " + invoice.getTaxValue().setScale(2, RoundingMode.HALF_UP) + "€").buildTable();
        return pdf.foot().build();
    }
}