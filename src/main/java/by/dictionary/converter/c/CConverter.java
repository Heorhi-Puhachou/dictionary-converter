package by.dictionary.converter.c;

import by.dictionary.converter.Converter;
import by.dictionary.converter.Dictonary;
import by.dictionary.converter.OutputDictionaryObject;
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

public class CConverter implements Converter {
    private static final String GLOSSARY_SHEET_NAME = "Слоўнік";

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

        readConvertWriteGlossary(
                new OutputDictionaryObject(id, "Слоўнік Георгія Пугачова", aNarRecords),
                "generated/c/narkam.json"
        );
        readConvertWriteGlossary(
                new OutputDictionaryObject(id, "Слоўнік Георгія Пугачова", aTarRecords),
                "generated/c/tarask.json"
        );
        readConvertWriteGlossary(
                new OutputDictionaryObject(id, "Слоўнік Георгія Пугачова", aLacRecords),
                "generated/c/lacink.json"
        );

        return aTarRecords;
    }


    public void readConvertWriteGlossary(OutputDictionaryObject outputDictionaryObject, String writePath) {
        writeObjects2JsonFile(outputDictionaryObject, writePath);
    }

    private List<Record> readExcelFile(String filePath, String sheetName) {
        try {
            Workbook workbook = getWorkbook(filePath);
            Iterator<Row> rows = getSheetIterator(workbook, sheetName);
            List<Record> lstCustomers = new ArrayList<Record>();
            while (rows.hasNext()) {
                Row currentRow = rows.next();


                Iterator<Cell> cellsInRow = currentRow.iterator();

                Record record = null;
                Integer id = 0;
                String originalValue = "";
                String translate = "";
                String desc = "";

                int cellIndex = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    if (cellIndex == 0) {
                        id = (new Double(currentCell.getNumericCellValue())).intValue();
                    } else if (cellIndex == 1) {
                        originalValue = currentCell.getStringCellValue();
                    } else if (cellIndex == 2) {
                        translate = currentCell.getStringCellValue();
                    } else if (cellIndex == 3) {
                        desc = currentCell.getStringCellValue();
                    }
                    cellIndex++;
                }

                if (originalValue.isEmpty()) {
                    break;
                }

                record = new Record(id,
                        originalValue,
                        translate + "\n" + desc);
                lstCustomers.add(record);
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
