package by.dictionary.converter;

import java.util.ArrayList;

public class UnionValue {
    String value;
    ArrayList<DoubleId> doubleIds;

    public UnionValue(String value,
                      ArrayList<DoubleId> doubleIds) {
        this.value = value;
        this.doubleIds = doubleIds;
    }

    public String getValue() {
        return value;
    }

    public ArrayList<DoubleId> getDoubleIds() {
        return doubleIds;
    }
}
