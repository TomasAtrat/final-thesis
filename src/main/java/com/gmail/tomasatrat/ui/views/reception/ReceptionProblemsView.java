package com.gmail.tomasatrat.ui.views.reception;

import com.gmail.tomasatrat.backend.data.Role;
import com.gmail.tomasatrat.backend.data.entity.ReceptionProblem;
import com.gmail.tomasatrat.backend.microservices.reception.services.ReceptionService;
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
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_RECEPTION_PROBLEMS;

@Route(value = PAGE_RECEPTION_PROBLEMS, layout = MainView.class)
@PageTitle(Constants.TITLE_RECEPTION_PROBLEMS)
@Secured( Role.ADMIN)
public class ReceptionProblemsView extends VerticalLayout {
    private Grid<ReceptionProblem> grid;
    private ReceptionService receptionService;

    @Autowired
    public ReceptionProblemsView(ReceptionService receptionService){
        this.receptionService = receptionService;

        setAlignItems(Alignment.CENTER);

        setupGrid();

        this.add(new H2("Problemas sin aceptar"), grid);
    }

    private void setupGrid() {
        grid = new Grid<>();

        grid.setItems(this.receptionService.getNotAcceptedProblems());

        grid.setColumnReorderingAllowed(true);
        grid.addColumn(createActionsMenuBar()).setHeader("").setAutoWidth(true);
        grid.addColumn(ReceptionProblem::getId).setHeader("ID").setAutoWidth(true).setResizable(true);
        grid.addColumn(ReceptionProblem::getProductCode).setHeader("Producto").setAutoWidth(true).setResizable(true);
        grid.addColumn(ReceptionProblem::getDifference).setHeader("Diferencia").setAutoWidth(true).setResizable(true);
        grid.addColumn(ReceptionProblem::getDescription).setHeader("Desc. dif.").setAutoWidth(true).setResizable(true);
        grid.addColumn(problem -> problem.getDetail().getScheduledQty()).setHeader("Cant. agendada").setAutoWidth(true).setResizable(true);
        grid.addColumn(problem -> problem.getDetail().getReceivedQty()).setHeader("Cant. recibida").setAutoWidth(true).setResizable(true);
        grid.addColumn(problem -> problem.getDetail().getReceptionList().getId()).setHeader("Recepción").setAutoWidth(true).setResizable(true);
    }

    private final SerializableBiConsumer<MenuBar, ReceptionProblem> actionsMenuBar = (menuBar, problem) -> {
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

    private void acceptDifference(ReceptionProblem problem) {
        this.receptionService.acceptDifference(problem);

        this.grid.setItems(this.receptionService.getNotAcceptedProblems());

        this.receptionService.updateReceptionDetailAcceptedQty(problem.getDetail());
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

    private ComponentRenderer<MenuBar, ReceptionProblem> createActionsMenuBar() {
        return new ComponentRenderer<>(MenuBar::new, actionsMenuBar);
    }

}
