package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Order;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class OrderDaoFileImpl implements OrderDao{

    // constants for data persistence
    private static final String ORDER_FOLDER = detectOrderFolder();
    private static final String BACKUP_FILE = Paths.get("FlooringMastery", "Backup", "DataExport.txt").toAbsolutePath().toString();
    public static final String DELIMITER = ",";

    // Storage for DAO data in-memory
    private Map<Integer, Order> orderMap = new HashMap<>();

    // If you're wondering why this method is needed then you're in the same boat as me
    // because for some reason my test folder doesn't want to use the same root directory as my
    // main folder which makes file pathing a real hassle.
    // This method makes it less of a hassle
    private static String detectOrderFolder() {
        String cwd = System.getProperty("user.dir"); // Current root directory

        if (cwd.endsWith("FlooringMastery")) {
            return Paths.get(cwd, "Orders").toAbsolutePath().toString() + "/";
        } else {
            return Paths.get(cwd, "FlooringMastery", "Orders").toAbsolutePath().toString() + "/";
        }
    }


    private String getFilePath(LocalDate date){
        return ORDER_FOLDER + "Orders_" + date.format(DateTimeFormatter.ofPattern("MMddyyyy")) + ".txt";
    }

    private boolean fileExists(LocalDate date){
        return Files.exists(Paths.get(getFilePath(date)));
    }

    private Order unmarshallOrder(String orderAsText, LocalDate date){

        String[] tokens = orderAsText.split(DELIMITER);

        Order order = new Order();
        int orderNumber = Integer.parseInt(tokens[0]);
        order.setOrderNumber(orderNumber);
        order.setCustomerName(tokens[1]);
        order.setState(tokens[2]);
        order.setTaxRate(new BigDecimal(tokens[3]));
        order.setProductType(tokens[4]);
        order.setArea(new BigDecimal(tokens[5]));
        order.setCostPerSquareFoot(new BigDecimal(tokens[6]));
        order.setLaborCostPerSquareFoot(new BigDecimal(tokens[7]));
        order.setMaterialCost(new BigDecimal(tokens[8]));
        order.setLaborCost(new BigDecimal(tokens[9]));
        order.setTax(new BigDecimal(tokens[10]));
        order.setTotal(new BigDecimal(tokens[11]));
        order.setDate(date);

        return order;
    }

    private void load(LocalDate date) throws DataPersistenceException {

        String filePath = getFilePath(date);
        orderMap.clear(); // To ensure don't reput existing orders into memory hashmap

        if (!fileExists(date)){
            System.out.println("File does not exist: " + filePath);
            return;
        }

        try (Scanner sc = new Scanner(new BufferedReader(new FileReader(filePath)))) {

            if (sc.hasNextLine()){
                sc.nextLine(); // skip header row
            }
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                Order order = unmarshallOrder(line, date);
                orderMap.put(order.getOrderNumber(), order);
            }
        } catch (FileNotFoundException e) {
            throw new DataPersistenceException("Could not load orders from " + filePath, e);
        }
    }

    private String marshallOrder(Order order){
        return order.getOrderNumber() + DELIMITER +
                order.getCustomerName() + DELIMITER +
                order.getState() + DELIMITER +
                order.getTaxRate() + DELIMITER +
                order.getProductType() + DELIMITER +
                order.getArea() + DELIMITER +
                order.getCostPerSquareFoot() + DELIMITER +
                order.getLaborCostPerSquareFoot() + DELIMITER +
                order.getMaterialCost() + DELIMITER +
                order.getLaborCost() + DELIMITER +
                order.getTax() + DELIMITER +
                order.getTotal();
    }

    private void save(LocalDate date) throws DataPersistenceException{

        String filePath = getFilePath(date);


        try (PrintWriter out = new PrintWriter(new FileWriter(filePath))){
            // print header row
            out.println("OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot, LaborCostPerSquareFoot, MaterialCost, LaborCost, Tax, Total");

            for (Order order : orderMap.values()){
                out.println(marshallOrder(order));
            }
        }
        catch (IOException e){
            throw new DataPersistenceException("Could not save orders to " + filePath, e);
        }
    }

    // helper function to retrieve and generate a unique new order number
    private int getNextOrderNumber() throws DataPersistenceException {
        int maxOrderNumber = 0;
        File ordersFolder = new File(ORDER_FOLDER);

        // Ensure directory exists
        if (!ordersFolder.exists()) {
            return 1;
        }

        // Get all order files in a stream safely
        File[] orderFilesArray = ordersFolder.listFiles();
        if (orderFilesArray == null) {
            return 1;
        }

        // Here's my usage of streams and lambdas
        List<File> orderFiles = Arrays.stream(orderFilesArray)
                .filter(file -> file.getName().startsWith("Orders_") && file.getName().endsWith(".txt"))
                .toList(); // I'm allowed to use this instead of collect(Collectors.toList()) since I'm using Java 17 :)

        for (File file : orderFiles) {
            try (Scanner sc = new Scanner(new BufferedReader(new FileReader(file)))) {
                if (sc.hasNextLine()) {
                    sc.nextLine(); // Skip header row
                }

                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] tokens = line.split(DELIMITER);
                    int orderNumber = Integer.parseInt(tokens[0]);

                    maxOrderNumber = Math.max(maxOrderNumber, orderNumber);
                }
            } catch (IOException | NumberFormatException e) {
                throw new DataPersistenceException("Error reading order files to determine next order number.", e);
            }
        }

        return maxOrderNumber + 1;
    }



    @Override
    public Order addOrder(Order order, LocalDate date) throws DataPersistenceException {
        load(date);

        int nextOrderNumber = getNextOrderNumber();
        order.setOrderNumber(nextOrderNumber);
        order.setDate(date);
        orderMap.put(order.getOrderNumber(), order);

        save(date);
        return order;
    }

    @Override
    public List<Order> getAllOrdersByDate(LocalDate date) throws DataPersistenceException {
        load(date);

        return new ArrayList<>(orderMap.values());
    }

    @Override
    public Order removeOrder(int orderNumber, LocalDate date) throws DataPersistenceException {
        load(date);

        Order removedOrder = orderMap.remove(orderNumber);

        save(date);
        return removedOrder;
    }

    @Override
    public Order updateOrder(int orderNumber, LocalDate date, Order updatedOrder) throws DataPersistenceException {
        load(date);

        if (!orderMap.containsKey(orderNumber)){
            return null;
        }

        Order existingOrder = orderMap.get(orderNumber);

        existingOrder.setCustomerName(updatedOrder.getCustomerName());
        existingOrder.setState(updatedOrder.getState());
        existingOrder.setProductType(updatedOrder.getProductType());
        existingOrder.setArea(updatedOrder.getArea());

        save(date);

        return(existingOrder);

    }

    @Override
    public Order getByOrderAndDate(int orderNumber, LocalDate date) throws DataPersistenceException {
        load(date);

        return orderMap.get(orderNumber);
    }

    @Override
    public void exportAll() throws DataPersistenceException {
        File ordersFolder = new File(ORDER_FOLDER);

        // Ensure directory exists
        if (!ordersFolder.exists()) {
            throw new DataPersistenceException("Order directory does not exist.");
        }

        // Get all order files safely
        File[] orderFilesArray = ordersFolder.listFiles();
        if (orderFilesArray == null) {
            throw new DataPersistenceException("Could not find any order files.");
        }

        // You really thought I wouldn't take the chance to use streams and lambdas again
        List<File> orderFiles = Arrays.stream(orderFilesArray)
                .filter(file -> file.getName().startsWith("Orders_") && file.getName().endsWith(".txt"))
                .toList(); // I'm allowed to use this instead of collect(Collectors.toList()) since I'm using Java 17 :)

        // Ensure there are order files to process
        if (orderFiles.isEmpty()) {
            throw new DataPersistenceException("No order files found for export.");
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(BACKUP_FILE))) {
            for (File file : orderFiles) {
                LocalDate date = LocalDate.parse(file.getName().substring(7, 15), DateTimeFormatter.ofPattern("MMddyyyy"));

                try (Scanner sc = new Scanner(new BufferedReader(new FileReader(file)))) {
                    if (sc.hasNextLine()) {
                        sc.nextLine(); // Skip header row
                    }

                    while (sc.hasNextLine()) {
                        String orderLine = sc.nextLine();
                        out.println(orderLine + DELIMITER + date.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
                    }
                }
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Could not export orders to " + BACKUP_FILE, e);
        }
    }

}
