package loottracker;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;

public class Loot extends Item {
	private boolean isSold = false;
	
	
	public Loot(String name, double valueTT) {
		super(name, valueTT);
	}
        
        public Loot(String name, double valueTT, double markup) {
		super(name, valueTT);
                this.markup = markup;
	}
	
	public void setSoldWithMarkup(double markup) {
		this.markup = markup;
		isSold = true;
	}
		
	public boolean isSold() {
		return isSold;
	}
        
        public void saveToDisk(JsonGenerator generator) throws IOException {
            generator.writeStartObject();
            generator.writeStringField("name", this.name);
            generator.writeNumberField("valueTT", this.valueTT);
            generator.writeEndObject();
        }
}
