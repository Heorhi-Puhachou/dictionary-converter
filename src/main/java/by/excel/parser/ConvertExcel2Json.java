package by.excel.parser;

import java.io.*;
import java.util.*;

import by.convert.lacink.NarkamLacinkConverter;
import by.convert.tarask.NarkamTaraskConverter;
import by.convert.BaseConverter;
import by.excel.parser.glossary.Record;
import by.excel.parser.links.Link;
import by.excel.parser.links.LinkGroup;
import by.excel.parser.style.StyleGroup;
import by.excel.parser.style.StyleInfo;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvertExcel2Json {

    private static final String INPUT_FILE = "Гласарый і стайлгайд перакладу Ubuntu на беларускую мову.xlsx";
    private static final String GLOSSARY_SHEET_NAME = "Спіс тэрмінаў";
    private static final String LINKS_SHEET_NAME = "Карысныя спасылкі";
    private static final String STYLE_SHEET_NAME = "Стайлгайд";

    public static void main(String[] args) {
        readConvertWriteGlossary(new BaseConverter(), "generated/glossary/narkam.json");
        readConvertWriteGlossary(new NarkamTaraskConverter(), "generated/glossary/tarask.json");
        readConvertWriteGlossary(new NarkamLacinkConverter(), "generated/glossary/lacink.json");

        readConvertWriteLinks(new BaseConverter(), "generated/links/narkam.json");
        readConvertWriteLinks(new NarkamTaraskConverter(), "generated/links/tarask.json");
        readConvertWriteLinks(new NarkamLacinkConverter(), "generated/links/lacink.json");

        readConvertWriteStyle(new BaseConverter(), "generated/style/narkam.json");
        readConvertWriteStyle(new NarkamTaraskConverter(), "generated/style/tarask.json");
        readConvertWriteStyle(new NarkamLacinkConverter(), "generated/style/lacink.json");

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

    public static void readConvertWriteLinks(BaseConverter baseConverter, String writePath) {
        ArrayList<LinkGroup> linksInfo = readLinksInfo(
                INPUT_FILE,
                LINKS_SHEET_NAME,
                baseConverter);
        writeObjects2JsonFile(linksInfo, writePath);
    }

    public static void readConvertWriteStyle(BaseConverter converter, String writePath) {
        ArrayList<StyleGroup> styleInfo = readStyleInfo(
                INPUT_FILE,
                STYLE_SHEET_NAME,
                converter);
        writeObjects2JsonFile(styleInfo, writePath);
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


    /**
     * Read Excel File into Java List Objects
     *
     * @param filePath
     * @return
     */
    private static ArrayList<LinkGroup> readLinksInfo(String filePath, String sheetName, BaseConverter converter) {
        try {
            Workbook workbook = getWorkbook(filePath);
            Iterator<Row> rows = getSheetIterator(workbook, sheetName);

            ArrayList<LinkGroup> linkGroups = new ArrayList<>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }


                Iterator<Cell> cellsInRow = currentRow.iterator();

                String category = "";
                String url = "";
                String description = "";


                int cellIndex = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    if (cellIndex == 0) { // Original value
                        category = converter.convert(currentCell.getStringCellValue());
                    } else if (cellIndex == 1) { // acad value
                        url = currentCell.getStringCellValue();
                    } else if (cellIndex == 2) { // Wrong example
                        description = converter.convert(currentCell.getStringCellValue());
                    }

                    cellIndex++;
                }

                if (url.isEmpty()) {
                    break;
                }

                Link link = new Link(url, description);
                if (category.isEmpty()) {
                    category = "*";
                }

                if (getGroupByName(category, linkGroups) == null) {
                    LinkGroup newGroup = new LinkGroup(category, new ArrayList<>());
                    linkGroups.add(newGroup);
                }
                getGroupByName(category, linkGroups).getLinks().add(link);
                rowNumber++;
            }
            workbook.close();
            return linkGroups;
        } catch (IOException e) {
            throw new RuntimeException("FAIL! -> message = " + e.getMessage());
        }
    }

    private static ArrayList<StyleGroup> readStyleInfo(String filePath, String sheetName, BaseConverter converter) {
        try {
            Workbook workbook = getWorkbook(filePath);
            Iterator<Row> rows = getSheetIterator(workbook, sheetName);

            ArrayList<StyleGroup> styleGroups = new ArrayList<>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }


                Iterator<Cell> cellsInRow = currentRow.iterator();

                String category = "";
                String description = "";
                String example = "";


                int cellIndex = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    if (cellIndex == 0) { // Original value
                        category = converter.convert(currentCell.getStringCellValue());
                    } else if (cellIndex == 1) { // acad value
                        description = converter.convert(currentCell.getStringCellValue());
                    } else if (cellIndex == 2) { // Wrong example
                        example = converter.convert(currentCell.getStringCellValue());
                    }

                    cellIndex++;
                }

                if (description.isEmpty()) {
                    break;
                }

                StyleInfo styleInfo = new StyleInfo(description, example);

                if (getStyleGroupByName(category, styleGroups) == null) {
                    StyleGroup newGroup = new StyleGroup(category, new ArrayList<>());
                    styleGroups.add(newGroup);
                }
                getStyleGroupByName(category, styleGroups).getRecords().add(styleInfo);
                rowNumber++;
            }
            workbook.close();
            return styleGroups;
        } catch (IOException e) {
            throw new RuntimeException("FAIL! -> message = " + e.getMessage());
        }
    }

    private static LinkGroup getGroupByName(String name, List<LinkGroup> groups) {
        LinkGroup result = null;
        for (LinkGroup group : groups) {
            if (group.getGroupName().equals(name)) {
                result = group;
            }
        }
        return result;
    }

    private static StyleGroup getStyleGroupByName(String name, List<StyleGroup> groups) {
        StyleGroup result = null;
        for (StyleGroup group : groups) {
            if (group.getCategory().equals(name)) {
                result = group;
            }
        }
        return result;
    }
}