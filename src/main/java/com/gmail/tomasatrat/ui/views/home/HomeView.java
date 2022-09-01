package com.gmail.tomasatrat.ui.views.home;

import com.gmail.tomasatrat.backend.data.Role;
import com.gmail.tomasatrat.backend.data.entity.Branch;
import com.gmail.tomasatrat.backend.service.BranchService;
import com.gmail.tomasatrat.ui.MainView;
import com.gmail.tomasatrat.ui.crud.CrudEntityDataProvider;
import com.gmail.tomasatrat.ui.crud.GenericDataProvider;
import com.gmail.tomasatrat.ui.crud.PersonDataProvider;
import com.gmail.tomasatrat.ui.utils.Constants;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_HOME;

@Route(value = PAGE_HOME, layout = MainView.class)
@PageTitle(Constants.TITLE_HOME)
@Secured({Role.EMPLOYEE, Role.ADMIN})
public class HomeView extends VerticalLayout {

    @Autowired
    public HomeView(BranchService branchService){
        Crud<Branch> crud = new Crud<>(Branch.class, createBranchEditor());

        GenericDataProvider<Branch> dataProvider = new GenericDataProvider(branchService);

        crud.setDataProvider(dataProvider);
        crud.addSaveListener(e -> dataProvider.persist(e.getItem()));

        crud.addThemeVariants(CrudVariant.NO_BORDER);
        // end-source-example
        this.add(crud);
    }

    private CrudEditor<Branch> createBranchEditor() {
        TextField description = new TextField("Descripción");
        FormLayout form = new FormLayout(description);

        Binder<Branch> binder = new Binder<>(Branch.class);
        binder.bind(description, Branch::getDescription, Branch::setDescription);

        return new BinderCrudEditor<>(binder, form);
    }
}
