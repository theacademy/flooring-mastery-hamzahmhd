package com.sg.flooringmastery.view;

import com.sg.flooringmastery.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class FlooringView {

    private UserIO io;

    @Autowired
    public FlooringView(UserIO io) {
        this.io = io;
    }

    public int printMenuAndGetSelection() {
        io.print("<< Flooring Program >>");
        io.print("1. Display Orders");
        io.print("2. Add an Order");
        io.print("3. Edit an Order");
        io.print("4. Remove an Order");
        io.print("5. Export All Data");
        io.print("6. Quit");

        return io.readInt("Please select from the above choices.", 1, 6);
    }

    public LocalDate getOrderDate() throws InvalidUserInputException {
        return io.readLocalDate("Enter order date");
    }

    public int getOrderNumber() {
        return io.readInt("Enter the order number: ");
    }

    public String getCustomerName(String currentName) {
        String name = io.readString("Enter customer name (" + currentName + "): ");
        return name.isEmpty() ? currentName : name;
    }

    public String getState(String currentState) {
        String state = io.readString("Enter state abbreviation (" + currentState + "): ");
        return state.isEmpty() ? currentState : state;
    }

    public String getProductType(String currentProduct) {
        String product = io.readString("Enter product type (" + currentProduct + "): ");
        return product.isEmpty() ? currentProduct : product;
    }

    public BigDecimal getArea(BigDecimal currentArea) {
        String input = io.readString("Enter area (" + currentArea + " sq ft): ");
        return input.isEmpty() ? currentArea : new BigDecimal(input);
    }

    public void displayOrder(Order order) {
        io.print("*******************************************");
        io.print("Order Number: " + order.getOrderNumber());
        io.print("Customer Name: " + order.getCustomerName());
        io.print("State: " + order.getState());
        io.print("Tax Rate: " + order.getTaxRate() + "%");
        io.print("Product Type: " + order.getProductType());
        io.print("Area: " + order.getArea() + " sq ft");
        io.print("Cost Per Sq Ft: $" + order.getCostPerSquareFoot());
        io.print("Labor Cost Per Sq Ft: $" + order.getLaborCostPerSquareFoot());
        io.print("Material Cost: $" + order.getMaterialCost());
        io.print("Labor Cost: $" + order.getLaborCost());
        io.print("Tax: $" + order.getTax());
        io.print("Total Cost: $" + order.getTotal());
        io.print("********************************************");
    }

    public void displayOrders(List<Order> orders) {
        if (orders.isEmpty()) {
            io.print("No orders found for this date.");
        }
        else {
            for (Order order : orders) {
                displayOrder(order);
            }
        }
    }


    public void displayMessage(String message) {
        io.print(message);
    }

    public boolean confirmAdd() {
        return io.readString("Would you like to place this order? (Y/N)").equalsIgnoreCase("Y");
    }

    public boolean confirmEdit() {
        return io.readString("Would you like to save these changes? (Y/N)").equalsIgnoreCase("Y");
    }

    public boolean confirmRemove() {
        return io.readString("Are you sure you want to remove this order? (Y/N)").equalsIgnoreCase("Y");
    }


    public void displayError(String errorMessage) {
        io.print("ERROR: " + errorMessage);
    }

    public void displayExportSuccess() {
        io.print("All orders successfully exported to backup file.");
    }

    public void displayDisplayOrdersBanner() {
        io.print("*** Display Orders ***");
    }

    public void displayAddOrderBanner() {
        io.print("*** Add an Order ***");
    }

    public void displayEditOrderBanner() {
        io.print("*** Edit an Order ***");
    }

    public void displayRemoveOrderBanner() {
        io.print("*** Remove an Order ***");
    }

    public void displayExportDataBanner() {
        io.print("*** Export All Data ***");
    }
}
