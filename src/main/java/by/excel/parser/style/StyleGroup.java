package by.excel.parser.style;

import java.util.ArrayList;

public class StyleGroup {
    private String category;
    private ArrayList<StyleInfo> records;

    public StyleGroup(String category, ArrayList<StyleInfo> records) {
        this.category = category;
        this.records = records;
    }

    public String getCategory() {
        return category;
    }

    public ArrayList<StyleInfo> getRecords() {
        return records;
    }

}
