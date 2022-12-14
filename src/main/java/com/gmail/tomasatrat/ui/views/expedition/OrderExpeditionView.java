package com.gmail.tomasatrat.ui.views.expedition;

import com.gmail.tomasatrat.backend.data.Role;
import com.gmail.tomasatrat.backend.data.entity.Preparation;
import com.gmail.tomasatrat.backend.microservices.expedition.ExpeditionService;
import com.gmail.tomasatrat.backend.microservices.preparation.services.PreparationService;
import com.gmail.tomasatrat.ui.MainView;
import com.gmail.tomasatrat.ui.utils.Constants;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_ORDER_EXPEDITION;

@Route(value = PAGE_ORDER_EXPEDITION, layout = MainView.class)
@PageTitle(Constants.TITLE_ORDER_EXPEDITION)
@Secured(Role.ADMIN)
public class OrderExpeditionView extends VerticalLayout {
    private Grid<Preparation> grid;
    private ConfirmDialog confirmDialog;

    private PreparationService preparationService;
    private ExpeditionService expeditionService;

    @Autowired
    public OrderExpeditionView(PreparationService preparationService,
                               ExpeditionService expeditionService) {
        this.preparationService = preparationService;
        this.expeditionService = expeditionService;

        setAlignItems(Alignment.CENTER);

        setupGrid();

        this.add(new H2("Finalizar pedidos"), grid);
    }

    private void setupGrid() {
        grid = new Grid();

        grid.setItems(this.preparationService.getAllPreparationsNotFinished());

        grid.setColumnReorderingAllowed(true);
        grid.addColumn(createActionsMenuBar()).setHeader("").setAutoWidth(true);
        grid.addColumn(prep -> prep.getOrder().getId()).setHeader("Pedido").setAutoWidth(true).setResizable(true);
        grid.addColumn(prep -> prep.getOrder().getAddrowDate()).setHeader("Fecha pedido").setAutoWidth(true).setResizable(true);
        grid.addColumn(prep -> prep.getOrder().getCustomer().getId()).setHeader("CI cliente").setAutoWidth(true).setResizable(true);
        grid.addColumn(prep -> prep.getOrder().getAcceptsPartialExpedition() ? "SI" : "NO").setHeader("Acepta expedici??n parcial").setAutoWidth(true).setResizable(true);
        grid.addColumn(Preparation::getId).setHeader("Preparaci??n").setAutoWidth(true).setResizable(true);
        grid.addColumn(Preparation::getStartingDate).setHeader("Inicio preparaci??n").setAutoWidth(true).setResizable(true);
        grid.addColumn(Preparation::getEndingDate).setHeader("Fin preparaci??n").setAutoWidth(true).setResizable(true);
    }

    private ComponentRenderer<MenuBar, Preparation> createActionsMenuBar() {
        return new ComponentRenderer<>(MenuBar::new, actionsMenuBar);
    }

    private final SerializableBiConsumer<MenuBar, Preparation> actionsMenuBar = (menuBar, preparation) -> {
        menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);

        MenuItem actions = createIconItem(menuBar, VaadinIcon.BULLETS, null, null);

        MenuItem addDetail = createIconItem(actions.getSubMenu(), VaadinIcon.CHECK, "Aceptar diferencias", null, true);

        addDetail.addClickListener(e -> {
            createConfirmDialog(preparation);
            confirmDialog.open();
        });
    };

    private void createConfirmDialog(Preparation preparation) {
        confirmDialog = new ConfirmDialog();

        confirmDialog.setHeader("Confirmar c??dula del cliente");

        TextField clientDocumentTxt = new TextField("C??dula identidad");

        confirmDialog.add(clientDocumentTxt);

        confirmDialog.setConfirmText("Confirmar");

        confirmDialog.addConfirmListener(e -> {
            if (!clientDocumentTxt.getValue().equals(preparation.getOrder().getCustomer().getId()))
                Notification.show("ERROR: La c??dula proporcionada no coincide con la c??dula de quien realiz?? el pedido", 5000, Notification.Position.BOTTOM_CENTER);
            else {
                expeditionService.shipPreparation(preparation);
                grid.setItems(this.preparationService.getAllPreparationsNotFinished());
                confirmDialog.close();
                Notification.show("La preparaci??n ha sido correctamente expedida", 5000, Notification.Position.BOTTOM_CENTER);
            }
        });

        confirmDialog.setCancelable(true);
        confirmDialog.addCancelListener(event -> {
            confirmDialog.close();
        });
    }

    private static MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName, String label, String ariaLabel) {
        return createIconItem(menu, iconName, label, ariaLabel, false);
    }

    private static MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName, String label, String ariaLabel, boolean isChild) {
        Icon icon = new Icon(iconName);
        if (isChild) {
            icon.getStyle().set("width", "var(--lumo-icon-size-s)");
            icon.getStyle().set("height", "var(--lumo-icon-size-s)");
            icon.getStyle().set("marginRight", "var(--lumo-space-s)");
        }
        MenuItem item = menu.addItem(icon, e -> {
        });

        if (ariaLabel != null) {
            item.getElement().setAttribute("aria-label", ariaLabel);
        }

        if (label != null) {
            item.add(new Text(label));
        }

        return item;
    }

}
