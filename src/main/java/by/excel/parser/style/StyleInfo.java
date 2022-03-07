package by.excel.parser.style;

public class StyleInfo {

    private String description;
    private String example;

    public StyleInfo(String description, String example) {
        this.description = description;
        this.example = example;
    }

    public String getExample() {
        return example;
    }

    public String getDescription() {
        return description;
    }
}
