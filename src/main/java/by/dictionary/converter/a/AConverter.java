package by.dictionary.converter.a;

import by.dictionary.converter.Converter;
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

public class AConverter implements Converter {

    private static final String GLOSSARY_SHEET_NAME = "Спіс тэрмінаў";

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
                new OutputDictionaryObject(id, "Слоўнік UBUNTU", aNarRecords),
                "generated/a/narkam.json"
        );
        readConvertWriteGlossary(
                new OutputDictionaryObject(id, "Слоўнік UBUNTU", aTarRecords),
                "generated/a/tarask.json"
        );
        readConvertWriteGlossary(
                new OutputDictionaryObject(id, "Слоўнік UBUNTU", aLacRecords),
                "generated/a/lacink.json"
        );

        return aNarRecords;
    }

    public void readConvertWriteGlossary(OutputDictionaryObject outputDictionaryObject, String writePath) {
        writeObjects2JsonFile(outputDictionaryObject, writePath);
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
                String acadValue = "";
                String acadWrong = "";
                String acadComment = "";


                int cellIndex = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    if (cellIndex == 0) { // Original value
                        originalValue = currentCell.getStringCellValue();
                    } else if (cellIndex == 1) { // acad value
                        acadValue = currentCell.getStringCellValue();
                    } else if (cellIndex == 2) { // Wrong example
                        acadWrong = currentCell.getStringCellValue();
                    } else if (cellIndex == 3) { // Comment
                        acadComment = currentCell.getStringCellValue();
                    }


                    cellIndex++;
                }

                if (originalValue.isEmpty()) {
                    break;
                }

                record = new Record(rowNumber,
                        originalValue,
                        getInformationString(acadValue, acadWrong, acadComment));
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

    private String getInformationString(String value, String wrong, String comment) {
        String result = value;
        if (wrong != null && !wrong.isEmpty()) {
            result = result + "\nНяправільны пераклад: " + wrong;
        }
        if (comment != null && !comment.isEmpty()) {
            result = result + "\nКаментарый: " + comment;
        }
        return result;
    }


}
