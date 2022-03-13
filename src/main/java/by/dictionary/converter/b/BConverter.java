package by.dictionary.converter.b;

import by.dictionary.converter.Converter;
import by.dictionary.converter.Record;
import by.spelling.conversion.converter.lacink.NarkamLacinkConverter;
import by.spelling.conversion.converter.tarask.NarkamTaraskConverter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BConverter implements Converter {
    private static final String GLOSSARY_SHEET_NAME = "Sheet1";

    @Override
    public List<Record>  convert(String id, String inputDictonary) throws URISyntaxException {
        URL aURL = getClass().getClassLoader().getResource(inputDictonary);
        File aFile = Paths.get(aURL.toURI()).toFile();
        String inputFile = aFile.getAbsolutePath();

        List<Record> aNarRecords = readExcelFile(
                inputFile,
                GLOSSARY_SHEET_NAME);
        List<Record> aTarRecords = convert(aNarRecords, new NarkamTaraskConverter());
        List<Record> aLacRecords = convert(aNarRecords, new NarkamLacinkConverter());

        readConvertWriteGlossary(aNarRecords, "generated/b/narkam.json");
        readConvertWriteGlossary(aTarRecords, "generated/b/tarask.json");
        readConvertWriteGlossary(aLacRecords, "generated/b/lacink.json");

        return aNarRecords;
    }


    public void readConvertWriteGlossary(List<Record> records, String writePath) {
        writeObjects2JsonFile(records, writePath);
    }

    private List<Record> readExcelFile(String filePath, String sheetName) {
        try {
            Workbook workbook = getWorkbook(filePath);
            Iterator<Row> rows = getSheetIterator(workbook, sheetName);
            List<Record> lstCustomers = new ArrayList<Record>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }


                Iterator<Cell> cellsInRow = currentRow.iterator();

                Record record = null;
                String originalValue = "";
                String desc1 = "";
                String desc2 = "";
                String value = "";


                int cellIndex = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    if (cellIndex == 0) { // Original value
                        originalValue = currentCell.getStringCellValue();
                    } else if (cellIndex == 1) { // Description [en] 1
                        desc1 = currentCell.getStringCellValue();
                    } else if (cellIndex == 2) { // Description [en] 2
                        desc2 = currentCell.getStringCellValue();
                    } else if (cellIndex == 3) { // Term
                        value = currentCell.getStringCellValue();
                    }


                    cellIndex++;
                }

                if (originalValue.isEmpty()) {
                    break;
                }

                record = new Record(rowNumber,
                        originalValue,
                        getInformationString(desc1, desc2, value));
                lstCustomers.add(record);
                rowNumber++;
            }

            // Close WorkBook
            workbook.close();

            return lstCustomers;
        } catch (IOException e) {
            throw new RuntimeException("FAIL! -> message = " + e.getMessage());
        }
    }

    private String getInformationString(String desc1, String desc2, String value) {
        return desc1 + " " + desc2 + "\n" + value;
    }


}
