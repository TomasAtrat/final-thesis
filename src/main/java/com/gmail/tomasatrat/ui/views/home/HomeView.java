package com.gmail.tomasatrat.ui.views.home;

import com.gmail.tomasatrat.backend.data.Role;
import com.gmail.tomasatrat.ui.MainView;
import com.gmail.tomasatrat.ui.utils.Constants;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.security.access.annotation.Secured;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_HOME;

@Route(value = PAGE_HOME, layout = MainView.class)
@RouteAlias(value = "",  layout = MainView.class)
@PageTitle(Constants.TITLE_HOME)
@Secured({Role.EMPLOYEE, Role.ADMIN, Role.TECHNICIAN})
public class HomeView extends VerticalLayout {

    public HomeView(){

    }
}
