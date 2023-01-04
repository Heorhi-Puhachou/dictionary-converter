package by.dictionary.converter.e;

import by.dictionary.converter.Converter;
import by.dictionary.converter.Record;
import by.spelling.conversion.converter.lacink.TaraskLacinkConverter;
import by.spelling.conversion.converter.tarask.NarkamTaraskConverter;
import by.spelling.conversion.converter.tarask.TaraskNarkamConverter;
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

public class EConverter implements Converter {
    private static final String GLOSSARY_SHEET_NAME = "Слоўнік";

    @Override
    public List<Record> convert(String id, String inputDictonary) throws URISyntaxException {
        URL aURL = getClass().getClassLoader().getResource(inputDictonary);
        File aFile = Paths.get(aURL.toURI()).toFile();
        String inputFile = aFile.getAbsolutePath();

        List<Record> aTarRecords = readExcelFile(
                inputFile,
                GLOSSARY_SHEET_NAME);


        aTarRecords = convert(aTarRecords, new NarkamTaraskConverter());


        List<Record> aNarRecords = convert(aTarRecords, new TaraskNarkamConverter());
        List<Record> aLacRecords = convert(aTarRecords, new TaraskLacinkConverter());

        readConvertWriteGlossary(aNarRecords, "generated/e/narkam.json");
        readConvertWriteGlossary(aTarRecords, "generated/e/tarask.json");
        readConvertWriteGlossary(aLacRecords, "generated/e/lacink.json");

        return aTarRecords;
    }


    public void readConvertWriteGlossary(List<Record> records, String writePath) {
        writeObjects2JsonFile(records, writePath);
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

                if (desc != null && !desc.isEmpty()) {
                    record = new Record(id,
                            originalValue,
                            translate + " - " + desc);
                } else {
                    record = new Record(id,
                            originalValue,
                            translate);
                }
                lstCustomers.add(record);
            }

            // Close WorkBook
            workbook.close();

            return lstCustomers;
        } catch (IOException e) {
            throw new RuntimeException("FAIL! -> message = " + e.getMessage());
        }
    }
}
