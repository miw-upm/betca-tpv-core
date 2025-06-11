package es.upm.miw.betca_tpv_core.domain.services.utils;

import es.upm.miw.betca_tpv_core.domain.model.Invoice;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

@Log
@Component
public class PdfInvoiceBuilder {
    private static final String PATH = "/tpv-pdfs/invoices/";
    private static final String FILE = "invoice-";
    private static final String[] TABLE_COLUMNS_HEADERS = {"Desc.", "Ud.", "€/Ud.", "Total €"};
    private static final float[] TABLE_COLUMNS_SIZES_INVOICES = {95, 20, 25, 40};

    private final UserMicroservice userMicroservice;

    public PdfInvoiceBuilder(UserMicroservice userMicroservice) {
        this.userMicroservice = userMicroservice;
    }


    public byte[] generateInvoice(Invoice invoice, String autenticate) {
        System.out.println("Generating Invoice " + invoice.toString());

        PdfCoreBuilder pdf = new PdfCoreBuilder(PATH, FILE + invoice.getIdentity());
        pdf.head();
        pdf.paragraphEmphasized("INVOICE");
        pdf.line();
        var user = invoice.getUser()  == null ?
                this.userMicroservice.readByMobileWithAuthenticate(invoice.getUser().getMobile(), autenticate).block() :
                invoice.getUser();

        pdf.paragraph("Nombre del cliente " + user.getFirstName());
        pdf.paragraph("DNI:  " + user.getDni());
        pdf.paragraph("Mobile:  " + invoice.getUser().getMobile());
        pdf.paragraph("Adrress:  " + user.getAddress());
        pdf.paragraph("Email:  " + user.getEmail());
        pdf.line();
        pdf.paragraphEmphasized(invoice.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        PdfTableBuilder table = pdf.table(TABLE_COLUMNS_SIZES_INVOICES).tableColumnsHeader(TABLE_COLUMNS_HEADERS);
        invoice.getTicket()
                .getShoppingList()
                .forEach(shopping ->
                        table.tableCells(shopping.getDescription(), "" + shopping.getAmount(), "" + shopping.getRetailPrice(),
                                shopping.totalShopping().setScale(2, RoundingMode.HALF_UP) + "€"));
        table.tableColspanRight("Base tax: " + invoice.getBaseTax().setScale(2, RoundingMode.HALF_UP) + "€");
        table.tableColspanRight("Tax value: " + invoice.getTaxValue().setScale(2, RoundingMode.HALF_UP) + "€");
        table.tableColspanRight("Total: " + invoice.getTicket().total().setScale(2, RoundingMode.HALF_UP) + "€").buildTable();
        return pdf.foot().build();
    }
}