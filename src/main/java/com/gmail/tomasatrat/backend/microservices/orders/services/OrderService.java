package com.gmail.tomasatrat.backend.microservices.orders.services;

import com.gmail.tomasatrat.backend.common.ICrudService;
import com.gmail.tomasatrat.backend.common.IDataEntity;
import com.gmail.tomasatrat.backend.common.exceptions.SmartStoreException;
import com.gmail.tomasatrat.backend.data.entity.OrderDetail;
import com.gmail.tomasatrat.backend.data.entity.OrderInfo;
import com.gmail.tomasatrat.backend.microservices.orders.components.OrderClient;
import com.gmail.tomasatrat.backend.microservices.stock.services.StockService;
import com.gmail.tomasatrat.backend.repositories.BarcodeRepository;
import com.gmail.tomasatrat.backend.repositories.CustomerRepository;
import com.gmail.tomasatrat.backend.repositories.OrderDetailRepository;
import com.gmail.tomasatrat.backend.repositories.OrderInfoRepository;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService implements ICrudService {

    private final OrderClient orderClient;
    private OrderInfoRepository orderInfoRepository;
    private OrderDetailRepository orderDetailRepository;
    private CustomerRepository customerRepository;
    private BarcodeRepository barcodeRepository;
    private StockService stockService;

    @Autowired
    public OrderService(OrderInfoRepository orderInfoRepository,
                        OrderDetailRepository orderDetailRepository,
                        CustomerRepository customerRepository,
                        BarcodeRepository barcodeRepository,
                        StockService stockService) {
        this.orderInfoRepository = orderInfoRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.customerRepository = customerRepository;
        this.barcodeRepository = barcodeRepository;
        this.stockService = stockService;
        orderClient = new OrderClient();
    }

    @Override
    public List<OrderInfo> findAll() {
        return this.orderClient.getOrders();
    }

    @Override
    public IDataEntity addItem(IDataEntity item) {
        return saveOrderAndAttachedEntities((OrderInfo) item);
    }

    private IDataEntity saveOrderAndAttachedEntities(OrderInfo orderInfo) {
        customerRepository.save(orderInfo.getCustomer());
        return this.orderInfoRepository.save(orderInfo);
    }

    @Override
    public Optional<OrderInfo> findByID(Long id) {
        return this.orderInfoRepository.findById(id);
    }

    @Override
    public void delete(IDataEntity item) {
        this.orderInfoRepository.delete((OrderInfo) item);
    }

    public List<OrderDetail> getOrderDetailsByOrder(OrderInfo order) {
        return orderDetailRepository.findByOrderInfo(order);
    }

    public void addDetail(OrderDetail detail) throws SmartStoreException {
        if (!orderInfoRepository.existsById(detail.getOrderInfo().getId()))
            throw new SmartStoreException("No existe el pedido para el detalle de pedido");

        if (!barcodeRepository.existsById(detail.getBarcode().getId()))
            throw new SmartStoreException("No existe el producto específico ingresado");

        var stock = stockService.findStockByBarcodeAndBranch(detail.getBarcode(), detail.getOrderInfo().getBranch());

        if (stock == null || (stock.getQtStock() - stock.getQtReserve()) > detail.getOrderedQuantity())
            throw new SmartStoreException("La cantidad en stock es insuficiente para cubrir la cantidad especificada");

        orderDetailRepository.save(detail);

        updateStockReservedQty(detail);
    }

    private void updateStockReservedQty(OrderDetail detail) {
        var stock = stockService.findStockByBarcodeAndBranch(detail.getBarcode(), detail.getOrderInfo().getBranch());
        stock.setQtReserve(stock.getQtReserve() + detail.getOrderedQuantity());
        stockService.update(stock);
    }

    public void updateOrder(OrderInfo order) {
        this.orderInfoRepository.save(order);
    }
}
