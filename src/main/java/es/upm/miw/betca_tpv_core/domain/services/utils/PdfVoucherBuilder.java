package es.upm.miw.betca_tpv_core.domain.services.utils;

import es.upm.miw.betca_tpv_core.domain.model.Property;
import es.upm.miw.betca_tpv_core.domain.model.Voucher;

public class PdfVoucherBuilder {

    private static final String PATH = "/tpv-pdfs/vouchers/";
    private static final String FILE = "voucher-";
    private static final String VOUCHER = "/home/vouchers/";

    public byte[] generateVoucherTicket(Voucher voucher) {
        PdfCoreBuilder pdf = new PdfCoreBuilder(PATH, FILE + voucher.getReference().toString());
        pdf.head();
        pdf.paragraphEmphasized("VOUCHER");
        pdf.barcode(voucher.getReference().toString()).line();
        pdf.paragraphEmphasized("Voucher value: " + voucher.getValue());
        pdf.paragraphEmphasized("Voucher creation date: " + voucher.getCreationDate());
        pdf.qrCode(Property.getProperty().getMiwTpv() + VOUCHER + voucher.getReference().toString());
        return pdf.foot().build();
    }
}
