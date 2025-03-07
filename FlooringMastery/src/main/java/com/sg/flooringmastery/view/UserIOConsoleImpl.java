package com.sg.flooringmastery.view;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

@Component
public class UserIOConsoleImpl implements UserIO{

    Scanner sc = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Override
    public void print(String msg) {
        System.out.println(msg);
    }

    @Override
    public int readInt(String prompt) {
        System.out.println(prompt);
        while (!sc.hasNextInt()) {
            sc.next();
            System.out.println("Invalid input. Please enter a valid number.");
        }
        return sc.nextInt();
    }

    @Override
    public int readInt(String prompt, int min, int max) {
        int input;
        do {
            System.out.println(prompt);
            while (!sc.hasNextInt()) {
                sc.next();
                System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
            }
            input = sc.nextInt();
            sc.nextLine();
        } while (input < min || input > max);
        return input;
    }

    @Override
    public String readString(String prompt) {
        System.out.println(prompt);

        if (sc.hasNextLine()) {
            String input = sc.nextLine().trim();

            // If the first read is empty, attempt reading again (handles cases where an int left a newline)
            if (input.isEmpty() && sc.hasNextLine()) {
                input = sc.nextLine().trim();
            }

            return input;
        }

        return "";
    }



    @Override
    public LocalDate readLocalDate(String prompt) {
        while (true) {
            System.out.println(prompt + " (MM/dd/yyyy): ");
            if (sc.hasNextLine()) {
                String input = sc.nextLine().trim();
                try {
                    return LocalDate.parse(input, DATE_FORMAT);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid input! Please enter the date in MM/dd/yyyy format.");
                }
            }
        }
    }





}
