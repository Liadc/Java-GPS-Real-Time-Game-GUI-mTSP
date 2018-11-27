package File_format;

import GIS.GIS_layer;

import java.io.File;
import java.io.IOException;

public class Csv2Layer {
    public static GIS_layer csv2Layer(String fileName) {
        String path = System.getProperty("user.dir")+ File.separator;;
        File csv = new File(path+File.separator+"dataExamples"+File.separator+fileName);
        CsvTable csvTable = null;
        try {
            csvTable = new CsvTable(csv);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TableLayer layer = new TableLayer();
        GIS_layer testlayer = layer.TableLayer(csvTable, csv.getName());
        return testlayer;
    }
}
