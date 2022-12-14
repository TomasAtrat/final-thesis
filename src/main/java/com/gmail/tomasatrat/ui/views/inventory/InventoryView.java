package com.gmail.tomasatrat.ui.views.inventory;

import com.gmail.tomasatrat.backend.data.Role;
import com.gmail.tomasatrat.backend.data.entity.*;
import com.gmail.tomasatrat.backend.microservices.barcode.services.BarcodeService;
import com.gmail.tomasatrat.backend.microservices.inventory.services.InventoryService;
import com.gmail.tomasatrat.backend.microservices.product.services.ProductService;
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
import com.vaadin.flow.component.dependency.CssImport;
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
@CssImport(value = "./styles/styles.css", themeFor = "vaadin-grid")
@Secured({Role.EMPLOYEE, Role.ADMIN})
public class InventoryView extends VerticalLayout {

    private InventoryService inventoryService;
    private UserService userService;
    private BarcodeService barcodeService;
    private StockService stockService;
    private ProductService productService;

    private Grid<Inventory> grid;
    private Crud<Inventory> crud;

    private TextField description;
    Grid<Stock> productListBox;
    private ComboBox<User> users;
    private Dialog detailsForm;

    @Autowired
    public InventoryView(InventoryService inventoryService,
                         UserService userService,
                         BarcodeService barcodeService,
                         StockService stockService,
                         ProductService productService) {
        this.inventoryService = inventoryService;
        this.userService = userService;
        this.barcodeService = barcodeService;
        this.stockService = stockService;
        this.productService = productService;

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
        grid.addColumn(Inventory::getDescription).setHeader("Descripci??n").setAutoWidth(true).setResizable(true);
        grid.addColumn(Inventory::getAddDate).setHeader("Fecha de creaci??n").setAutoWidth(true).setResizable(true);
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

        if (inventory.getStartingDate() != null) {
            MenuItem addDetail = createIconItem(actions.getSubMenu(), VaadinIcon.BULLETS, "Detalles", null, true);
            addDetail.addClickListener(e -> openDetailsForm(inventory));
        }
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
        detailsForm.getElement().setAttribute("aria-label", "A??adir detalles");
        Grid<InventoryDetail> detailGrid = new Grid<>();

        detailGrid.setItems(this.inventoryService.getOrderDetailsByOrder(inventory));

        detailGrid.setColumnReorderingAllowed(true);
        detailGrid.addColumn(createDetailStatusIndicator()).setHeader("Estado").setAutoWidth(true).setResizable(true);
        detailGrid.addColumn(detail -> detail.getBarcode().getProductCode().getId()).setHeader("Producto").setAutoWidth(true).setResizable(true);
        detailGrid.addColumn(detail -> detail.getBarcode().getProductCode().getDescription()).setHeader("Producto").setAutoWidth(true).setResizable(true);
        detailGrid.addColumn(detail -> detail.getBarcode().getId()).setHeader("C??digo de barras").setAutoWidth(true).setResizable(true);
        detailGrid.addColumn(detail -> detail.getBarcode().getColour()).setHeader("Color").setAutoWidth(true).setResizable(true);
        detailGrid.addColumn(detail -> detail.getBarcode().getSize()).setHeader("Tama??o").setAutoWidth(true).setResizable(true);
        detailGrid.addColumn(InventoryDetail::getSupposedQty).setHeader("Cantidad te??rica").setAutoWidth(true).setResizable(true);

        detailGrid.setClassNameGenerator(detail -> {
            boolean hasProblems = !Objects.equals(detail.getReadQty(), detail.getSupposedQty());

            if (!hasProblems) return "stock-product-style-2";

            else if (detail.getReadQty() == 0) return "stock-product-style-1";

            else if (detail.getReadQty() < productService.findById(detail.getBarcode().getProductCode().getId()).getResupplyQuantity())
                return "stock-product-style-3";

            return "stock-product-style-1";
        });

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

        if (!hasProblems) span.setText("OK");

        else if (detail.getReadQty() == 0) span.setText("OUT OF STOCK");

        else if (detail.getReadQty() < productService.findById(detail.getBarcode().getProductCode().getId()).getResupplyQuantity())
            span.setText("QUIEBRE");
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

        form.setWidthFull();

        form.add(description, users, productListBox);

        Binder<Inventory> binder = getBinder();

        return new BinderCrudEditor<>(binder, form);
    }

    private void setupFormFields() {
        productListBox = new Grid<>(Stock.class, false);
        productListBox.setSelectionMode(Grid.SelectionMode.MULTI);
        productListBox.setItems(stockService.findAll());
        productListBox.addColumn(stock -> stock.getBarcodeBarcode().getProductCode().getDescription()).setHeader("Descripci??n");
        productListBox.addColumn(Stock::getQtStock).setHeader("Stock");
        productListBox.addColumn(stock -> stock.getBarcodeBarcode().getId()).setHeader("C??digo");
        description = new TextField("Descripci??n");
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
            Notification.show("El registro ha sido correctamente ingresado", 5000, Notification.Position.BOTTOM_CENTER);
        }
    }

    private boolean validateFields() {
        boolean rest = true;

        if (this.users.isEmpty()) {
            newErrorMsg("El usuario es requerido");
            rest = false;
        }

        if (this.description.isEmpty()) {
            newErrorMsg("La descripci??n es requerida");
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
        spanishI18n.getConfirm().getDelete().setContent("Esta acci??n no se puede revertir");
        spanishI18n.getConfirm().getDelete().getButton().setDismiss("Cancelar");
        spanishI18n.getConfirm().getDelete().getButton().setConfirm("Guardar cambios");

        return spanishI18n;
    }
}
