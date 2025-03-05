package es.upm.miw.betca_tpv_core.domain.services.utils;

import es.upm.miw.betca_tpv_core.domain.model.Voucher;

import java.time.format.DateTimeFormatter;

public class PdfVoucherBuilder {

    private static final String PATH = "/tpv-pdfs/tickets/";
    private static final String FILE = "ticket-";
    private static final String BASE_URL = "http://localhost:4200";
    private static final String VOUCHER_URL = "/home/vouchers/";

    public byte[] generateVoucher(Voucher voucher) {

        PdfCoreBuilder pdf = new PdfCoreBuilder(PATH, FILE + voucher.getReference());
        pdf.head();
        pdf.paragraphEmphasized("VOUCHER - " + voucher.getReference());
        pdf.paragraph("Value: " + voucher.getValue() + "€");
        pdf.paragraph("Creation date: " + voucher.getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        pdf.paragraph("Date of user: " + voucher.getDateOfUse().format(DateTimeFormatter.ISO_LOCAL_DATE));
        pdf.paragraph("User mobile: " + voucher.getUser().getMobile());

        pdf.line();
        pdf.paragraphEmphasized("VOUCHER QR CODE");
        pdf.qrCode(BASE_URL + VOUCHER_URL + voucher.getReference());
        return pdf.foot().build();
    }

}
