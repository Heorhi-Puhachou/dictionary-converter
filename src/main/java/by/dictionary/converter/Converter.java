package by.dictionary.converter;

import by.spelling.conversion.converter.BaseConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public interface Converter {

    public List<Record>  convert(String id, String inputDictonary) throws URISyntaxException;

   default Workbook getWorkbook(String filePath) throws IOException {
        FileInputStream excelFile = new FileInputStream(new File(filePath));
        return new XSSFWorkbook(excelFile);
    }

    default Iterator<Row> getSheetIterator(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        return sheet.iterator();
    }

    default List<Record> convert(List<Record> aNarRecords, BaseConverter converter) {
        return aNarRecords
                .stream().
                map(narRecord -> new Record(narRecord.getId(),
                        narRecord.getOriginalValue(),
                        converter.convert(narRecord.getInformation())))
                .collect(Collectors.toList());
    }

    default void writeObjects2JsonFile(OutputDictionaryObject outputDictionaryObject, String pathFile) {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        File file = new File(pathFile);
        try {
            // Serialize Java object info JSON file.
            mapper.writeValue(file, outputDictionaryObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
