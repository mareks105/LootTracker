package loottracker;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkupHandler {
	private Map<String, Double> markupTable = new HashMap<>();
	private Charset charset = Charset.forName("ISO-8859-1");
	
	public MarkupHandler(Path markupFile) {
		try {
			List<String> lines = Files.readAllLines(markupFile, this.charset);
                        lines.stream()
                                .map((line) -> line.split("\t"))
                                .forEachOrdered((parts) -> {
                            String itemName = parts[0];
                            String markupString = parts[1];
                            Double markup = Double.parseDouble(markupString);
                            markupTable.put(itemName, markup);
                    });
		}
		catch (IOException e) {
			System.out.println("Inventory File not Found!");
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
        
        public void displayMarkup(){
            markupTable.forEach((k, v) -> {
                System.out.println(k + "\t" + v);
            });
        }
}
