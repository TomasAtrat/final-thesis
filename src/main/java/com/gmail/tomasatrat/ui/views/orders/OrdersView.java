package com.gmail.tomasatrat.ui.views.orders;

import com.gmail.tomasatrat.app.HasLogger;
import com.gmail.tomasatrat.backend.common.exceptions.SmartStoreException;
import com.gmail.tomasatrat.backend.data.entity.*;
import com.gmail.tomasatrat.backend.microservices.expedition.ExpeditionTypeService;
import com.gmail.tomasatrat.backend.microservices.orders.services.OrderService;
import com.gmail.tomasatrat.backend.microservices.product.services.ProductService;
import com.gmail.tomasatrat.backend.microservices.branch.services.BranchService;
import com.gmail.tomasatrat.ui.MainView;
import com.gmail.tomasatrat.ui.dataproviders.GenericDataProvider;
import com.gmail.tomasatrat.ui.utils.Constants;
import com.gmail.tomasatrat.ui.utils.converters.DateConverter;
import com.gmail.tomasatrat.ui.views.orders.enums.ExpeditionTypeEnum;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.crud.*;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Objects;

import static com.gmail.tomasatrat.ui.utils.Constants.NOTIFICATION_DURATION;
import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_ORDERS;

@Route(value = PAGE_ORDERS, layout = MainView.class)
@PageTitle(Constants.TITLE_ORDERS)
public class OrdersView extends VerticalLayout implements HasLogger {
    //region Services
    private final OrderService orderService;
    private final ExpeditionTypeService expeditionTypeService;
    private final BranchService branchService;
    private final ProductService productService;
    //endregion

    //region Components
    private GenericDataProvider<OrderInfo> dataProvider;

    private Crud<OrderInfo> crud;
    private Grid<OrderInfo> grid;
    private Dialog detailsForm;
    private Grid<OrderDetail> detailGrid;

    private TextField document;
    private TextField firstName;
    private TextField lastName;
    private EmailField email;
    private TextField phone;
    private TextField address;
    private Checkbox acceptPartial;
    private TextField description1;
    private TextField description2;
    private TextField description3;
    private TextField description4;
    private ComboBox<ExpeditionType> expeditionType;
    private ComboBox<Branch> branch;
    private ComboBox<Barcode> barcodeComboBox;
    private TextField sizeField;
    private NumberField numberField;
    private TextField colourField;
    private ComboBox<Product> productComboBox;
    //endregion

    @Autowired
    public OrdersView(OrderService orderService,
                      ExpeditionTypeService expeditionTypeService,
                      BranchService branchService,
                      ProductService productService) {
        this.orderService = orderService;
        this.expeditionTypeService = expeditionTypeService;
        this.branchService = branchService;
        this.productService = productService;
        setAlignItems(Alignment.CENTER);

        setupGrid();

        setupCrud();

        Button newItemButton = new Button("Nuevo pedido");
        newItemButton.addClickListener(e -> crud.edit(new OrderInfo(), Crud.EditMode.NEW_ITEM));

        this.add(new H2("Panel de pedidos"), newItemButton, crud);
    }

    private void setupGrid() {
        grid = new Grid<>();
        grid.setColumnReorderingAllowed(true);
        grid.addColumn(createActionsMenuBar()).setHeader("").setAutoWidth(true);
        grid.addColumn(order -> order.getCustomer().getId()).setHeader("Documento").setAutoWidth(true).setResizable(true);
        grid.addColumn(order -> order.getCustomer().getName() + " " + order.getCustomer().getLastName()).setHeader("Nombre")
                .setAutoWidth(true).setResizable(true);
        grid.addColumn(order -> order.getCustomer().getEmail()).setHeader("Email").setAutoWidth(true).setResizable(true);
        grid.addColumn(order -> order.getCustomer().getPhoneNumber()).setHeader("Teléfono").setAutoWidth(true).setResizable(true);
        grid.addColumn(order -> order.getBranch().getDescription()).setHeader("Sucursal").setAutoWidth(true).setResizable(true);
        grid.addColumn(order -> order.getExpedition().getDescription()).setHeader("Tp. Expedición").setAutoWidth(true).setResizable(true);
        grid.addColumn(order -> DateConverter.convert(order.getAddrowDate())).setHeader("Fecha alta").setAutoWidth(true).setResizable(true);
        grid.addColumn(OrderInfo::getAddress).setHeader("Dirección").setAutoWidth(true).setResizable(true);
    }

    private final SerializableBiConsumer<MenuBar, OrderInfo> statusComponentUpdater = (menuBar, order) -> {
        menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);

        MenuItem actions = createIconItem(menuBar, VaadinIcon.BULLETS, null, null);

        MenuItem addDetail = createIconItem(actions.getSubMenu(), VaadinIcon.BULLETS, "Detalles", null, true);

        addDetail.addClickListener(e -> openDetailsForm(order));
    };

    private ComponentRenderer<MenuBar, OrderInfo> createActionsMenuBar() {
        return new ComponentRenderer<>(MenuBar::new, statusComponentUpdater);
    }

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

    private void setupCrud() {
        crud = new Crud<>(OrderInfo.class, grid, createOrdersEditor());

        setupDataProvider();

        crud.addThemeVariants(CrudVariant.NO_BORDER);

        crud.setI18n(createSpanishI18n());
    }

    private CrudI18n createSpanishI18n() {
        CrudI18n spanishI18n = CrudI18n.createDefault();

        spanishI18n.setNewItem("Nuevo pedido");
        spanishI18n.setEditItem("Editar pedido");
        spanishI18n.setSaveItem("Guardar cambios");
        spanishI18n.setDeleteItem("Borrar pedido");
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

    private void setupDataProvider() {
        dataProvider = new GenericDataProvider(orderService);

        crud.setDataProvider(dataProvider);
        crud.addSaveListener(e -> {
            saveOrder(e.getItem(), dataProvider);
            document.clear();
            firstName.clear();
            lastName.clear();
            email.clear();
            phone.clear();
        });

        crud.addDeleteListener(e -> dataProvider.delete(e.getItem()));
    }

    private OrderInfo saveOrder(OrderInfo item, GenericDataProvider<OrderInfo> dataProvider) {
        try {
            OrderInfo orderInfo = getOrderIfNotBindedFieldsAreValid(item);
            var order = dataProvider.persist(orderInfo);
            Notification.show("El pedido ha sido añadido correctamente", 5000, Notification.Position.BOTTOM_CENTER);
            return (OrderInfo) order;
        } catch (SmartStoreException ex) {
            Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER);
        }
        return null;
    }

    private OrderInfo getOrderIfNotBindedFieldsAreValid(OrderInfo order) throws SmartStoreException {
        validateNotBindedFields();
        order.setCustomer(getCustomer());
        order.setAddrowDate(new Date());
        return order;
    }

    private Customer getCustomer() {
        Customer customer = new Customer();
        customer.setId(document.getValue());
        customer.setName(firstName.getValue());
        customer.setLastName(lastName.getValue());
        customer.setEmail(email.getValue());
        customer.setPhoneNumber(phone.getValue());
        return customer;
    }

    private void validateNotBindedFields() throws SmartStoreException {
        if (requiredFieldsAreEmpty())
            throw new SmartStoreException("El documento, nombre y apellido del cliente son requeridos");

        if (bothContactMeansAreEmpty())
            throw new SmartStoreException("Se necesita especificar al menos un medio de contacto con el cliente");

        if (expeditionTypeIsBOPISNotAcceptsPartialExpedition())
            throw new SmartStoreException("Un pedido de tipo BOPIS no puede aceptar expedición parcial");

        if (addressIsEmptyWhenExpeditionTypeIsSend())
            throw new SmartStoreException("Se necesita especificar una dirección cuando el tipo de expedición es Envía");
    }

    private boolean expeditionTypeIsBOPISNotAcceptsPartialExpedition() {
        return expeditionType.getValue().getId() == ExpeditionTypeEnum.BOPIS.getValue() && acceptPartial.getValue();
    }

    private boolean addressIsEmptyWhenExpeditionTypeIsSend() {
        return address.isEmpty() && Objects.equals(expeditionType.getValue().getId(), (long) (ExpeditionTypeEnum.SEND_TO_ADDRESS.getValue()));
    }

    private boolean bothContactMeansAreEmpty() {
        return email.isEmpty() && phone.isEmpty();
    }

    private boolean requiredFieldsAreEmpty() {
        return document.isEmpty() || firstName.isEmpty() || lastName.isEmpty();
    }

    private CrudEditor<OrderInfo> createOrdersEditor() {
        setupFormFields();

        FormLayout form = new FormLayout();

        HorizontalLayout contact = new HorizontalLayout();
        contact.add(email, phone);

        form.add(document, contact, firstName, lastName, branch, expeditionType, acceptPartial, address, description1, description2,
                description3, description4);

        contact.setAlignItems(Alignment.BASELINE);

        Binder<OrderInfo> binder = getBinder();

        return new BinderCrudEditor<>(binder, form);
    }

    private void openDetailsForm(OrderInfo order) {
        detailsForm = new Dialog();
        detailsForm.setWidthFull();
        detailsForm.getElement().setAttribute("aria-label", "Añadir detalles");
        FormLayout dialogLayout = createDetailsFormLayout();
        detailGrid = new Grid<>();

        detailGrid.setItems(this.orderService.getOrderDetailsByOrder(order));

        detailGrid.setColumnReorderingAllowed(true);
        detailGrid.addColumn(detail -> detail.getBarcode().getProductCode().getDescription()).setHeader("Producto").setAutoWidth(true).setResizable(true);
        detailGrid.addColumn(OrderDetail::getOrderedQuantity).setHeader("Cantidad pedida").setAutoWidth(true).setResizable(true);

        Button submitButton = new Button("Guardar cambios");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClickListener(e -> saveDetail(order));

        Button discardChanges = new Button("Descartar cambios");
        discardChanges.addClickListener(i -> detailsForm.close());

        HorizontalLayout actions = new HorizontalLayout(discardChanges, submitButton);
        actions.setAlignItems(Alignment.END);

        detailsForm.add(dialogLayout, new Hr(), detailGrid, actions);
        detailsForm.open();
    }

    private FormLayout createDetailsFormLayout() {
        FormLayout layout = new FormLayout();

        productComboBox = new ComboBox<>("Producto genérico");
        productComboBox.setItems(productService.findAll());

        barcodeComboBox = new ComboBox<>("Producto específico");

        productComboBox.addValueChangeListener(e -> barcodeComboBox.setItems(this.productService.getBarcodesByProduct(e.getValue())));

        productComboBox.setItemLabelGenerator(product ->
                product.getId() + " - " + product.getDescription());

        sizeField = new TextField("Tamaño");
        sizeField.setEnabled(false);

        colourField = new TextField("Color");
        colourField.setEnabled(false);

        barcodeComboBox.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                sizeField.setValue(e.getValue().getSize());
                colourField.setValue(e.getValue().getColour());
            }
        });

        barcodeComboBox.setItemLabelGenerator(barcode ->
                barcode.getId() + " - " + barcode.getColour() + " | " + barcode.getSize());

        numberField = new NumberField("Cantidad");
        numberField.setMin(1);

        HorizontalLayout readOnlyFields = new HorizontalLayout(sizeField, colourField, numberField);
        readOnlyFields.setAlignItems(Alignment.BASELINE);

        layout.add(productComboBox, barcodeComboBox, readOnlyFields);

        layout.setColspan(readOnlyFields, 1);

        return layout;
    }

    private void saveDetail(OrderInfo order) {
        try {
            Product product = productComboBox.getValue();
            Barcode barcode = barcodeComboBox.getValue();
            int orderedQty = numberField.getValue().intValue();

            validateValues(product, barcode, orderedQty);

            addDetail(order, barcode, orderedQty);

            cleanFields();

            Notification.show("Detalle añadido correctamente", 5000, Notification.Position.BOTTOM_CENTER);

            detailGrid.setItems(this.orderService.getOrderDetailsByOrder(order));
        } catch (SmartStoreException ex) {
            Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER);
        }
    }

    private void addDetail(OrderInfo order, Barcode barcode, int orderedQty) throws SmartStoreException {
        OrderDetail detail = new OrderDetail();
        detail.setOrderInfo(order);
        detail.setBarcode(barcode);
        detail.setOrderedQuantity(orderedQty);

        this.orderService.addDetail(detail);
    }

    private void cleanFields() {
        productComboBox.setValue(null);
        barcodeComboBox.setValue(null);
        numberField.setValue(1.0);
        sizeField.setValue("");
        colourField.setValue("");
    }

    private void validateValues(Product product, Barcode barcode, int orderedQty) throws SmartStoreException {
        if (product == null)
            throw new SmartStoreException("El campo para el producto genérico no puede estar vacío");

        if (barcode == null)
            throw new SmartStoreException("El campo para el producto específico no puede estar vacío");

        if (orderedQty == 0)
            throw new SmartStoreException("No se puede ingresar una cantidad menor a 1");
    }

    private Binder<OrderInfo> getBinder() {
        Binder<OrderInfo> binder = new Binder<>(OrderInfo.class);
        binder.bind(address, OrderInfo::getAddress, OrderInfo::setAddress);
        binder.bind(acceptPartial, OrderInfo::getAcceptsPartialExpedition, OrderInfo::setAcceptsPartialExpedition);
        binder.bind(description1, OrderInfo::getDescription1, OrderInfo::setDescription1);
        binder.bind(description2, OrderInfo::getDescription2, OrderInfo::setDescription2);
        binder.bind(description3, OrderInfo::getDescription3, OrderInfo::setDescription3);
        binder.bind(description4, OrderInfo::getDescription4, OrderInfo::setDescription4);
        binder.bind(expeditionType, OrderInfo::getExpedition, OrderInfo::setExpedition);
        binder.bind(branch, OrderInfo::getBranch, OrderInfo::setBranch);

        return binder;
    }

    private void setupFormFields() {
        document = new TextField("Documento de identidad");
        document.setRequired(true);
        firstName = new TextField("Nombre");
        firstName.setRequired(true);
        lastName = new TextField("Apellido");
        lastName.setRequired(true);
        email = new EmailField("Email");
        phone = new TextField("Teléfono");
        expeditionType = new ComboBox<>("Tipo expedición");
        expeditionType.setItems(expeditionTypeService.getExpeditionTypes());

        expeditionType.setItemLabelGenerator(expeditionType1 ->
                expeditionType1.getId() + " - " + expeditionType1.getDescription());

        expeditionType.setRequired(true);

        branch = new ComboBox<>("Sucursal");
        branch.setItems(branchService.findAll());

        branch.setItemLabelGenerator(b ->
                b.getId() + " - " + b.getDescription());

        branch.setRequired(true);
        address = new TextField("Dirección");
        acceptPartial = new Checkbox("Acepta expedición parcial");
        description1 = new TextField("Anexo 1");
        description2 = new TextField("Anexo 2");
        description3 = new TextField("Anexo 3");
        description4 = new TextField("Anexo 4");
    }
}
