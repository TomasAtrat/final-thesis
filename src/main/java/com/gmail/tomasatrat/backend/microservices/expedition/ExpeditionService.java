package com.gmail.tomasatrat.backend.microservices.expedition;

import com.gmail.tomasatrat.backend.data.entity.OrderInfo;
import com.gmail.tomasatrat.backend.data.entity.Preparation;
import com.gmail.tomasatrat.backend.microservices.orders.services.OrderService;
import com.gmail.tomasatrat.backend.microservices.preparation.services.PreparationService;
import com.gmail.tomasatrat.backend.microservices.stock.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ExpeditionService {

    private OrderService orderService;
    private PreparationService preparationService;
    private StockService stockService;

    @Autowired
    public ExpeditionService(OrderService orderService,
                             PreparationService preparationService,
                             StockService stockService) {
        this.orderService = orderService;
        this.preparationService = preparationService;
        this.stockService = stockService;
    }

    public void shipPreparation(Preparation preparation) {
        finalizePreparation(preparation);

        if (preparation.getIsFinished())
            finalizeOrder(preparation.getOrder());

        updateStock(preparation);
    }

    private void updateStock(Preparation preparation) {
        var preparationDetails = preparationService.getDetailsByPreparation(preparation);
        preparationDetails.forEach(i -> {
            var stock = stockService.findStockByBarcodeAndBranch(i.getBarcode(),
                    preparation.getOrder().getBranch());
            stock.setQtStock(stock.getQtStock() - i.getPreparedQty());
            stock.setQtReserve(stock.getQtReserve() - i.getPreparedQty());
            stock.setUpdateDate(new Date());

            stockService.addItem(stock);
        });
    }

    private void finalizeOrder(OrderInfo order) {
        order.setDeliveryDate(new Date());
        orderService.updateOrder(order);
    }

    private void finalizePreparation(Preparation preparation) {
        preparation.setUpdateDate(new Date());
        preparation.setIsFinished(true);
        preparation.setIsShipped(true);
        preparationService.updatePreparation(preparation);
    }

}
