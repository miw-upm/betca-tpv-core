package es.upm.miw.betca_tpv_core.domain.services.utils;

import es.upm.miw.betca_tpv_core.domain.model.Offer;

import java.time.format.DateTimeFormatter;

public class PdfOfferBuilder {

    private static final String PATH = "/tpv-pdfs/tickets/";
    private static final String FILE = "ticket-";
    private static final String BASE_URL = "http://localhost:4200";
    private static final String OFFER_URL = "/home/offers/";

    public byte[] generateOffer(Offer offer) {

        PdfCoreBuilder pdf = new PdfCoreBuilder(PATH, FILE + offer.getReference());
        pdf.head();
        pdf.paragraphEmphasized("OFFER - " + offer.getReference());
        pdf.paragraph("Description: " + offer.getDescription());
        pdf.paragraph("Creation date: " + offer.getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        pdf.paragraph("Expiry date: " + offer.getExpiryDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        pdf.paragraph("Discount: " + offer.getDiscount() + "%");
        pdf.line();
        pdf.paragraphEmphasized("OFFER QR CODE");
        pdf.qrCode(BASE_URL + OFFER_URL + offer.getReference());
        return pdf.foot().build();
    }

}
