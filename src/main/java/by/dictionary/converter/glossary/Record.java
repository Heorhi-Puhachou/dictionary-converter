package by.dictionary.converter.glossary;

public class Record {


    int id;
    private String originalValue;
    private String value;
    private String wrong;
    private String comment;

    public Record(int id,
                  String originalValue,
                  String value,
                  String wrong,
                  String comment) {
        this.id = id;
        this.originalValue = originalValue;
        this.value = value;
        this.wrong = wrong;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public String getValue() {
        return value;
    }

    public String getWrong() {
        return wrong;
    }

    public String getComment() {
        return comment;
    }

}