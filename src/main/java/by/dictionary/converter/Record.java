package by.dictionary.converter;

public class Record {


    private int id;
    private String originalValue;
    private String information;

    public Record(int id,
                  String originalValue,
                  String information) {
        this.id = id;
        this.originalValue = originalValue;
        this.information = information;
    }

    public int getId() {
        return id;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public String getInformation() {
        return information;
    }

}