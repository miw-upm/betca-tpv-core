package es.upm.miw.betca_tpv_core.domain.services.utils;

import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.domain.model.Property;

import java.time.format.DateTimeFormatter;

public class PdfOfferBuilder {

    private static final String PATH = "/tpv-pdfs/offers/";
    private static final String FILE = "ticket-";
    private static final String OFFER = "/home/offers/";

    public byte[] generateOffer(Offer offer) {
        PdfCoreBuilder pdf = new PdfCoreBuilder(PATH, FILE + offer.getReference());
        pdf.head();
        pdf.paragraphEmphasized("OFFER");
        pdf.barcode(offer.getReference()).line();
        pdf.paragraphEmphasized("Offer description: " + offer.getDescription());
        pdf.paragraphEmphasized("Offer expiry date: " + offer.getExpiryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        pdf.qrCode(Property.getProperty().getMiwTpv() + OFFER + offer.getReference());
        return pdf.foot().build();
    }
}
