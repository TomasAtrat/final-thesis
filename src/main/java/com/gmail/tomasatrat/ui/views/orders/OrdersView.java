package com.gmail.tomasatrat.ui.views.orders;

import com.gmail.tomasatrat.app.HasLogger;
import com.gmail.tomasatrat.backend.common.exceptions.SmartStoreException;
import com.gmail.tomasatrat.backend.data.entity.Branch;
import com.gmail.tomasatrat.backend.data.entity.Customer;
import com.gmail.tomasatrat.backend.data.entity.ExpeditionType;
import com.gmail.tomasatrat.backend.data.entity.OrderInfo;
import com.gmail.tomasatrat.backend.microservices.expedition.ExpeditionTypeService;
import com.gmail.tomasatrat.backend.microservices.orders.services.OrderService;
import com.gmail.tomasatrat.backend.service.BranchService;
import com.gmail.tomasatrat.ui.MainView;
import com.gmail.tomasatrat.ui.crud.GenericDataProvider;
import com.gmail.tomasatrat.ui.layout.size.Horizontal;
import com.gmail.tomasatrat.ui.utils.Constants;
import com.gmail.tomasatrat.ui.views.orders.enums.ExpeditionTypeEnum;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.*;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Objects;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_ORDERS;

@Route(value = PAGE_ORDERS, layout = MainView.class)
@PageTitle(Constants.TITLE_ORDERS)
public class OrdersView extends VerticalLayout implements HasLogger {
    private final OrderService orderService;
    private final ExpeditionTypeService expeditionTypeService;
    private BranchService branchService;

    private Crud<OrderInfo> crud;
    private Grid<OrderInfo> grid;

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

    @Autowired
    public OrdersView(OrderService orderService, ExpeditionTypeService expeditionTypeService, BranchService branchService) {
        this.orderService = orderService;
        this.expeditionTypeService = expeditionTypeService;
        this.branchService = branchService;

        setupGrid();

        setupCrud();

        this.add(crud);
    }

    private void setupGrid() {
        grid = new Grid<>();
        grid.setColumnReorderingAllowed(true);
        grid.addColumn(order -> order.getCustomer().getId()).setHeader("Documento").setAutoWidth(true).setResizable(true);
        grid.addColumn(order -> order.getCustomer().getName() + " " + order.getCustomer().getLastName()).setHeader("Nombre")
                .setAutoWidth(true).setResizable(true);
        grid.addColumn(order -> order.getCustomer().getEmail()).setHeader("Email").setAutoWidth(true).setResizable(true);
        grid.addColumn(order -> order.getCustomer().getPhoneNumber()).setHeader("Teléfono").setAutoWidth(true).setResizable(true);
        grid.addColumn(order -> order.getBranch().getDescription()).setHeader("Sucursal").setAutoWidth(true).setResizable(true);
        grid.addColumn(order -> order.getExpedition().getDescription()).setHeader("Tp. Expedición").setAutoWidth(true).setResizable(true);
        grid.addColumn(OrderInfo::getAddrowDate).setHeader("Fecha alta").setAutoWidth(true).setResizable(true);
        grid.addColumn(OrderInfo::getAddress).setHeader("Dirección").setAutoWidth(true).setResizable(true);

    }

    private void setupCrud() {
        crud = new Crud<>(OrderInfo.class, grid, createOrdersEditor());

        setupDataProvider();

        Button newItemButton = new Button("Nuevo pedido");
        newItemButton.addClickListener(e -> crud.edit(new OrderInfo(), Crud.EditMode.NEW_ITEM));

        Span footer = new Span();
        footer.getElement().getStyle().set("flex", "1");

        crud.setToolbar(footer, newItemButton);

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
        GenericDataProvider<OrderInfo> dataProvider = new GenericDataProvider(orderService);

        crud.setDataProvider(dataProvider);
        crud.addSaveListener(e -> saveOrder(e.getItem(), dataProvider));
        crud.addDeleteListener(e -> dataProvider.delete(e.getItem()));
    }

    private void saveOrder(OrderInfo item, GenericDataProvider<OrderInfo> dataProvider) {
        try {
            OrderInfo orderInfo = getOrderIfNotBindedFieldsAreValid(item);
            dataProvider.persist(orderInfo);
        } catch (SmartStoreException ex) {
            Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER);
        }
    }

    private OrderInfo getOrderIfNotBindedFieldsAreValid(OrderInfo order) throws SmartStoreException {
        validateNotBindedFields();
        order.setCustomer(getCustomer());
        order.setAddrowDate(LocalDate.now());
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

        if (addressIsEmptyWhenExpeditionTypeIsSend())
            throw new SmartStoreException("Se necesita especificar una dirección cuando el tipo de expedición es Envía");
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

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        horizontalLayout.add(address, branch, acceptPartial);

        form.add(document, expeditionType, firstName, lastName, email, phone, horizontalLayout, description1, description2,
                description3, description4);

        Binder<OrderInfo> binder = getBinder();

        return new BinderCrudEditor<>(binder, form);
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
