package by.dictionary.converter;

import by.dictionary.converter.a.AConverter;
import by.dictionary.converter.b.BConverter;
import by.dictionary.converter.c.CConverter;
import by.dictionary.converter.d.DConverter;

public enum Dictonary {

    UBUNTU("a",
            "Гласарый і стайлгайд перакладу Ubuntu на беларускую мову.xlsx",
            new AConverter()),
    PROTON("b",
            "Terms_for_BE_translated_by_Alaksandr_Košal;_revised_by_Źmicier_Turok.xlsx",
            new BConverter()),
    PUHACHOU("c",
            "Слоўнік Пугачова.xlsx",
            new CConverter()),
    COLOR("c",
                     "Колеры.xlsx",
                     new DConverter());

    public final String id;
    public final String inputPath;
    public final Converter converter;

    Dictonary(String id,
              String inputPath,
              Converter converter) {
        this.id = id;
        this.inputPath = inputPath;
        this.converter = converter;
    }
}
