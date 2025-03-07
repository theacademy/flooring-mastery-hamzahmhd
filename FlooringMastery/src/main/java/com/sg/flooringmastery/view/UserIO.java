package com.sg.flooringmastery.view;

import java.time.LocalDate;

public interface UserIO {

    void print(String msg);

    int readInt(String prompt);

    int readInt(String prompt, int min, int max);

    String readString(String prompt);

    LocalDate readLocalDate(String prompt) throws InvalidUserInputException;
}
