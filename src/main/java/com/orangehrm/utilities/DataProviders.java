package com.orangehrm.utilities;

import org.testng.annotations.DataProvider;

import java.util.List;

public class DataProviders {
    private static final String FILE_PATH = System.getProperty("user.dir") + "/src/test/resources/testdata/TestData.xlsx";

    @DataProvider(name = "validLoginData")
    public static Object[][] validLoginData() {
        return getSheetData("validLoginData");
    }

    @DataProvider(name = "inValidLoginData")
    public static Object[][] inValidLoginData() {
        return getSheetData("inValidLoginData");
    }

    @DataProvider(name = "empVerification")
    public static Object[][] empVerification() {
        return getSheetData("empVerification");
    }

    private static Object[][] getSheetData(String sheetName) {
        List<String[]> sheetData = ExcelReaderUtility.getSheetData(FILE_PATH, sheetName);
        Object[][] data = new Object[sheetData.size()][sheetData.getFirst().length];

        //For loop to iterate through the sheet data and convert it to Object[][] format
        for (int i = 0; i < sheetData.size(); i++) {
            data[i] = sheetData.get(i);
        }
        return data;
    }


}
