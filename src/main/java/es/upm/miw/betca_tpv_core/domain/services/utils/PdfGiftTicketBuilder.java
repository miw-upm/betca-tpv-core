package es.upm.miw.betca_tpv_core.domain.services.utils;

import es.upm.miw.betca_tpv_core.domain.model.GiftTicket;
import es.upm.miw.betca_tpv_core.domain.model.Property;

import java.time.LocalDate;

public class PdfGiftTicketBuilder {
    private static final String PATH = "/tpv-pdfs/gifttickets/";
    private static final String FILE = "ticket-";
    private static final String GIFTTICKET = "/home/gifttickets/";

    public byte[] generateGiftTicket(GiftTicket giftTicket) {
        PdfCoreBuilder pdf = new PdfCoreBuilder(PATH, FILE + giftTicket.getId());
        pdf.head();
        pdf.paragraphEmphasized("GIFTTICKET");
        pdf.barcode(giftTicket.getId()).line();
        pdf.paragraphEmphasized("Gift ticket message: " + giftTicket.getMessage());
        pdf.paragraphEmphasized("Gift ticket date: " + LocalDate.now());
        pdf.qrCode(Property.getProperty().getMiwTpv() + GIFTTICKET + giftTicket.getId());
        return pdf.foot().build();
    }
}
