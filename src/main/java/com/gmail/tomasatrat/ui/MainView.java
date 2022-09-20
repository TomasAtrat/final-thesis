package com.gmail.tomasatrat.ui;

import com.gmail.tomasatrat.app.security.SecurityUtils;
import com.gmail.tomasatrat.ui.components.navigation.drawer.BrandExpression;
import com.gmail.tomasatrat.ui.components.navigation.drawer.NaviDrawer;
import com.gmail.tomasatrat.ui.components.navigation.drawer.NaviItem;
import com.gmail.tomasatrat.ui.components.navigation.drawer.NaviMenu;
import com.gmail.tomasatrat.ui.views.HasConfirmation;
import com.gmail.tomasatrat.ui.views.inventory.InventoryProblemsView;
import com.gmail.tomasatrat.ui.views.users.UsersView;
import com.gmail.tomasatrat.ui.views.home.HomeView;
import com.gmail.tomasatrat.ui.views.orders.OrdersView;
import com.gmail.tomasatrat.ui.views.tasks.TasksView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;

import java.util.Optional;

import static com.gmail.tomasatrat.ui.utils.Constants.TITLE_DASHBOARD;
import static com.gmail.tomasatrat.ui.utils.Constants.TITLE_HOME;

@CssImport(value = "./styles/components/charts.css", themeFor = "vaadin-chart", include = "vaadin-chart-default-theme")
@CssImport(value = "./styles/components/floating-action-button.css", themeFor = "vaadin-button")
@CssImport(value = "./styles/components/grid.css", themeFor = "vaadin-grid")
@CssImport("./styles/lumo/border-radius.css")
@CssImport("./styles/lumo/icon-size.css")
@CssImport("./styles/lumo/margin.css")
@CssImport("./styles/lumo/padding.css")
@CssImport("./styles/lumo/shadow.css")
@CssImport("./styles/lumo/spacing.css")
@CssImport("./styles/lumo/typography.css")
@CssImport("./styles/misc/box-shadow-borders.css")
@CssImport(value = "./styles/styles.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge")
@PWA(name = "SmartStore", shortName = "SmartStore",
        startPath = "login",
        backgroundColor = "#227aef", themeColor = "#227aef",
        offlinePath = "offline-page.html",
        offlineResources = {"images/offline-login-banner.jpg"},
        enableInstallPrompt = true)
public class MainView extends AppLayout {

    private final ConfirmDialog confirmDialog = new ConfirmDialog();

    private static NaviDrawer naviDrawer;
    private static NaviMenu menu;

    public MainView() {
        naviDrawer = new NaviDrawer();
        confirmDialog.setCancelable(true);
        confirmDialog.setConfirmButtonTheme("raised tertiary error");
        confirmDialog.setCancelButtonTheme("raised tertiary");

        Avatar avatar = new Avatar();
        avatar.setClassName("tab-bar" + "__avatar");
        avatar.setName(SecurityUtils.getUsername());

        ContextMenu contextMenu = new ContextMenu(avatar);
        contextMenu.setOpenOnClick(true);
        contextMenu.addItem("Cerrar sesión",
                e -> {
                    UI.getCurrent().getPage().executeJs("window.location.href='/logout'");
                });

        DrawerToggle toggle = new DrawerToggle();

        menu = createMenu();

        naviDrawer.getMenu().getNaviItems().forEach(NaviItem::closeSubItems);

        this.addToDrawer(new BrandExpression("SmartStore"), menu);
        this.addToNavbar(toggle, avatar);
        this.getElement().appendChild(confirmDialog.getElement());

        getElement().addEventListener("search-focus", e -> {
            getElement().getClassList().add("hide-navbar");
        });

        getElement().addEventListener("search-blur", e -> {
            getElement().getClassList().remove("hide-navbar");
        });
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        confirmDialog.setOpened(false);
        if (getContent() instanceof HasConfirmation) {
            ((HasConfirmation) getContent()).setConfirmDialog(confirmDialog);
        }
        RouteConfiguration configuration = RouteConfiguration.forSessionScope();
        if (configuration.isRouteRegistered(this.getContent().getClass())) {
            String target = configuration.getUrl(this.getContent().getClass());
            Optional<Component> tabToSelect = menu.getChildren().filter(tab -> {
                Component child = tab.getChildren().findFirst().get();
                return child instanceof RouterLink && ((RouterLink) child).getHref().equals(target);
            }).findFirst();
        }
    }

    private static NaviMenu createMenu() {
        menu = naviDrawer.getMenu();

        menu.addNaviItem(VaadinIcon.HOME, TITLE_HOME, HomeView.class);

        NaviItem register = menu.addNaviItem(VaadinIcon.PLUS, "Registro",
                null);

        if (SecurityUtils.isAccessGranted(UsersView.class))
            menu.addNaviItem(register, "Usuarios", UsersView.class);


        //region Inventory

        NaviItem inventory = menu.addNaviItem(VaadinIcon.CLIPBOARD_TEXT, "Inventario",
                null);
        if (SecurityUtils.isAccessGranted(UsersView.class))
            menu.addNaviItem(inventory, "Problemas sin aceptar", InventoryProblemsView.class);

        //endregion

        NaviItem orders = menu.addNaviItem(VaadinIcon.PACKAGE, "Pedidos",
                null);

        menu.addNaviItem(orders, "Panel de pedidos", OrdersView.class);

        NaviItem stock = menu.addNaviItem(VaadinIcon.STOCK, "Stock",
                null);


        menu.addNaviItem(VaadinIcon.EDIT, "Tareas", TasksView.class);

        if (SecurityUtils.isAccessGranted(UsersView.class))
            menu.addNaviItem(VaadinIcon.AUTOMATION, "RFID", ReadersView.class);

        menu.addNaviItem(VaadinIcon.CHART, TITLE_DASHBOARD, HomeView.class);

        menu.addNaviItem(VaadinIcon.COG, "Configuración", HomeView.class);

        return menu;
    }
}