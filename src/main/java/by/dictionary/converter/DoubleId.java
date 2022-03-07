package by.dictionary.converter;

public class DoubleId {
    String dictionaryId;
    Integer termId;

    public DoubleId(String dictionaryId,
                    Integer termId) {
        this.dictionaryId = dictionaryId;
        this.termId = termId;
    }

    public String getDictionaryId() {
        return dictionaryId;
    }

    public Integer getTermId() {
        return termId;
    }
}
