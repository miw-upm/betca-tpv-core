package es.upm.miw.betca_tpv_core.domain.services.utils;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import java.util.Arrays;

public class PdfTableBuilder {

    private static final int FONT_SIZE_EMPHASIZED = 10;

    private final PdfCoreBuilder pdfCoreBuilder;

    private final Document document;

    private final Table table;

    private int index = 1;


    PdfTableBuilder(PdfCoreBuilder pdfCoreBuilder, Document document, float... widths) {
        this.pdfCoreBuilder = pdfCoreBuilder;
        this.document = document;
        float[] columns = new float[widths.length + 1];
        System.arraycopy(widths, 0, columns, 1, widths.length);
        columns[0] = 15F;
        this.table = new Table(columns, true);
        this.table.setBorder(new SolidBorder(Color.WHITE, 2));
        this.table.setVerticalAlignment(VerticalAlignment.MIDDLE);
        this.table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        this.table.setTextAlignment(TextAlignment.RIGHT);
    }

    public PdfTableBuilder tableColumnsHeader(String... headers) {
        this.table.addHeaderCell(" ");
        Arrays.stream(headers).forEach(this.table::addHeaderCell);
        return this;
    }

    public PdfTableBuilder tableCells(String... cells) {
        this.table.addCell(String.valueOf(index++));
        Arrays.stream(cells).forEach(this.table::addCell);
        return this;
    }

    public PdfTableBuilder tableColspanRight(String value) {
        Cell cell = new Cell(1, this.table.getNumberOfColumns());
        cell.setTextAlignment(TextAlignment.RIGHT).setBold().setFontSize(FONT_SIZE_EMPHASIZED);
        cell.add(value);
        this.table.addCell(cell);
        return this;
    }

    public PdfCoreBuilder buildTable() {
        this.document.add(this.table);
        return this.pdfCoreBuilder;
    }

}
