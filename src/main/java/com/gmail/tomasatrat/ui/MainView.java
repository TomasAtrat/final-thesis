package com.gmail.tomasatrat.ui;

import com.gmail.tomasatrat.app.security.SecurityUtils;
import com.gmail.tomasatrat.backend.data.entity.Config;
import com.gmail.tomasatrat.backend.microservices.main.services.ConfigServices;
import com.gmail.tomasatrat.ui.components.navigation.drawer.BrandExpression;
import com.gmail.tomasatrat.ui.components.navigation.drawer.NaviDrawer;
import com.gmail.tomasatrat.ui.components.navigation.drawer.NaviItem;
import com.gmail.tomasatrat.ui.components.navigation.drawer.NaviMenu;
import com.gmail.tomasatrat.ui.views.HasConfirmation;
import com.gmail.tomasatrat.ui.views.carousel.CarouselView;
import com.gmail.tomasatrat.ui.views.dashboard.DashboardView;
import com.gmail.tomasatrat.ui.views.expedition.OrderExpeditionView;
import com.gmail.tomasatrat.ui.views.home.HomeView;
import com.gmail.tomasatrat.ui.views.inventory.InventoryProblemsView;
import com.gmail.tomasatrat.ui.views.inventory.InventoryView;
import com.gmail.tomasatrat.ui.views.orders.OrdersView;
import com.gmail.tomasatrat.ui.views.readers.ReadersView;
import com.gmail.tomasatrat.ui.views.reception.ReceptionProblemsView;
import com.gmail.tomasatrat.ui.views.stock.StockProductView;
import com.gmail.tomasatrat.ui.views.stock.StockView;
import com.gmail.tomasatrat.ui.views.tasks.TasksView;
import com.gmail.tomasatrat.ui.views.users.UsersView;
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

import java.util.List;
import java.util.Optional;

import static com.gmail.tomasatrat.ui.utils.Constants.TITLE_CAROUSEL;
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

    private final ConfigServices configServices;
    private List<Config> configList = null;

    private static NaviDrawer naviDrawer;
    private static NaviMenu menu;

    public MainView(ConfigServices configServices) {
        this.configServices = configServices;
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

    private NaviMenu createMenu() {
        this.configList = this.configServices.findAll();

        menu = naviDrawer.getMenu();

        menu.addNaviItem(VaadinIcon.HOME, TITLE_HOME, HomeView.class);

        //region Register

       /* NaviItem register = menu.addNaviItem(VaadinIcon.PLUS, "Registro",
                null);*/

        if (SecurityUtils.isAccessGranted(UsersView.class))
            tryAddNaviItem(VaadinIcon.USER, "Usuarios", UsersView.class);

        //endregion

        //region Reception
        if (SecurityUtils.isAccessGranted(ReceptionProblemsView.class)) {

            NaviItem reception = tryAddNaviItem(VaadinIcon.CLIPBOARD_CHECK, "Recepción",
                    null);

            if (reception != null)
                menu.addNaviItem(reception, "Problemas sin aceptar", ReceptionProblemsView.class);
        }
        //endregion

        //region Stock
        if (SecurityUtils.isAccessGranted(StockView.class)) {
            NaviItem stock = tryAddNaviItem(VaadinIcon.STOCK, "Stock", null);

            if (stock != null) {

                menu.addNaviItem(stock, "Panel stock", StockView.class);

                if (SecurityUtils.isAccessGranted(StockProductView.class))
                    menu.addNaviItem(stock, "Monitoreo stock", StockProductView.class);
            }
        }
        //endregion

        //region Inventory


        if (SecurityUtils.isAccessGranted(InventoryView.class)) {
            NaviItem inventory = tryAddNaviItem(VaadinIcon.CLIPBOARD_TEXT, "Inventario",
                    null);

            if (inventory != null) {
                menu.addNaviItem(inventory, "Panel de Inventario", InventoryView.class);

                if (SecurityUtils.isAccessGranted(InventoryProblemsView.class))
                    menu.addNaviItem(inventory, "Problemas sin aceptar", InventoryProblemsView.class);
            }
        }

        //endregion

        //region Orders
        if (SecurityUtils.isAccessGranted(OrdersView.class)) {
            NaviItem orders = tryAddNaviItem(VaadinIcon.PACKAGE, "Pedidos",
                    null);

            if (orders != null) {
                menu.addNaviItem(orders, "Panel de pedidos", OrdersView.class);
            }
        }
        //endregion
        if (SecurityUtils.isAccessGranted(TasksView.class))
            tryAddNaviItem(VaadinIcon.EDIT, "Tareas", TasksView.class);

        if (SecurityUtils.isAccessGranted(CarouselView.class))
            tryAddNaviItem(VaadinIcon.PRESENTATION, TITLE_CAROUSEL, CarouselView.class);

        //region Expedition

        if (SecurityUtils.isAccessGranted(UsersView.class)) {

            NaviItem expedition = tryAddNaviItem(VaadinIcon.TRUCK, "Expedición",
                    null);

            if (expedition != null)
                menu.addNaviItem(expedition, "Finalizar pedidos", OrderExpeditionView.class);
        }

        //endregion
        if (SecurityUtils.isAccessGranted(DashboardView.class))
            tryAddNaviItem(VaadinIcon.CHART, "Dashboard", DashboardView.class);

        if (SecurityUtils.isAccessGranted(ReadersView.class)) {
            NaviItem configuration = tryAddNaviItem(VaadinIcon.COG, "Configuración", null);

            if (configuration != null)
                menu.addNaviItem(configuration, "RFID", ReadersView.class);
        }
        return menu;
    }

    private NaviItem tryAddNaviItem(VaadinIcon icon, String text, Class<? extends Component> viewClass) {
        NaviItem result = null;
        if (configList != null) {
            for (Config config : configList) {
                if (config.getModuleToShow().equals(text)) {
                    result = menu.addNaviItem(icon, text, viewClass);
                }
            }
        }

        return result;
    }
}