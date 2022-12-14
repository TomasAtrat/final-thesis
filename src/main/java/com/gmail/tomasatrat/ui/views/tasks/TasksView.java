package com.gmail.tomasatrat.ui.views.tasks;

import com.gmail.tomasatrat.app.HasLogger;
import com.gmail.tomasatrat.backend.common.enums.PriorityEnum;
import com.gmail.tomasatrat.backend.data.Role;
import com.gmail.tomasatrat.backend.data.entity.Task;
import com.gmail.tomasatrat.backend.data.entity.User;
import com.gmail.tomasatrat.backend.microservices.tasks.services.TaskService;
import com.gmail.tomasatrat.backend.service.UserService;
import com.gmail.tomasatrat.ui.MainView;
import com.gmail.tomasatrat.ui.dataproviders.GenericDataProvider;
import com.gmail.tomasatrat.ui.utils.Constants;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.*;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_TASKS;

@Route(value = PAGE_TASKS, layout = MainView.class)
@PageTitle(Constants.TITLE_TASKS)
@Secured({Role.EMPLOYEE, Role.ADMIN})
public class TasksView extends VerticalLayout implements HasLogger {

    private final TaskService taskService;

    private final UserService userService;

    private Grid<Task> grid;

    private Crud<Task> crud;

    private TextField title;
    private TextField description;
    private Select<String> category;
    private Select<String> priority;
    private Select<String> state;

    private ComboBox<User> users;

    @Autowired
    public TasksView(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;

        setAlignItems(Alignment.CENTER);

        setupGrid();

        Button newItemButton = new Button("Nueva tarea");
        newItemButton.addClickListener(e -> crud.edit(new Task(), Crud.EditMode.NEW_ITEM));

        setupCrud();

        this.add(crud);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().anyMatch(i -> i.getAuthority().equals("admin")))
            this.add(new H2("Panel de tareas"), newItemButton, crud);
        else
            this.add(new H2("Panel de tareas"), crud);
    }

    private void setupGrid() {
        grid = new Grid<Task>();
        Crud.addEditColumn(grid);
        grid.setColumnReorderingAllowed(true);
        grid.addColumn(Task::getTitle).setHeader("Titulo").setAutoWidth(true).setResizable(true);
        grid.addColumn(Task::getDescription).setHeader("Descripci??n").setAutoWidth(true).setResizable(true);
        grid.addColumn(Task::getPriority).setHeader("Prioridad").setAutoWidth(true).setResizable(true);
        grid.addColumn(Task::getState).setHeader("Estado").setAutoWidth(true).setResizable(true);
        grid.addColumn(Task::getCategory).setHeader("Categor??a").setAutoWidth(true).setResizable(true);
        grid.addColumn(task -> task.getUserId().getUsername()).setHeader("Usuario").setWidth("90px").setResizable(true);
    }

    private void setupDataProvider() {
        GenericDataProvider<Task> dataProvider = new GenericDataProvider<>(taskService);

        crud.setDataProvider(dataProvider);
        crud.addSaveListener(e -> saveTask(e.getItem(), dataProvider));
        crud.addDeleteListener(e -> dataProvider.delete(e.getItem()));
    }

    private void saveTask(Task item, GenericDataProvider<Task> dataProvider) {
        if (validateFields()) {
            dataProvider.persist(item);
        }
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

    private boolean validateFields() {
        boolean rest = true;

        if (this.category.isEmpty()) {
            newErrorMsg("La categor??a es requerida");
            rest = false;
        }

        if (this.state.isEmpty()) {
            newErrorMsg("El estado es requerido");
            rest = false;
        }

        if (this.priority.isEmpty()) {
            newErrorMsg("La prioridad es requerida");
            rest = false;
        }

        if (this.title.isEmpty()) {
            newErrorMsg("El t??tulo es requerido");
            rest = false;
        }

        if (this.description.isEmpty()) {
            newErrorMsg("La descripci??n es requerida");
            rest = false;
        }

        return rest;
    }

    private void setupCrud() {
        crud = new Crud<>(Task.class, grid, createTaskEditor());

        setupDataProvider();

        crud.addThemeVariants(CrudVariant.NO_BORDER);

        crud.setI18n(createSpanishI18n());
    }

    private CrudI18n createSpanishI18n() {
        CrudI18n spanishI18n = CrudI18n.createDefault();

        spanishI18n.setNewItem("Nueva tarea");
        spanishI18n.setEditItem("Editar tarea");
        spanishI18n.setSaveItem("Guardar cambios");
        spanishI18n.setDeleteItem("Borrar tarea");
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

    private CrudEditor<Task> createTaskEditor() {
        setupFormFields();

        FormLayout form = new FormLayout();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream().anyMatch(i -> i.getAuthority().equals("Empleado")))
            users.setEnabled(false);

        form.add(title, description, category, priority, state, users);

        Binder<Task> binder = getBinder();

        return new BinderCrudEditor<>(binder, form);
    }

    private Binder<Task> getBinder() {
        Binder<Task> binder = new Binder<>(Task.class);
        binder.bind(title, Task::getTitle, Task::setTitle);
        binder.bind(description, Task::getDescription, Task::setDescription);
        binder.bind(category, Task::getCategory, Task::setCategory);
        binder.bind(priority, Task::getPriority, Task::setPriority);
        binder.bind(state, Task::getState, Task::setState);
        binder.bind(users, Task::getUserId, Task::setUserId);

        return binder;
    }

    private void setupFormFields() {
        title = new TextField("T??tulo");
        description = new TextField("Descripci??n");
        category = new Select<>("SAL??N", "DEP??SITO", "OTROS");
        category.setLabel("Categor??a");
        priority = new Select<>(PriorityEnum.getPriorities());
        priority.setLabel("Prioridad");
        state = new Select<>("COMPLETADA", "EN PROCESO", "CANCELADA", "PENDIENTE");
        state.setLabel("Estado");
        users = new ComboBox<>("Usuario");
        users.setItems(userService.findAllByActiveIsTrue());
        users.setItemLabelGenerator(user ->
                user.getId() + " - " + user.getUsername());
    }
}
