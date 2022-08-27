package com.gmail.tomasatrat.ui.views.admin.users;

import com.gmail.tomasatrat.app.security.CurrentUser;
import com.gmail.tomasatrat.backend.data.Role;
import com.gmail.tomasatrat.backend.data.entity.User;
import com.gmail.tomasatrat.backend.service.UserService;
import com.gmail.tomasatrat.ui.MainView;
import com.gmail.tomasatrat.ui.crud.AbstractBakeryCrudView;
import com.gmail.tomasatrat.ui.utils.Constants;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_USERS;

@Route(value = PAGE_USERS, layout = MainView.class)
@PageTitle(Constants.TITLE_USERS)
@Secured(Role.ADMIN)
public class UsersView extends AbstractBakeryCrudView<User> {

	@Autowired
	public UsersView(UserService service, CurrentUser currentUser, PasswordEncoder passwordEncoder) {
		super(User.class, service, new Grid<>(), createForm(passwordEncoder), currentUser);
	}

	@Override
	public void setupGrid(Grid<User> grid) {
		grid.addColumn(User::getEmail).setWidth("270px").setHeader("Email").setFlexGrow(5);
		grid.addColumn(u -> u.getFirstName() + " " + u.getLastName()).setHeader("Name").setWidth("200px").setFlexGrow(5);
		grid.addColumn(User::getRole).setHeader("Role").setWidth("150px");
	}

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
