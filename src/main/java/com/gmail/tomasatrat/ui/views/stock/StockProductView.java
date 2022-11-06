package com.gmail.tomasatrat.ui.views.stock;

import com.gmail.tomasatrat.app.HasLogger;
import com.gmail.tomasatrat.backend.data.Role;
import com.gmail.tomasatrat.backend.data.entity.InventoryDetail;
import com.gmail.tomasatrat.backend.data.entity.Stock;
import com.gmail.tomasatrat.backend.data.entity.VStockProduct;
import com.gmail.tomasatrat.backend.microservices.stock.services.StockService;
import com.gmail.tomasatrat.ui.MainView;
import com.gmail.tomasatrat.ui.utils.Constants;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;

import java.util.Objects;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_STOCKPRODUCT;

@Route(value = PAGE_STOCKPRODUCT, layout = MainView.class)
@CssImport(value = "./styles/styles.css", themeFor = "vaadin-grid")
@PageTitle(Constants.TITLE_STOCKPRODUCT)
@Secured({Role.ADMIN, Role.EMPLOYEE})
public class StockProductView extends VerticalLayout implements HasLogger {

    private StockService stockService;

    private Grid<VStockProduct> grid;

    public StockProductView(StockService stockService) {
        this.stockService = stockService;

        setAlignItems(Alignment.CENTER);

        setupGrid();

        this.add(new H2("Monitoreo de stock"), grid);
    }

    private void setupGrid() {
        grid = new Grid<>();
        grid.setColumnReorderingAllowed(true);
        grid.addColumn(createDetailStatusIndicator()).setHeader("Estado").setAutoWidth(true).setResizable(true);
        grid.addColumn(VStockProduct::getCode).setHeader("Código producto").setAutoWidth(true);
        grid.addColumn(VStockProduct::getDescription).setHeader("Descripción").setAutoWidth(true);
        grid.addColumn(VStockProduct::getQtStock).setHeader("Cantidad de stock").setAutoWidth(true);
        grid.addColumn(VStockProduct::getResupplyQuantity).setHeader("Quiebre de stock").setAutoWidth(true);

        grid.setItems(stockService.getAllStockProduct());

        grid.setClassNameGenerator(stockProduct -> {
            if (stockProduct.getQtStock().intValue() == 0) {
                return "stock-product-style-1";
            } else if (stockProduct.getQtStock().intValue() >= stockProduct.getResupplyQuantity()) {
                return "stock-product-style-2";
            } else if (stockProduct.getQtStock().intValue() < stockProduct.getResupplyQuantity()) {
                return "stock-product-style-3";
            }

            return "stock-product-style-1";
        });
    }

    private ComponentRenderer<Span, VStockProduct> createDetailStatusIndicator() {
        return new ComponentRenderer<>(Span::new, detailStatusIndicator);
    }

    private final SerializableBiConsumer<Span, VStockProduct> detailStatusIndicator = (span, stockProduct) -> {

        boolean hasProblems = stockProduct.getQtStock().intValue() == 0 || stockProduct.getQtStock().intValue() < stockProduct.getResupplyQuantity();

        String theme = String
                .format("badge %s", hasProblems ? "error" : "success");

        span.getElement().setAttribute("theme", theme);

        if (stockProduct.getQtStock().intValue() == 0) span.setText("OUT OF STOCK");

        else if (!hasProblems) span.setText("OK");

        else if (stockProduct.getQtStock().intValue() < stockProduct.getResupplyQuantity()) span.setText("QUIEBRE");
    };
}
