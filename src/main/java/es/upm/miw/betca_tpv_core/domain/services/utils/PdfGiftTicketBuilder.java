package es.upm.miw.betca_tpv_core.domain.services.utils;

import es.upm.miw.betca_tpv_core.domain.model.GiftTicket;
import es.upm.miw.betca_tpv_core.domain.model.Property;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PdfGiftTicketBuilder {

    private static final String[] TABLE_COLUMNS_HEADERS_GIFT = {"Desc.", "Ud."};
    private static final float[] TABLE_COLUMNS_SIZES_TICKETS_GIFT = {90, 15};
    private static final String PATH_TPV = "/tpv-pdfs";
    private static final String PATH_TICKET = "/tickets";
    private static final String FILE = "ticket-";
    private static final int EXPIRATION_DAYS = 30;
    public byte[] generateGiftTicket(GiftTicket giftTicket) {

        PdfCoreBuilder pdf = new PdfCoreBuilder(PATH_TPV + PATH_TICKET, FILE + giftTicket.getId());
        pdf.head();
        pdf.paragraphEmphasized("TICKET");

        pdf.paragraphEmphasized(giftTicket.getTicket().getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        PdfTableBuilder table = pdf.table(TABLE_COLUMNS_SIZES_TICKETS_GIFT).tableColumnsHeader(TABLE_COLUMNS_HEADERS_GIFT);
        giftTicket.getTicket().getShoppingList().forEach(shopping ->
            table.tableCells(shopping.getDescription(), "" + shopping.getAmount())
        );
        if (LocalDateTime.now().isAfter(giftTicket.getTicket().getCreationDate().plusDays(EXPIRATION_DAYS))) {
            pdf.paragraph("It is no longer possible to use the QR code because it has expired.");
        } else {
            pdf.qrCode(Property.getMiwTpv() + "/getTicket?reference=" + giftTicket.getReference());
        }
        pdf.paragraphEmphasized("Expiration date: " + giftTicket.getTicket().getCreationDate().plusDays(EXPIRATION_DAYS));

        pdf.paragraphEmphasized(giftTicket.getMessage());

        return pdf.foot_GiftTicket().build();
    }
}