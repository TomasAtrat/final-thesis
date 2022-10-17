package com.gmail.tomasatrat.ui.views.inventory;

import com.gmail.tomasatrat.backend.data.Role;
import com.gmail.tomasatrat.backend.data.entity.Inventory;
import com.gmail.tomasatrat.backend.data.entity.InventoryDetail;
import com.gmail.tomasatrat.backend.data.entity.Stock;
import com.gmail.tomasatrat.backend.data.entity.User;
import com.gmail.tomasatrat.backend.microservices.barcode.services.BarcodeService;
import com.gmail.tomasatrat.backend.microservices.inventory.services.InventoryService;
import com.gmail.tomasatrat.backend.microservices.stock.services.StockService;
import com.gmail.tomasatrat.backend.service.UserService;
import com.gmail.tomasatrat.ui.MainView;
import com.gmail.tomasatrat.ui.dataproviders.GenericDataProvider;
import com.gmail.tomasatrat.ui.utils.Constants;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.crud.*;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_INVENTORY;

@Route(value = PAGE_INVENTORY, layout = MainView.class)
@PageTitle(Constants.TITLE_INVENTORY)
@Secured({Role.EMPLOYEE, Role.ADMIN})
public class InventoryView extends VerticalLayout {

    private InventoryService inventoryService;
    private UserService userService;
    private BarcodeService barcodeService;
    private StockService stockService;

    private Grid<Inventory> grid;
    private Crud<Inventory> crud;

    private TextField description;
    Grid<Stock> productListBox;
    private ComboBox<User> users;
    private Dialog detailsForm;

    @Autowired
    public InventoryView(InventoryService inventoryService, UserService userService, BarcodeService barcodeService, StockService stockService) {
        this.inventoryService = inventoryService;
        this.userService = userService;
        this.barcodeService = barcodeService;
        this.stockService = stockService;

        setAlignItems(Alignment.CENTER);

        setupGrid();

        Button newItemButton = new Button("Nuevo inventario");
        newItemButton.addClickListener(e -> crud.edit(new Inventory(), Crud.EditMode.NEW_ITEM));

        setupCrud();

        this.add(crud);

        this.add(new H2("Panel de inventario"), newItemButton, crud);
    }

    private void setupGrid() {
        grid = new Grid<Inventory>();
        Crud.addEditColumn(grid);
        grid.setColumnReorderingAllowed(true);
        grid.addColumn(createActionsMenuBar()).setHeader("").setAutoWidth(true);
        grid.addColumn(Inventory::getId).setHeader("Id").setAutoWidth(true).setResizable(true);
        grid.addColumn(Inventory::getDescription).setHeader("Descripción").setAutoWidth(true).setResizable(true);
        grid.addColumn(Inventory::getAddDate).setHeader("Fecha de creación").setAutoWidth(true).setResizable(true);
        grid.addColumn(Inventory::getStartingDate).setHeader("Fecha de inicio").setAutoWidth(true).setResizable(true);
        grid.addColumn(Inventory::getEndingDate).setHeader("Fecha de fin").setAutoWidth(true).setResizable(true);
        grid.addColumn(inventory -> inventory.getUserAssigned().getUsername()).setHeader("Usuario").setWidth("90px").setResizable(true);
    }

    private ComponentRenderer<MenuBar, Inventory> createActionsMenuBar() {
        return new ComponentRenderer<>(MenuBar::new, statusComponentUpdater);
    }

    private final SerializableBiConsumer<MenuBar, Inventory> statusComponentUpdater = (menuBar, inventory) -> {
        menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);

        MenuItem actions = createIconItem(menuBar, VaadinIcon.BULLETS, null, null);

        MenuItem addDetail = createIconItem(actions.getSubMenu(), VaadinIcon.BULLETS, "Detalles", null, true);

        addDetail.addClickListener(e -> openDetailsForm(inventory));
    };

    private static MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName, String label, String ariaLabel) {
        return createIconItem(menu, iconName, label, ariaLabel, false);
    }

    private static MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName, String label, String ariaLabel, boolean isChild) {
        Icon icon = new Icon(iconName);
        if (isChild) {
            icon.getStyle().set("width", "var(--lumo-icon-size-s)");
            icon.getStyle().set("height", "var(--lumo-icon-size-s)");
            icon.getStyle().set("marginRight", "var(--lumo-space-s)");
        }
        MenuItem item = menu.addItem(icon, e -> {
        });

        if (ariaLabel != null) {
            item.getElement().setAttribute("aria-label", ariaLabel);
        }

        if (label != null) {
            item.add(new Text(label));
        }

        return item;
    }

    private void openDetailsForm(Inventory inventory) {
        detailsForm = new Dialog();
        detailsForm.setWidthFull();
        detailsForm.getElement().setAttribute("aria-label", "Añadir detalles");
        Grid<InventoryDetail> detailGrid = new Grid<>();

        detailGrid.setItems(this.inventoryService.getOrderDetailsByOrder(inventory));

        detailGrid.setColumnReorderingAllowed(true);
        detailGrid.addColumn(createDetailStatusIndicator()).setHeader("Estado").setAutoWidth(true).setResizable(true);
        detailGrid.addColumn(detail -> detail.getBarcode().getProductCode().getId()).setHeader("Producto").setAutoWidth(true).setResizable(true);
        detailGrid.addColumn(detail -> detail.getBarcode().getProductCode().getDescription()).setHeader("Producto").setAutoWidth(true).setResizable(true);
        detailGrid.addColumn(detail -> detail.getBarcode().getId()).setHeader("Código de barras").setAutoWidth(true).setResizable(true);
        detailGrid.addColumn(detail -> detail.getBarcode().getColour()).setHeader("Color").setAutoWidth(true).setResizable(true);
        detailGrid.addColumn(detail -> detail.getBarcode().getSize()).setHeader("Tamaño").setAutoWidth(true).setResizable(true);
        detailGrid.addColumn(InventoryDetail::getSupposedQty).setHeader("Cantidad teórica").setAutoWidth(true).setResizable(true);

        detailsForm.add(detailGrid);
        detailsForm.open();
    }

    private ComponentRenderer<Span, InventoryDetail> createDetailStatusIndicator() {
        return new ComponentRenderer<>(Span::new, detailStatusIndicator);
    }

    private final SerializableBiConsumer<Span, InventoryDetail> detailStatusIndicator = (span, detail) -> {
        boolean hasProblems = !Objects.equals(detail.getReadQty(), detail.getSupposedQty());
        String theme = String
                .format("badge %s", hasProblems ? "error" : "success");
        span.getElement().setAttribute("theme", theme);

        if(!hasProblems) span.setText("OK");

        else if(detail.getReadQty() < detail.getSupposedQty()) span.setText("QUIEBRE");

        else if(detail.getReadQty() == 0) span.setText("OUT OF STOCK");
    };

    private void setupCrud() {
        crud = new Crud<>(Inventory.class, grid, createInventoryEditor());

        setupDataProvider();

        crud.addThemeVariants(CrudVariant.NO_BORDER);

        crud.setI18n(createSpanishI18n());
    }

    private CrudEditor<Inventory> createInventoryEditor() {
        setupFormFields();

        FormLayout form = new FormLayout();

        form.add(description, productListBox, users);

        Binder<Inventory> binder = getBinder();

        return new BinderCrudEditor<>(binder, form);
    }

    private void setupFormFields() {
        productListBox = new Grid<>(Stock.class, false);
        productListBox.setSelectionMode(Grid.SelectionMode.MULTI);
        productListBox.setItems(stockService.findAll());
        productListBox.addColumn(stock -> stock.getBarcodeBarcode().getProductCode().getDescription()).setHeader("Descripción");
        productListBox.addColumn(Stock::getQtStock).setHeader("Stock");
        productListBox.addColumn(stock -> stock.getBarcodeBarcode().getId()).setHeader("Código");
        description = new TextField("Descripción");
        users = new ComboBox<>("Usuario");
        users.setItems(userService.findAllByActiveIsTrue());
        users.setItemLabelGenerator(user ->
                user.getId() + " - " + user.getUsername());
    }

    private Binder<Inventory> getBinder() {
        Binder<Inventory> binder = new Binder<>(Inventory.class);
        binder.bind(description, Inventory::getDescription, Inventory::setDescription);
        binder.bind(users, Inventory::getUserAssigned, Inventory::setUserAssigned);

        return binder;
    }

    private void setupDataProvider() {
        GenericDataProvider<Inventory> dataProvider = new GenericDataProvider<>(inventoryService);

        crud.setDataProvider(dataProvider);
        crud.addSaveListener(e -> saveInventory(e.getItem(), productListBox.getSelectedItems()));
        crud.addDeleteListener(e -> dataProvider.delete(e.getItem()));
    }

    private void saveInventory(Inventory item, Set<Stock> barcodes) {
        if (validateFields()) {
            List<Stock> list = new ArrayList<>(barcodes);
            inventoryService.addItem(item, list);
        }
    }

    private boolean validateFields() {
        boolean rest = true;

        if (this.users.isEmpty()) {
            newErrorMsg("El usuario es requerido");
            rest = false;
        }

        if (this.description.isEmpty()) {
            newErrorMsg("La descripción es requerida");
            rest = false;
        }

        if (this.productListBox.getSelectedItems().size() == 0) {
            newErrorMsg("Se requiere seleccionar al menos un producto");
            rest = false;
        }

        return rest;
    }

    private void newErrorMsg(String msg) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        Div statusText = new Div(new Text(msg));

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(statusText, closeButton);
        layout.setAlignItems(Alignment.CENTER);

        notification.add(layout);
        notification.setPosition(Notification.Position.BOTTOM_CENTER);
        notification.setDuration(4000);
        notification.open();
    }

    private CrudI18n createSpanishI18n() {
        CrudI18n spanishI18n = CrudI18n.createDefault();

        spanishI18n.setNewItem("Nuevo inventario");
        spanishI18n.setEditItem("Editar inventario");
        spanishI18n.setSaveItem("Guardar cambios");
        spanishI18n.setDeleteItem("Borrar inventario");
        spanishI18n.setCancel("Cancelar");
        spanishI18n.setEditLabel("Modificar");

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
