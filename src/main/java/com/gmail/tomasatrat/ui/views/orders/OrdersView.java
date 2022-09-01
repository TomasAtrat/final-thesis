package com.gmail.tomasatrat.ui.views.orders;

import com.gmail.tomasatrat.backend.data.entity.Branch;
import com.gmail.tomasatrat.backend.data.entity.OrderInfo;
import com.gmail.tomasatrat.backend.microservices.orders.services.OrderService;
import com.gmail.tomasatrat.ui.MainView;
import com.gmail.tomasatrat.ui.crud.GenericDataProvider;
import com.gmail.tomasatrat.ui.utils.Constants;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_ORDERS;

@Route(value = PAGE_ORDERS, layout = MainView.class)
@PageTitle(Constants.TITLE_ORDERS)
public class OrdersView extends VerticalLayout {
    private OrderService orderService;


    @Autowired
    public OrdersView(OrderService orderService) {
        this.orderService = orderService;

        Crud<OrderInfo> crud = new Crud<>(OrderInfo.class, createOrdersEditor());

        GenericDataProvider<OrderInfo> dataProvider = new GenericDataProvider(orderService);

        crud.setDataProvider(dataProvider);
        crud.addSaveListener(e -> dataProvider.persist(e.getItem()));

        crud.addThemeVariants(CrudVariant.NO_BORDER);
        // end-source-example
        this.add(crud);

    }

    private CrudEditor<OrderInfo> createOrdersEditor() {
        TextField document = new TextField("Documento de identidad");
        TextField firstName = new TextField("Descripción");
        TextField lastName = new TextField("Descripción");
        EmailField email = new EmailField("Email");
        TextField phone = new TextField("Descripción");
        FormLayout form = new FormLayout();

     /*   Binder<Branch> binder = new Binder<>(OrderInfo.class);

        return new BinderCrudEditor<>(binder, form);*/
        return null;
    }
}
