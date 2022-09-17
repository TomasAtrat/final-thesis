package com.gmail.tomasatrat.ui.views.users;

import com.gmail.tomasatrat.app.security.CurrentUser;
import com.gmail.tomasatrat.backend.data.Role;
import com.gmail.tomasatrat.backend.data.entity.User;
import com.gmail.tomasatrat.backend.service.UserService;
import com.gmail.tomasatrat.ui.MainView;
import com.gmail.tomasatrat.ui.dataproviders.GenericDataProvider;
import com.gmail.tomasatrat.ui.utils.Constants;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.crud.*;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_USERS;

@Route(value = PAGE_USERS, layout = MainView.class)
@PageTitle(Constants.TITLE_USERS)
@Secured(Role.ADMIN)
public class UsersView extends VerticalLayout {

    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private static String INACTIVATE_USER_PROMPT = "¿Quieres desactivar al usuario?";
    private static String ACTIVATE_USER_PROMPT = "¿Quieres activar al usuario?";
    private GenericDataProvider<User> dataProvider;


    private Crud<User> crud;
    private Grid<User> grid;

    @Autowired
    public UsersView(UserService service, PasswordEncoder passwordEncoder) {
        userService = service;
        this.passwordEncoder = passwordEncoder;
        setAlignItems(Alignment.CENTER);

        setupGrid();

        setupCrud();

        Button newItemButton = new Button("Nuevo usuario");
        newItemButton.addClickListener(e -> crud.edit(new User(), Crud.EditMode.NEW_ITEM));

        this.add(new H2("Panel de usuarios"), newItemButton, crud);
    }

    private void setupGrid() {
        grid = new Grid<>();
        grid.setColumnReorderingAllowed(true);
        grid.addColumn(User::getEmail).setAutoWidth(true).setHeader("Nombre usuario").setResizable(true);
        grid.addColumn(User::getEmail).setAutoWidth(true).setHeader("Email").setResizable(true);
        grid.addColumn(u -> u.getFirstName() + " " + u.getLastName()).setHeader("Nombre").setAutoWidth(true).setResizable(true);
        grid.addColumn(User::getRole).setHeader("Rol").setAutoWidth(true);
        grid.addColumn(createSwitchComponentRenderer()).setHeader("Activo");
    }

    private final SerializableBiConsumer<ToggleButton, User> statusComponentUpdater = (toggle, user) -> {
        toggle.setValue(user.isActive());
        toggle.addValueChangeListener(i -> {
            ConfirmDialog dialog = new ConfirmDialog();
            var header = user.isActive() ? INACTIVATE_USER_PROMPT : ACTIVATE_USER_PROMPT;
            dialog.setHeader(header);
            dialog.setText("¿Estás seguro que quieres realizar esta operación?");

            dialog.setCancelable(true);
            dialog.addCancelListener(event -> {
                dialog.close();
            });

            dialog.setConfirmText("Guardar");
            dialog.addConfirmListener(event -> {
                userService.toggleStatus(user);
                dialog.close();
                Notification.show("Se han guardado los cambios", 5000, Notification.Position.BOTTOM_CENTER);
            });

            dialog.open();
        });
    };


    private ComponentRenderer createSwitchComponentRenderer() {
        return new ComponentRenderer<>(ToggleButton::new, statusComponentUpdater);
    }

    private void setupCrud() {
        crud = new Crud<>(User.class, grid, createUsersEditor());

        setupDataProvider();

        crud.addThemeVariants(CrudVariant.NO_BORDER);

        crud.setI18n(createSpanishI18n());
    }

    private CrudEditor<User> createUsersEditor() {
        TextField username = new TextField("Nombre de usuario");
        username.getElement().setAttribute("colspan", "2");
        EmailField email = new EmailField("Email");
        email.getElement().setAttribute("colspan", "2");
        TextField first = new TextField("Nombre");
        TextField last = new TextField("Apellido");
        PasswordField password = new PasswordField("Contraseña");
        password.getElement().setAttribute("colspan", "2");
        ComboBox<String> role = new ComboBox<>();
        role.getElement().setAttribute("colspan", "2");
        role.setLabel("Rol");
        role.setItems(Role.getBusinessRoles());
        role.setItemLabelGenerator(s -> s != null ? s : "");

        FormLayout form = new FormLayout(username, email, first, last, password, role);

        BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);

        binder.bind(username, "username");
        binder.bind(first, "firstName");
        binder.bind(last, "lastName");
        binder.bind(email, "email");
        binder.bind(role, "role");

        binder.forField(password)
                .withValidator(pass -> pass.matches("^(|(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,})$"),
                        "need 6 or more chars, mixing digits, lowercase and uppercase letters")
                .bind(user -> password.getEmptyValue(), (user, pass) -> {
                    if (!password.getEmptyValue().equals(pass)) {
                        user.setPasswordHash(passwordEncoder.encode(pass));
                    }
                });

        return new BinderCrudEditor<User>(binder, form);
    }

    private CrudI18n createSpanishI18n() {
        CrudI18n spanishI18n = CrudI18n.createDefault();

        spanishI18n.setNewItem("Nuevo usuario");
        spanishI18n.setEditItem("Editar usuario");
        spanishI18n.setSaveItem("Guardar cambios");
        spanishI18n.setDeleteItem("Borrar usuario");
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
        dataProvider = new GenericDataProvider(userService);

        crud.setDataProvider(dataProvider);
        crud.addSaveListener(e ->  dataProvider.persist(e.getItem()));
        crud.addDeleteListener(e -> dataProvider.delete(e.getItem()));
    }

}
