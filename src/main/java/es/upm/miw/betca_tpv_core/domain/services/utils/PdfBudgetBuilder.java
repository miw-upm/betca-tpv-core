package es.upm.miw.betca_tpv_core.domain.services.utils;

import es.upm.miw.betca_tpv_core.domain.model.Budget;
import es.upm.miw.betca_tpv_core.domain.model.ShoppingState;


import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
public class PdfBudgetBuilder {
    private static final String[] TABLE_COLUMNS_HEADERS = {"Desc.", "Ud.", "Dto.%", "€", "E."};
    private static final float[] TABLE_COLUMNS_SIZES_TICKETS = {90, 15, 25, 35, 15};
    private static final String PATH = "/tpv-pdfs/budgets/";
    private static final String FILE = "budget-";

    public byte[] generateBudget(Budget budget) {

        PdfCoreBuilder pdf = new PdfCoreBuilder(PATH, FILE + budget.getId());
        pdf.head();
        pdf.paragraphEmphasized("BUDGET");
        pdf.barcode(budget.getId()).line();

        pdf.paragraphEmphasized(budget.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        PdfTableBuilder table = pdf.table(TABLE_COLUMNS_SIZES_TICKETS).tableColumnsHeader(TABLE_COLUMNS_HEADERS);
        budget.getShoppingList().forEach(shopping -> {
            String state = (shopping.getState() != ShoppingState.COMMITTED && shopping.getAmount() > 0) ? "N" : "";
            String discount = "";
            if ((shopping.getDiscount().doubleValue() > 0.04) && !shopping.getBarcode().equals("1")) {
                discount = "" + shopping.getDiscount().setScale(1, RoundingMode.HALF_UP);
            }
            table.tableCells(shopping.getDescription(), "" + shopping.getAmount(), discount,
                    shopping.totalShopping().setScale(2, RoundingMode.HALF_UP) + "€", state);
        });
        table.tableColspanRight(budget.getBudgetTotal().setScale(2, RoundingMode.HALF_UP) + "€").buildTable();
        return pdf.foot().build();
    }
}
