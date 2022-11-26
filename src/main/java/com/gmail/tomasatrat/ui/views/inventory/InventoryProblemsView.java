package com.gmail.tomasatrat.ui.views.inventory;

import com.gmail.tomasatrat.backend.data.Role;
import com.gmail.tomasatrat.backend.data.entity.InventoryProblem;
import com.gmail.tomasatrat.backend.microservices.inventory.services.InventoryProblemService;
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
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_INVENTORY_PROBLEMS;

@Route(value = PAGE_INVENTORY_PROBLEMS, layout = MainView.class)
@PageTitle(Constants.TITLE_INVENTORY_PROBLEMS)
@Secured(Role.ADMIN)
public class InventoryProblemsView extends VerticalLayout {

    private Grid<InventoryProblem> grid;
    private InventoryProblemService inventoryProblemService;

    @Autowired
    public InventoryProblemsView(InventoryProblemService inventoryProblemService){
        this.inventoryProblemService = inventoryProblemService;

        setAlignItems(Alignment.CENTER);

        setupGrid();

        this.add(new H2("Problemas sin aceptar"), grid);
    }

    private void setupGrid() {
        grid = new Grid<>();

        grid.setItems(this.inventoryProblemService.getNotAcceptedProblems());

        grid.setColumnReorderingAllowed(true);
        grid.addColumn(createActionsMenuBar()).setHeader("").setAutoWidth(true);
        grid.addColumn(InventoryProblem::getId).setHeader("ID").setAutoWidth(true).setResizable(true);
        grid.addColumn(InventoryProblem::getProductCode).setHeader("Producto").setAutoWidth(true).setResizable(true);
        grid.addColumn(InventoryProblem::getDifference).setHeader("Diferencia").setAutoWidth(true).setResizable(true);
        grid.addColumn(InventoryProblem::getDescription).setHeader("Desc. dif.").setAutoWidth(true).setResizable(true);
        grid.addColumn(problem -> problem.getDetail().getSupposedQty()).setHeader("Cant. teórica").setAutoWidth(true).setResizable(true);
        grid.addColumn(problem -> problem.getDetail().getReadQty()).setHeader("Cant. leída").setAutoWidth(true).setResizable(true);
        grid.addColumn(problem -> problem.getDetail().getInventory().getId()).setHeader("Inventario").setAutoWidth(true).setResizable(true);
    }

    private final SerializableBiConsumer<MenuBar, InventoryProblem> actionsMenuBar = (menuBar, problem) -> {
        menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);

        MenuItem actions = createIconItem(menuBar, VaadinIcon.BULLETS, null, null);

        MenuItem addDetail = createIconItem(actions.getSubMenu(), VaadinIcon.CHECK, "Aceptar diferencias", null, true);

        addDetail.addClickListener(e -> {
            ConfirmDialog dialog = new ConfirmDialog();
            var header = "Aceptar diferencia";
            dialog.setHeader(header);
            dialog.setText("¿Estás seguro que quieres guardar este cambio?");

            dialog.setCancelable(true);
            dialog.addCancelListener(event -> {
                dialog.close();
            });

            dialog.setCancelText("Cancelar");

            dialog.setConfirmText("Aceptar diferencia");
            dialog.addConfirmListener(event -> {
                acceptDifference(problem);
                dialog.close();
                Notification.show("Se han guardado los cambios", 5000, Notification.Position.BOTTOM_CENTER);
            });

            dialog.open();

        });
    };

    private void acceptDifference(InventoryProblem problem) {
        this.inventoryProblemService.acceptDifference(problem);

        this.grid.setItems(this.inventoryProblemService.getNotAcceptedProblems());

        this.inventoryProblemService.updateInventoryDetailAcceptedQty(problem.getDetail());
        this.inventoryProblemService.updateStockAfterInventoryProblemAccepted(problem.getDetail());
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

    private ComponentRenderer<MenuBar, InventoryProblem> createActionsMenuBar() {
        return new ComponentRenderer<>(MenuBar::new, actionsMenuBar);
    }

}
