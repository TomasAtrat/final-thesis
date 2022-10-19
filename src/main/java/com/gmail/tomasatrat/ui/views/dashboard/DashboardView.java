package com.gmail.tomasatrat.ui.views.dashboard;

import com.gmail.tomasatrat.app.security.SecurityUtils;
import com.gmail.tomasatrat.backend.data.Role;
import com.gmail.tomasatrat.backend.data.entity.Branch;
import com.gmail.tomasatrat.backend.data.entity.VProductsSoldByCategory;
import com.gmail.tomasatrat.backend.data.entity.VProductsSoldByHour;
import com.gmail.tomasatrat.backend.microservices.purchase.services.PurchaseService;
import com.gmail.tomasatrat.backend.service.UserService;
import com.gmail.tomasatrat.ui.MainView;
import com.gmail.tomasatrat.ui.utils.Constants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_DASHBOARD;

@Route(value = PAGE_DASHBOARD, layout = MainView.class)
@PageTitle(Constants.TITLE_DASHBOARD)
@Secured(Role.ADMIN)
@CssImport(value="./styles/MultipleAxes.css", themeFor = "vaadin-chart", include = "vaadin-chart-default-theme")
public class DashboardView extends VerticalLayout {

    private Branch branch;

    private PurchaseService purchaseService;
    private UserService userService;

    @Autowired
    public DashboardView(PurchaseService purchaseService, UserService userService) {

        addClassName("dashboard-view");

        branch = userService.getUserBranchByUsername(SecurityUtils.getUsername());

        this.purchaseService = purchaseService;
        this.userService = userService;

        setAlignItems(Alignment.CENTER);

        Board board = new Board();

        board.addRow(createSalesPerHourChart(), createProductsSoldByCategoryChart());

        add(new H2("Dashboard gerencial"), board);
    }

    private Component createProductsSoldByCategoryChart() {
        Chart chart = new Chart();

        var productsSoldByCategory = purchaseService.getProductsSoldByCategoryAndBranch(branch.getId());

        Configuration configuration = chart.getConfiguration();

        configuration.getChart().setZoomType(Dimension.XY);

        XAxis x = new XAxis();
        x.setCrosshair(new Crosshair());
        x.setCategories(productsSoldByCategory.stream().map(e-> e.getCategory() + ": " + e.getCode()).toArray(String[]::new));
        configuration.addxAxis(x);

        YAxis y1 = new YAxis();
        y1.setShowEmpty(false);
        y1.setTitle(new AxisTitle("Dinero obtenido ($URU)"));
        Labels labels = new Labels();
        labels.setFormatter("return this.value +'$URU'");
        y1.setLabels(labels);
        y1.setOpposite(true);
        y1.setClassName("y3");
        configuration.addyAxis(y1);

        YAxis y2 = new YAxis();
        y2.setShowEmpty(false);
        y2.setTitle(new AxisTitle("Cantidad ventas"));
        labels = new Labels();
        labels.setFormatter("return this.value");
        y2.setLabels(labels);
        y2.setClassName("y2");
        configuration.addyAxis(y2);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("function() { "
                + "var unit = { 'salesQty': 'unidades', 'amountEarned': '$URU' }[this.series.name];"
                + "return ''+ this.x +': '+ this.y +' '+ unit; }");
        configuration.setTooltip(tooltip);

        Legend legend = new Legend();
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setAlign(HorizontalAlign.LEFT);
        legend.setVerticalAlign(VerticalAlign.TOP);
        legend.setFloating(true);
        configuration.setLegend(legend);

        DataSeries series = new DataSeries();
        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
        series.setPlotOptions(plotOptionsColumn);
        series.setName("salesQty");
        series.setyAxis(1);
        series.setData(productsSoldByCategory.stream().map(VProductsSoldByCategory::getCode).toArray(String[]::new),
                productsSoldByCategory.stream().map(VProductsSoldByCategory::getSoldQty).toArray(Long[]::new));
        configuration.addSeries(series);

        series = new DataSeries();
        PlotOptionsSpline plotOptionsSpline = new PlotOptionsSpline();
        series.setPlotOptions(plotOptionsSpline);
        series.setName("amountEarned");
        series.setyAxis(0);
        series.setData(productsSoldByCategory.stream().map(VProductsSoldByCategory::getAmountEarned).toArray(Double[]::new));

        configuration.addSeries(series);

        configuration.setTitle("Productos más vendidos por categoría");
        configuration.setSubTitle("Productos / cantidad ventas");

/*
        XAxis x = new XAxis();
        x.setCrosshair(new Crosshair());
        x.setCategories(productsSoldByCategory.stream().map(VProductsSoldByCategory::getCode).toArray(String[]::new));
        configuration.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        y.setTitle("Cantidad ventas");
        configuration.addyAxis(y);

        Tooltip tooltip = new Tooltip();
        tooltip.setShared(true);
        configuration.setTooltip(tooltip);
*/

        return chart;
    }

    private Component createSalesPerHourChart() {
        var hoursWithMoreSales = purchaseService.getHoursWithMoreSellsByBranch(branch.getId());

        List<String> availableHours = getBranchAvailableHours(branch);
        List<Double> sales = getSalesByHour(availableHours, hoursWithMoreSales);

        Chart chart = new Chart(ChartType.AREASPLINE);
        Configuration conf = chart.getConfiguration();

        conf.setTitle(new Title("Horas con más ventas"));

        XAxis xAxis = new XAxis();

        xAxis.setCategories(availableHours.toArray(new String[0]));

        conf.addxAxis(xAxis);

        conf.getyAxis().setTitle("Cantidad ventas");

        PlotOptionsAreaspline plotOptions = new PlotOptionsAreaspline();
        plotOptions.setPointPlacement(PointPlacement.ON);
        plotOptions.setMarker(new Marker(true));
        conf.addPlotOptions(plotOptions);

        conf.addSeries(new ListSeries("Sucursal " + branch.getDescription(), sales.toArray(new Double[0])));

        return chart;
    }

    private List<Double> getSalesByHour(List<String> availableHours, List<VProductsSoldByHour> hoursWithMoreSales) {
        List<Double> sales = new ArrayList<>();

        availableHours.forEach(i -> {
            if (hoursWithMoreSales.stream().anyMatch(j -> j.getTime().equals(i)))
                sales.add(hoursWithMoreSales.stream().filter(j -> j.getTime().equals(i)).findAny().get().getAverage());

            else sales.add(0D);
        });

        return sales;
    }

    private List<String> getBranchAvailableHours(Branch branch) {
        List<String> hours = new ArrayList<>();

        LocalTime opening = branch.getOpeningTime();
        hours.add(opening.format(DateTimeFormatter.ISO_TIME));

        do {
            hours.add(opening.plusHours(1).format(DateTimeFormatter.ISO_TIME));
            opening = opening.plusHours(1);
        } while (opening != branch.getClosingTime());

        return hours;
    }
}
