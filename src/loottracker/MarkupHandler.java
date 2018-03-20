package loottracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
                        /*markupTable.forEach((k,v) -> {
                            System.out.println(k + "\t" + v);
                        });*/
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
			// No Markup data found
			System.out.println("No Markup data found for " + itemName);
			return 1;
		}
	}
        
        public void addMarkup(String name, double markup){
            markupTable.put(name, markup);
        }
              
        public void displayMarkup(){
            markupTable.forEach((k, v) -> {
                System.out.println(k + "\t" + v);
            });
        }
}
