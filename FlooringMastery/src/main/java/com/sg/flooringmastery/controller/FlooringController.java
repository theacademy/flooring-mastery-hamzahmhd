package com.sg.flooringmastery.controller;

import com.sg.flooringmastery.dao.DataPersistenceException;
import com.sg.flooringmastery.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.sg.flooringmastery.service.FlooringService;
import com.sg.flooringmastery.view.FlooringView;
import com.sg.flooringmastery.view.InvalidUserInputException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class FlooringController {

    private FlooringView view;
    private FlooringService service;

    @Autowired
    public FlooringController(FlooringService service, FlooringView view){
        this.service = service;
        this.view = view;
    }

    public void run() {
        boolean keepGoing = true;

        while (keepGoing) {
            int menuSelection = view.printMenuAndGetSelection();

            try {
                switch (menuSelection) {
                    case 1:
                        displayOrders();
                        break;
                    case 2:
                        addOrder();
                        break;
                    case 3:
                        editOrder();
                        break;
                    case 4:
                        removeOrder();
                        break;
                    case 5:
                        exportData();
                        break;
                    case 6:
                        keepGoing = false;
                        break;
                    default:
                        view.displayMessage("Unknown command. Please try again.");
                }
            } catch (DataPersistenceException e) {
                view.displayError("Error accessing data: " + e.getMessage());
            }
        }

        view.displayMessage("Exiting program...");
    }

    private void displayOrders() throws DataPersistenceException {
        view.displayDisplayOrdersBanner();
        boolean hasErrors;

        do {
            try {
                LocalDate date = view.getOrderDate();
                List<Order> orders = service.getOrdersByDate(date);
                view.displayOrders(orders);
                hasErrors = false;
            } catch (InvalidUserInputException e) {
                view.displayError("Invalid input: " + e.getMessage());
                hasErrors = true; // Loop again until valid input
            }
        } while (hasErrors);
    }

    private void addOrder() throws DataPersistenceException {
        view.displayAddOrderBanner();
        boolean hasErrors;

        do {
            try {
                LocalDate date = view.getOrderDate();
                String customerName = view.getCustomerName("");
                String state = view.getState("");
                String productType = view.getProductType("");
                BigDecimal area = view.getArea(BigDecimal.ZERO);

                // Create a new order object
                Order newOrder = service.createOrder(date, customerName, state, productType, area);

                // Show order summary
                view.displayOrder(newOrder);

                // Confirm before adding order
                boolean confirm = view.confirmAdd();
                if (confirm) {
                    service.addOrder(newOrder);
                    view.displayMessage("Order successfully added.");
                } else {
                    view.displayMessage("Order addition cancelled.");
                }
                hasErrors = false;
            } catch (InvalidUserInputException e) {
                view.displayError("Invalid input: " + e.getMessage());
                hasErrors = true;  // Loop again for valid input
            }
        } while (hasErrors);
    }

    private void editOrder() throws DataPersistenceException {
        view.displayEditOrderBanner();
        boolean hasErrors;

        do {
            try {
                LocalDate date = view.getOrderDate();
                int orderNumber = view.getOrderNumber();
                Order existingOrder = service.getOrderByNumber(date, orderNumber);

                if (existingOrder == null) {
                    view.displayMessage("Order not found.");
                    return;
                }

                // Collect updated fields (keeps existing data if input is empty)
                String customerName = view.getCustomerName(existingOrder.getCustomerName());
                String state = view.getState(existingOrder.getState());
                String productType = view.getProductType(existingOrder.getProductType());
                BigDecimal area = view.getArea(existingOrder.getArea());

                // Create a new updated order object
                Order updatedOrder = service.updateOrder(existingOrder, customerName, state, productType, area);

                // Show updated order summary
                view.displayOrder(updatedOrder);

                // Confirm before saving edits
                boolean confirm = view.confirmEdit();
                if (confirm) {
                    service.saveUpdatedOrder(updatedOrder);
                    view.displayMessage("Order successfully updated.");
                } else {
                    view.displayMessage("Order update cancelled.");
                }
                hasErrors = false;
            } catch (InvalidUserInputException e) {
                view.displayError("Invalid input: " + e.getMessage());
                hasErrors = true;  // Loop again for valid input
            }
        } while (hasErrors);
    }

    private void removeOrder() throws DataPersistenceException {
        view.displayRemoveOrderBanner();
        boolean hasErrors;

        do {
            try {
                LocalDate date = view.getOrderDate();
                int orderNumber = view.getOrderNumber();
                Order orderToRemove = service.getOrderByNumber(date, orderNumber);

                if (orderToRemove == null) {
                    view.displayMessage("Order not found.");
                    return;
                }

                // Show order before confirmation
                view.displayOrder(orderToRemove);

                // Confirm before removing order
                boolean confirm = view.confirmRemove();
                if (confirm) {
                    service.removeOrder(date, orderNumber);
                    view.displayMessage("Order successfully removed.");
                } else {
                    view.displayMessage("Order removal cancelled.");
                }
                hasErrors = false;
            } catch (InvalidUserInputException e) {
                view.displayError("Invalid input: " + e.getMessage());
                hasErrors = true;  // Loop again for valid input
            }
        } while (hasErrors);
    }

    private void exportData() throws DataPersistenceException {
        view.displayExportDataBanner();
        service.exportAllData();
        view.displayExportSuccess();
    }


}
