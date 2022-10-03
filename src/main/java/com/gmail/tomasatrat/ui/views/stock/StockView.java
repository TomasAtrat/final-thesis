package com.gmail.tomasatrat.ui.views.stock;

import com.gmail.tomasatrat.app.HasLogger;
import com.gmail.tomasatrat.backend.data.Role;
import com.gmail.tomasatrat.backend.data.entity.Reader;
import com.gmail.tomasatrat.backend.data.entity.Stock;
import com.gmail.tomasatrat.backend.microservices.reader.services.ReaderService;
import com.gmail.tomasatrat.backend.microservices.stock.services.StockService;
import com.gmail.tomasatrat.ui.MainView;
import com.gmail.tomasatrat.ui.dataproviders.GenericDataProvider;
import com.gmail.tomasatrat.ui.utils.Constants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.crud.*;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_STOCK;

@Route(value = PAGE_STOCK, layout = MainView.class)
@PageTitle(Constants.TITLE_STOCK)
@Secured(Role.ADMIN)
public class StockView extends VerticalLayout implements HasLogger {

    private StockService stockService = null;

    private Grid<Stock> grid;
    private Crud<Stock> crud;

    public StockView(StockService stockService) {
        this.stockService = stockService;

        setAlignItems(Alignment.CENTER);

        setupGrid();

        setupCrud();

        this.add(crud);

        this.add(new H2("Stock"), crud);
    }

    private void setupGrid() {
        grid = new Grid<Stock>();
        grid.setColumnReorderingAllowed(true);
        grid.addColumn(stock -> stock.getBarcodeBarcode().getDescription1()).setHeader("Producto").setAutoWidth(true).setResizable(true);
        grid.addColumn(stock -> stock.getBarcodeBarcode().getId()).setHeader("Código de barras").setAutoWidth(true).setResizable(true);
        grid.addColumn(stock -> stock.getBarcodeBarcode().getColour()).setHeader("Color").setAutoWidth(true).setResizable(true);
        grid.addColumn(stock -> stock.getBarcodeBarcode().getSize()).setHeader("Talle").setAutoWidth(true).setResizable(true);
        grid.addColumn(Stock::getQtStock).setHeader("Cantidad stock").setAutoWidth(true).setResizable(true);
        grid.addColumn(Stock::getQtReserve).setHeader("Cantidad reserva").setAutoWidth(true).setResizable(true);
        grid.addColumn(Stock::getAddDate).setHeader("Fecha de alta").setAutoWidth(true).setResizable(true);
        grid.addColumn(Stock::getUpdateDate).setHeader("Fecha de modificación").setAutoWidth(true).setResizable(true);
    }

    private void setupCrud() {
        crud = new Crud<>(Stock.class, grid, createStockEditor());

        setupDataProvider();

        crud.addThemeVariants(CrudVariant.NO_BORDER);

        crud.setI18n(createSpanishI18n());
    }

    private CrudEditor<Stock> createStockEditor() {
        FormLayout form = new FormLayout();

        form.add();

        Binder<Stock> binder = new Binder<>();

        return new BinderCrudEditor<>(binder, form);
    }

    private void setupDataProvider() {
        GenericDataProvider<Stock> dataProvider = new GenericDataProvider<>(stockService);

        crud.setDataProvider(dataProvider);
        crud.addSaveListener(e -> saveStock(e.getItem(), dataProvider));
        crud.addDeleteListener(e -> dataProvider.delete(e.getItem()));
    }

    private void saveStock(Stock item, GenericDataProvider<Stock> dataProvider) {
        dataProvider.persist(item);
    }

    private CrudI18n createSpanishI18n() {
        CrudI18n spanishI18n = CrudI18n.createDefault();

        spanishI18n.getConfirm().getCancel().setTitle("Cancelar");
        spanishI18n.getConfirm().getCancel().setContent("Tienes cambios sin guardar");
        spanishI18n.getConfirm().getCancel().getButton().setDismiss("Ignorar");
        spanishI18n.getConfirm().getCancel().getButton().setConfirm("Confirmar");

        spanishI18n.getConfirm().getDelete().setTitle("Eliminar");
        spanishI18n.getConfirm().getDelete().setContent("Esta acción no se puede revertir");
        spanishI18n.getConfirm().getDelete().getButton().setDismiss("Cancelar");
        spanishI18n.getConfirm().getDelete().getButton().setConfirm("Guardar cambios");

        return spanishI18n;
    }
}
