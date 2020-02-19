package loottracker;

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
        
        public void print() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		System.out.format("Name: %-10s\tStartValue: %-1s\tMarkup: %-1s\tEndValue: %-1s\n", this.name, 
                        formatter.format(this.valueTT), formatter.format(this.markup),
                        formatter.format(this.endValue));
	}
}
