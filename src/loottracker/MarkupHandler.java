package loottracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;

public class MarkupHandler {
	private Map<String, Double> markupTable = new HashMap<>();
	private Charset charset = Charset.forName("ISO-8859-1");
	private Path markupFile;
	public MarkupHandler(Path markupFile) {
		try {
                        this.markupFile = markupFile;
                        ObjectMapper mapper = new ObjectMapper();
                        this.markupTable = mapper.readValue(new File(this.markupFile.toString()),
                                new TypeReference<Map<String, Double>>(){});
                        /*markupTable.forEach((k,v) -> {
                            System.out.println(k + "\t" + v);
                        });*/
		}
		catch (IOException e) {
			System.out.println("Markup File not Found!");
		}
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
        
        public boolean updateMarkupFile(){
            try{
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(new File(this.markupFile.toString()), this.markupTable);
            }
            catch (IOException e){
                return false;
            }
            return true;
        }
                
              
        public void displayMarkup(){
            markupTable.forEach((k, v) -> {
                System.out.println(k + "\t" + v);
            });
        }
}
