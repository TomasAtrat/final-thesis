package com.gmail.tomasatrat.ui.views.admin.readers;

import com.gmail.tomasatrat.app.HasLogger;
import com.gmail.tomasatrat.backend.data.Role;
import com.gmail.tomasatrat.backend.data.entity.Reader;
import com.gmail.tomasatrat.backend.data.entity.Task;
import com.gmail.tomasatrat.backend.microservices.reader.services.ReaderService;
import com.gmail.tomasatrat.backend.microservices.tasks.services.TaskService;
import com.gmail.tomasatrat.ui.MainView;
import com.gmail.tomasatrat.ui.crud.GenericDataProvider;
import com.gmail.tomasatrat.ui.utils.Constants;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.crud.*;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_READER;

@Route(value = PAGE_READER, layout = MainView.class)
@PageTitle(Constants.TITLE_READERS)
@Secured(Role.ADMIN)
public class ReadersView extends VerticalLayout implements HasLogger {

    private final ReaderService readerService;

    private TextField name;
    private TextField alias;
    private Select<Float> antenaPower;
    private Select<Float> RSSI;

    private Crud<Reader> crud;

    public ReadersView(ReaderService readerService) {
        this.readerService = readerService;

        setupCrud();

        setupGrid();

        this.add(crud);
    }

    private void setupGrid() {
        Grid<Reader> grid = crud.getGrid();

        List<String> visibleColumns = Arrays.asList(
                "name",
                "alias",
                "flActive",
                "antenaPower",
                "RSSI",
                "vaadin-crud-edit-column"
        );

        grid.getColumns().forEach(column -> {
            String key = column.getKey();
            if (!visibleColumns.contains(key)) {
                grid.removeColumn(column);
            }
        });

        grid.setColumnOrder(
                grid.getColumnByKey("name"),
                grid.getColumnByKey("alias"),
                grid.getColumnByKey("antenaPower"),
                grid.getColumnByKey("RSSI"),
                grid.getColumnByKey("flActive"),
                grid.getColumnByKey("vaadin-crud-edit-column")
        );

        grid.getColumnByKey("name").setHeader("Nombre");
        grid.getColumnByKey("alias").setHeader("Alias");
        grid.getColumnByKey("antenaPower").setHeader("Poder de antena");
        grid.getColumnByKey("RSSI").setHeader("RSSI");
        grid.removeColumnByKey("flActive");

        grid.addComponentColumn((item) -> {
                    Icon icon;
                    if(item.getFlActive()){ // change this to your own getter for the boolean value
                        icon = VaadinIcon.CHECK.create();
                        icon.setColor("green");
                    } else {
                        icon = VaadinIcon.CLOSE.create();
                        icon.setColor("red");
                    }
                    return icon;
                })
                .setKey("flActive")
                .setHeader("Activo") // set your own column key ;)
                .setComparator(Comparator.comparing(Reader::getFlActive)); // change this to your own getter for the boolean value
    }

    private void setupCrud() {
        crud = new Crud<>(Reader.class, createOrdersEditor());

        setupDataProvider();

        Button newItemButton = new Button("Nuevo lector RFID");
        newItemButton.addClickListener(e -> crud.edit(new Reader(), Crud.EditMode.NEW_ITEM));

        crud.setToolbar(newItemButton);

        crud.addThemeVariants(CrudVariant.NO_BORDER);

        crud.setI18n(createSpanishI18n());
    }

    private CrudEditor<Reader> createOrdersEditor() {
        setupFormFields();

        FormLayout form = new FormLayout();

        form.add(name, alias, antenaPower, RSSI);

        Binder<Reader> binder = getBinder();

        return new BinderCrudEditor<>(binder, form);
    }

    private void setupFormFields() {
        name = new TextField("Nombre");
        alias = new TextField("Alias");
        antenaPower = new Select<>(
                -12f,
                -15f,
                -18f,
                -21f,
                -24f,
                -27f,
                -30f
        );
        antenaPower.setLabel("Poder de antena");
        RSSI = new Select<>(
                -40f,
                -45f,
                -50f,
                -55f,
                -60f,
                -65f,
                -70f,
                -75f,
                -80f
        );
        RSSI.setLabel("RSSI");
    }

    private Binder<Reader> getBinder() {
        Binder<Reader> binder = new Binder<>(Reader.class);
        binder.bind(name, Reader::getName, Reader::setName);
        binder.bind(alias, Reader::getAlias, Reader::setAlias);
        binder.bind(antenaPower, Reader::getAntenaPower, Reader::setAntenaPower);
        binder.bind(RSSI, Reader::getRSSI, Reader::setRSSI);
        return binder;
    }

    private void setupDataProvider() {
        GenericDataProvider<Reader> dataProvider = new GenericDataProvider<>(readerService);

        crud.setDataProvider(dataProvider);
        crud.addSaveListener(e -> saveTask(e.getItem(), dataProvider));
        crud.addDeleteListener(e -> dataProvider.delete(e.getItem()));
    }

    private void saveTask(Reader item, GenericDataProvider<Reader> dataProvider) {
        if (validateFields()) {
            dataProvider.persist(item);
        }
    }

    private boolean validateFields() {
        boolean rest = true;

        if (this.name.isEmpty()) {
            newErrorMsg("El nombre es requerido");
            rest = false;
        }

        if (this.alias.isEmpty()) {
            newErrorMsg("El alias es requerido");
            rest = false;
        }

        if (this.antenaPower.getValue() == 0f) {
            newErrorMsg("El poder de antena es requerido");
            rest = false;
        }

        if (this.RSSI.getValue() == 0f) {
            newErrorMsg("El valor RSSI es requerido");
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
        notification.setDuration(80000);
        notification.open();
    }

    private CrudI18n createSpanishI18n() {
        CrudI18n spanishI18n = CrudI18n.createDefault();

        spanishI18n.setNewItem("Nuevo lector RFID");
        spanishI18n.setEditItem("Editar lector RFID");
        spanishI18n.setSaveItem("Guardar cambios");
        spanishI18n.setDeleteItem("Borrar lector RFID");
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
