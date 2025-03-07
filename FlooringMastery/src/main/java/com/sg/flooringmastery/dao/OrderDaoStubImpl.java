package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoStubImpl implements OrderDao {

    private Order testOrder;

    public OrderDaoStubImpl() {
        testOrder = new Order();
        testOrder.setOrderNumber(1);
        testOrder.setCustomerName("Test Customer");
        testOrder.setState("CA");
        testOrder.setTaxRate(new BigDecimal("25.00"));
        testOrder.setProductType("Tile");
        testOrder.setArea(new BigDecimal("200.00"));
        testOrder.setCostPerSquareFoot(new BigDecimal("3.50"));
        testOrder.setLaborCostPerSquareFoot(new BigDecimal("4.00"));
        testOrder.setMaterialCost(new BigDecimal("700.00"));
        testOrder.setLaborCost(new BigDecimal("800.00"));
        testOrder.setTax(new BigDecimal("375.00"));
        testOrder.setTotal(new BigDecimal("1875.00"));
        testOrder.setDate(LocalDate.of(2025, 3, 28));
    }

    @Override
    public Order addOrder(Order order, LocalDate date) throws DataPersistenceException {
        testOrder = order;
        return testOrder;
    }

    @Override
    public List<Order> getAllOrdersByDate(LocalDate date) throws DataPersistenceException {
        List<Order> orders = new ArrayList<>();
        orders.add(testOrder);
        return orders;
    }

    @Override
    public Order getByOrderAndDate(int orderNumber, LocalDate date) throws DataPersistenceException {
        if (testOrder == null || testOrder.getOrderNumber() != orderNumber) {
            return null;
        }
        return testOrder;
    }

    @Override
    public Order updateOrder(int orderNumber, LocalDate date, Order updatedOrder) throws DataPersistenceException {
        return testOrder;
    }

    @Override
    public Order removeOrder(int orderNumber, LocalDate date) {
        if (testOrder != null && testOrder.getOrderNumber() == orderNumber) {
            Order removedOrder = testOrder; // Store the removed order to return
            testOrder = null; // Remove it from the DAO
            return removedOrder;
        }
        return null; // If the order was not found, return null
    }





    @Override
    public void exportAll() throws DataPersistenceException {
    }
}
