package com.gmail.tomasatrat.ui.views.orders;

import com.gmail.tomasatrat.backend.data.OrderInfo;
import com.gmail.tomasatrat.backend.data.Role;
import com.gmail.tomasatrat.backend.microservices.orders.services.OrderService;
import com.gmail.tomasatrat.ui.MainView;
import com.gmail.tomasatrat.ui.utils.Constants;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.crudui.crud.impl.GridCrud;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_ORDERS;

@Route(value = PAGE_ORDERS, layout = MainView.class)
@PageTitle(Constants.TITLE_ORDERS)
@Secured({Role.EMPLOYEE, Role.ADMIN})
public class OrdersView extends VerticalLayout {
    private OrderService orderService;

    @Autowired
    public OrdersView(OrderService orderService){
        this.orderService = orderService;
        GridCrud<OrderInfo> crud = new GridCrud<>(OrderInfo.class, orderService);

        crud.getCrudFormFactory().setUseBeanValidation(true);

        add(crud);

        add(new H1("Orders"));
    }

}
