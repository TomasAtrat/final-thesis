package com.gmail.tomasatrat.ui.views.admin.users;

import com.gmail.tomasatrat.app.security.CurrentUser;
import com.gmail.tomasatrat.backend.data.Role;
import com.gmail.tomasatrat.backend.data.entity.User;
import com.gmail.tomasatrat.backend.service.UserService;
import com.gmail.tomasatrat.ui.MainView;
import com.gmail.tomasatrat.ui.crud.AbstractBakeryCrudView;
import com.gmail.tomasatrat.ui.utils.Constants;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_USERS;

@Route(value = PAGE_USERS, layout = MainView.class)
@PageTitle(Constants.TITLE_USERS)
@Secured(Role.ADMIN)
public class UsersView extends AbstractBakeryCrudView<User> {

    private UserService userService;
    private static String INACTIVATE_USER_PROMPT = "¿Quieres desactivar al usuario?";
    private static String ACTIVATE_USER_PROMPT = "¿Quieres activar al usuario?";

    @Autowired
    public UsersView(UserService service, CurrentUser currentUser, PasswordEncoder passwordEncoder) {
        super(User.class, service, new Grid<>(), createForm(passwordEncoder), currentUser);
        userService = service;
    }

    @Override
    public void setupGrid(Grid<User> grid) {
        grid.addColumn(User::getEmail).setAutoWidth(true).setHeader("Nombre usuario").setFlexGrow(5);
        grid.addColumn(User::getEmail).setAutoWidth(true).setHeader("Email").setFlexGrow(5);
        grid.addColumn(u -> u.getFirstName() + " " + u.getLastName()).setHeader("Nombre").setAutoWidth(true).setFlexGrow(5);
        grid.addColumn(User::getRole).setHeader("Rol").setAutoWidth(true);
/*
        grid.addColumn(createSwitchComponentRenderer()).setHeader("Activo");
*/
    }

/*
    private final SerializableBiConsumer<ToggleButton, User> statusComponentUpdater = (toggle, user) -> {
        toggle.setValue(user.isActive());
        toggle.addValueChangeListener(i -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            if (i.getValue()) themeList.add(Lumo.DARK);
            else themeList.remove(Lumo.DARK);
            ConfirmDialog dialog = new ConfirmDialog();
            var header = user.isActive() ? INACTIVATE_USER_PROMPT : ACTIVATE_USER_PROMPT;
            dialog.setHeader(header);
            dialog.setText("Are you sure you want to permanently delete this item?");

            dialog.setCancelable(true);
            dialog.addCancelListener(event -> dialog.close());

            dialog.setConfirmText("Guardar");
            dialog.addConfirmListener(event -> {
                userService.toggleStatus(user);
                dialog.close();
            });
            dialog.open();
        });
    };
*/

    /*private ComponentRenderer createSwitchComponentRenderer() {
        return new ComponentRenderer<>(ToggleButton::new, statusComponentUpdater);
    }*/

    @Override
    protected String getBasePage() {
        return PAGE_USERS;
    }

    private static BinderCrudEditor<User> createForm(PasswordEncoder passwordEncoder) {
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

        FormLayout form = new FormLayout(username, email, first, last, password, role);

        BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);

        ListDataProvider<String> roleProvider = DataProvider.ofItems(Role.getBusinessRoles());
        role.setItemLabelGenerator(s -> s != null ? s : "");
        role.setDataProvider(roleProvider);

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


}
