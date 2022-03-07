package by.dictionary.converter;

import java.util.List;

public class OutputDictionaryObject {
    private String id;
    private String name;
    private List<Record> records;

    public OutputDictionaryObject(String id,
                                  String name,
                                  List<Record> records) {
        this.id = id;
        this.name = name;
        this.records = records;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Record> getRecords() {
        return records;
    }

}
