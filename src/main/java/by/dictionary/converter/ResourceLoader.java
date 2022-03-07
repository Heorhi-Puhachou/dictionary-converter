package by.dictionary.converter;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class ResourceLoader {

    public String getAPath() throws URISyntaxException {
        URL aURL = getClass().getClassLoader().getResource("Гласарый і стайлгайд перакладу Ubuntu на беларускую мову.xlsx");
        File aFile = Paths.get(aURL.toURI()).toFile();
        return aFile.getAbsolutePath();
    }
}
