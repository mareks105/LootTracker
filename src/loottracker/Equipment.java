package loottracker;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 * @author mege9
 */
public class Equipment extends Item {
	private double endValue;
        protected EquipmentType type;
	
	public Equipment(String name, double startValue, double markup) {
		this(name, startValue, markup, 0.0);
	}
	
	public Equipment(String name, double startValue, double markup, double endValue) {
		super(name, startValue);
		this.markup = markup;
		this.endValue = endValue;
	}
	
        public EquipmentType getType(){
            return this.type;
        }
        
	public void setEndValue(double endValue) {
		this.endValue = endValue;
	}
	
        public double getEndValue(){
            return this.endValue;
        }
        
	public double getDecayTT() {
		return Utilities.round(getDecayTT(this.endValue), 2);
	}
	
	public double getDecayTT(double endValue) {
		return Utilities.round(this.valueTT - endValue,2);
	}
	
	public double getDecayWithMarkup() {
		return Utilities.round(getDecayWithMarkup(this.endValue),2);
	}
	
	public double getDecayWithMarkup(double endValue) {
		return Utilities.round(getDecayTT(endValue) * this.markup,2);
	}
        
        @Override
        public boolean equals(Object v){
            boolean retVal = false;
            if(v instanceof Equipment){
                Equipment e = (Equipment)v;
                retVal = e.getName().equals(this.name);
            }
            return retVal;
        }
        
        @Override
        public int hashCode(){
            int hash = 7;
            hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
            return hash;
        }
        
        public void print() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		System.out.format("Name: %-10s\tStartValue: %-1s\tMarkup: %-1s\tEndValue: %-1s\n", this.name, 
                        formatter.format(this.valueTT), formatter.format(this.markup),
                        formatter.format(this.endValue));
	}
        
        public void saveToDisk(JsonGenerator generator) throws IOException {
            generator.writeStartObject();
            generator.writeStringField("name", this.name);
            generator.writeStringField("type", EquipmentUtilities.getTypeForEquipment(this));
            generator.writeNumberField("valueTT", this.valueTT);
            generator.writeNumberField("markup", this.markup);
            generator.writeNumberField("endValue", this.endValue);
            generator.writeEndObject();
        }
}
