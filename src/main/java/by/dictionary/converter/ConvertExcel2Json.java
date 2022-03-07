package by.dictionary.converter;

import by.dictionary.converter.glossary.Record;
import by.spelling.conversion.converter.BaseConverter;
import by.spelling.conversion.converter.lacink.NarkamLacinkConverter;
import by.spelling.conversion.converter.tarask.NarkamTaraskConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

public class ConvertExcel2Json {

    private static String INPUT_FILE = null;
    private static final String GLOSSARY_SHEET_NAME = "Спіс тэрмінаў";

    public ConvertExcel2Json() {
    }

    public static void main(String[] args) throws URISyntaxException {
        INPUT_FILE = (new ResourceLoader()).getAPath();
        readConvertWriteGlossary(new BaseConverter(), "generated/a/narkam.json");
        readConvertWriteGlossary(new NarkamTaraskConverter(), "generated/a/tarask.json");
        readConvertWriteGlossary(new NarkamLacinkConverter(), "generated/a/lacink.json");

        convertLabels();

        System.out.println("Канвертацыя скончана.");
    }

    public static void convertLabels() {

        String pathToNarkamFile = System.getProperty("user.dir") + "\\generated\\labels\\narkam.js";
        String pathToTaraskFile = System.getProperty("user.dir") + "\\generated\\labels\\tarask.js";
        String pathToLacinkFile = System.getProperty("user.dir") + "\\generated\\labels\\lacink.js";

        String narkamText = readTextFromFile(pathToNarkamFile);

        String taraskText = (new NarkamTaraskConverter()).convert(narkamText.toString()).replace("NARKAM", "TARASK");
        String lacinkText = (new NarkamLacinkConverter()).convert(narkamText.toString()).replace("NARKAM", "LACINK");

        writeTextToFile(lacinkText, pathToLacinkFile);
        writeTextToFile(taraskText, pathToTaraskFile);
    }

    private static void writeTextToFile(String text, String filePath) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            fileOutputStream.write(text.getBytes());
        } catch (FileNotFoundException e) {
            // exception handling
        } catch (IOException e) {
            // exception handling
        }
    }

    private static String readTextFromFile(String filePath) {
        StringJoiner text = new StringJoiner("\n");
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                text.add(line);
                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            // Exception handling
        } catch (IOException e) {
            // Exception handling
        }

        return text.toString();
    }

    public static void readConvertWriteGlossary(BaseConverter baseConverter, String writePath) {
        List<Record> records = readExcelFile(
                INPUT_FILE,
                GLOSSARY_SHEET_NAME,
                baseConverter);
        writeObjects2JsonFile(records, writePath);
    }


    /**
     * Read Excel File into Java List Objects
     *
     * @param filePath
     * @return
     */
    private static List<Record> readExcelFile(String filePath, String sheetName, BaseConverter converter) {
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
                        converter.convert(acadValue),
                        converter.convert(acadWrong),
                        converter.convert(acadComment));

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

    /**
     * Convert Java Objects to JSON File
     *
     * @param list
     * @param pathFile
     */
    private static void writeObjects2JsonFile(List<?> list, String pathFile) {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        File file = new File(pathFile);
        try {
            // Serialize Java object info JSON file.
            mapper.writeValue(file, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Workbook getWorkbook(String filePath) throws IOException {
        FileInputStream excelFile = new FileInputStream(new File(filePath));
        return new XSSFWorkbook(excelFile);
    }

    private static Iterator<Row> getSheetIterator(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        return sheet.iterator();
    }
}