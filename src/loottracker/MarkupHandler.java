package loottracker;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class MarkupHandler {
    
    private Map<String, Double> markupTable = new HashMap<>();
    private String markupFile;

    public MarkupHandler(String markupFile) {
            try {
                    this.markupFile = markupFile;
                    ObjectMapper mapper = new ObjectMapper();
                    this.markupTable = mapper.readValue(new File(this.markupFile),
                            new TypeReference<Map<String, Double>>(){});
            }
            catch (IOException e) {
                    System.out.println("Markup File not Found!");
            }
    }

    public MarkupHandler(Map<String, Double> markupTable){
        this.markupTable = markupTable;
    }

    public Map<String, Double> getMarkupTable(){
        return this.markupTable;
    }

    public double getMarkup(String itemName) {
            if(markupTable.containsKey(itemName)) {
                    return markupTable.get(itemName);
            }
            else {
                    throw new MarkupHandlerException("No markup for item " + itemName);
            }
    }

    public void addMarkup(String name, double markup){
        markupTable.put(name, markup);
    }

    public void removeMarkup(String name){
        markupTable.remove(name);
    }

    public void updateMarkup(Vector<Vector<String>> markupData){
        markupData.forEach((Vector<String> row)->{
            markupTable.put(row.elementAt(0), Double.parseDouble(row.elementAt(1)) / 100.0);
        });
    }

    public void displayMarkup(){
        markupTable.forEach((k, v) -> {
            System.out.println(k + "\t" + v);
        });
    }

    public void saveToDisk(JsonGenerator generator) throws IOException {
        for (Map.Entry<String, Double> entry : this.markupTable.entrySet()) {
            generator.writeNumberField(entry.getKey(), entry.getValue());
        }
    }
}
