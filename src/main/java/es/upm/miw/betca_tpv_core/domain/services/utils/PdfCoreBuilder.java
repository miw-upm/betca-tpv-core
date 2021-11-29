package es.upm.miw.betca_tpv_core.domain.services.utils;

import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PdfCoreBuilder {

    private static final String USER_HOME = "user.home";
    private static final String PDF_FILE_EXT = ".pdf";

    private static final int LINE_GAP = 2;
    private static final float LINE_WIDTH = 0.5f;
    private static final int BARCODE_WIDTH = 100;
    private static final int BARCODE_HEIGHT = 80;
    private static final int QR_CODE_PERCENT = 70;
    private static final int IMAGE_WIDTH = 80;

    private static final int THERMAL_FONT_SIZE = 7;
    private static final int THERMAL_FONT_SIZE_EMPHASIZED = 10;
    private static final int THERMAL_MARGIN_LEFT = 10;
    private static final int THERMAL_MARGIN_RIGHT = 14;
    private static final int THERMAL_MARGIN_TOP_BOTTOM = 12;
    private static final float THERMAL_PAGE_WIDTH = 227;
    private static final float THERMAL_PAGE_HEIGHT = 800;

    private final String filename;

    private final Document document;

    public PdfCoreBuilder(String path, String file) {
        path = System.getProperty(USER_HOME) + path;
        this.filename = path + file + PDF_FILE_EXT;
        File fileRef = new File(this.filename);
        if (!fileRef.exists()) {
            try {
                Files.createDirectories(Paths.get(path));
            } catch (IOException e) {
                throw new PdfException(String.format("PdfCoreBuilder::prepareDocument. Error when creating directory (%s). %s", this.filename, e));
            }
        }
        try {
            this.document = new Document(new PdfDocument(new PdfWriter(filename)), new PageSize(THERMAL_PAGE_WIDTH, THERMAL_PAGE_HEIGHT));
        } catch (FileNotFoundException fnfe) {
            throw new PdfException(String.format("PdfCoreBuilder::prepareDocument. Error when creating the pdf document (%s). %s", this.filename, fnfe));
        }
        this.document.setMargins(THERMAL_MARGIN_TOP_BOTTOM, THERMAL_MARGIN_RIGHT, THERMAL_MARGIN_TOP_BOTTOM, THERMAL_MARGIN_LEFT);
        this.document.setFontSize(THERMAL_FONT_SIZE);
    }

    public PdfCoreBuilder paragraphEmphasized(String text) {
        this.document.add(new Paragraph(text).setBold().setFontSize(THERMAL_FONT_SIZE_EMPHASIZED));
        return this;
    }

    public PdfCoreBuilder paragraph(String text) {
        this.document.add(new Paragraph(text));
        return this;
    }

    public PdfCoreBuilder line() {
        DottedLine separator = new DottedLine();
        separator.setGap(LINE_GAP);
        separator.setLineWidth(LINE_WIDTH);
        document.add(new LineSeparator(separator));
        return this;
    }

    public PdfCoreBuilder barcode(String code) {
        if (code.length() > 12) {
            code = code.substring(code.length() - 12);
        }
        Barcode128 code128 = new Barcode128(this.document.getPdfDocument());
        code128.setCodeType(Barcode128.CODE128);
        code128.setCode(code.trim().replace('-', '/').replace('_', '?'));// UTF8
        code128.setAltText(code.trim());
        Image code128Image = new Image(code128.createFormXObject(this.document.getPdfDocument()));
        code128Image.setWidthPercent(BARCODE_WIDTH);
        code128Image.setHeight(BARCODE_HEIGHT);
        code128Image.setHorizontalAlignment(HorizontalAlignment.CENTER);
        this.document.add(code128Image);
        return this;
    }

    public PdfCoreBuilder qrCode(String code) {
        BarcodeQRCode barcodeQRCode = new BarcodeQRCode(code.trim());
        Image qcCodeImage = new Image(barcodeQRCode.createFormXObject(this.document.getPdfDocument()));
        qcCodeImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
        qcCodeImage.setWidthPercent(QR_CODE_PERCENT);
        this.document.add(qcCodeImage);
        Paragraph paragraph = new Paragraph(code);
        paragraph.setTextAlignment(TextAlignment.CENTER);
        this.document.add(paragraph);
        return this;
    }

    public PdfCoreBuilder head() {
        this
                .image("logo-upm.png")
                .paragraphEmphasized("Master en Ingeniería Web. BETCA")
                .paragraphEmphasized("Tfn: +34 913366000")
                .paragraph("NIF: Q2818015F   -   Calle Alan Turing s/n, 28031 Madrid")
                .paragraph("Email: miw.etsisi@upm.es   -   Web: miw.etsisi.upm.es")
                .line();
        return this;
    }

    public PdfCoreBuilder foot() {
        this
                .line()
                .paragraph("Items can be returned within 15 days of shopping")
                .paragraphEmphasized("Thanks for your visit and please send us your suggestions to improve this service")
                .paragraphEmphasized(" ")
                .line();
        return this;
    }

    public PdfCoreBuilder image(String fileName) {
        try {
            Image img = new Image(ImageDataFactory.create(new ClassPathResource("imges/" + fileName).getURL()));
            img.setWidth(IMAGE_WIDTH);
            img.setHorizontalAlignment(HorizontalAlignment.CENTER);
            this.document.add(img);
        } catch (IOException e) {
            LogManager.getLogger(this.getClass())
                    .error(String.format("PdfTicketBuilder::addImage. Error when add image to PDF (%s). %s", fileName, e));
            throw new PdfException("Can’t add image to PDF (" + fileName + "). " + e);
        }
        return this;
    }

    public PdfTableBuilder table(float... widths) {
        return new PdfTableBuilder(this, this.document, widths);
    }

    public byte[] build() {
        this.document.close();
        try {
            return Files.readAllBytes(new File(this.filename).toPath());
        } catch (IOException ioe) {
            throw new PdfException(String.format("PdfTicketBuilder::buildTable. Error when read bytes to PDF. %s", ioe));
        }
    }
}
