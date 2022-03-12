package by.dictionary.converter;

import by.spelling.conversion.converter.lacink.NarkamLacinkConverter;
import by.spelling.conversion.converter.tarask.NarkamTaraskConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class ConvertExcel2Json {

    public ConvertExcel2Json() {
    }

    public static void main(String[] args) throws URISyntaxException {
        TreeMap<String, ArrayList<DoubleId>> union = new TreeMap<>();
        for (Dictonary dictonary : Dictonary.values()) {
            List<Record> dictionaryRecords = convertDictonary(dictonary);
            dictionaryRecords.forEach(record -> {
                DoubleId newDoubleId = new DoubleId(dictonary.id, record.getId());
                if (union.get(record.getOriginalValue()) == null) {
                    ArrayList<DoubleId> links = new ArrayList<>();
                    links.add(newDoubleId);
                    union.put(record.getOriginalValue(), links);
                } else {
                    System.out.println(record.getOriginalValue());
                    union.get(record.getOriginalValue()).add(newDoubleId);
                }
            });
        }

        writeObjects2JsonFile(union, "generated/union.json");

        convertLabels();
    }

    static void writeObjects2JsonFile(TreeMap<String, ArrayList<DoubleId>> unionValues, String pathFile) {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        File file = new File(pathFile);
        try {
            // Serialize Java object info JSON file.
            mapper.writeValue(file, unionValues);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Record> convertDictonary(Dictonary dictonary) throws URISyntaxException {
        return dictonary.converter.convert(dictonary.id, dictonary.inputPath);
    }

    public static void convertLabels() {
        String userDir = System.getProperty("user.dir");

        String pathToNarkamFile = userDir + "\\generated\\labels\\narkam.js";
        String pathToTaraskFile = userDir + "\\generated\\labels\\tarask.js";
        String pathToLacinkFile = userDir + "\\generated\\labels\\lacink.js";

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
}