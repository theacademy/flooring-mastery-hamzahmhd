package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Tax;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Component
public class TaxDaoFileImpl implements TaxDao {

    private final String TAX_FILEPATH;
    public static final String DELIMITER = ",";

    private Map<String, Tax> taxMap = new HashMap<>();

    public TaxDaoFileImpl(){
        this.TAX_FILEPATH = Paths.get( "FlooringMastery", "Data", "Taxes.txt").toAbsolutePath().toString();
    }

    public TaxDaoFileImpl(String filePath){
        this.TAX_FILEPATH = filePath;
    }

    private Tax unmarshallTax(String taxAsText){
        String[] taxTokens = taxAsText.split(DELIMITER);
        String stateAbbreviation = taxTokens[0];

        Tax taxFromFile = new Tax(stateAbbreviation);

        taxFromFile.setStateName(taxTokens[1]);
        taxFromFile.setTaxRate(new BigDecimal(taxTokens[2]));

        return taxFromFile;
    }


    private void load() throws DataPersistenceException {
        Scanner sc;

        try{
            sc = new Scanner(new BufferedReader(new FileReader(TAX_FILEPATH)));
        }
        catch (FileNotFoundException e){
            throw new DataPersistenceException("Could not load tax data into memory.", e);
        }

        if (sc.hasNextLine()){
            sc.nextLine(); // skip header row
        }

        while (sc.hasNextLine()){
            String currentLine = sc.nextLine();
            Tax currentTax = unmarshallTax(currentLine);
            taxMap.put(currentTax.getStateAbbreviation(), currentTax);
        }

        sc.close();

    }


    @Override
    public Tax getByStateAbbreviation(String stateAbbrevation) throws DataPersistenceException {
        load();
        return taxMap.get(stateAbbrevation);
    }
}
