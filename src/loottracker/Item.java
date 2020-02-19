package loottracker;

import java.text.*;

public class Item {
	
	protected String name;
	protected double valueTT;
        protected double markup;
	
	public Item(String name, double valueTT) {
		this.name = name;
		this.valueTT = valueTT;
	}
	
	public String getName() {
		return this.name;
	}
	
	public double getValue() {
		return this.valueTT;
	}
        
        public double getMarkup() {
		return this.markup;
	}
	
	public void changeValue(double newValue) {
		this.valueTT = newValue;
	}
	
	public void addValue(double value) {
		this.valueTT += value;
	}
	
        
	public void print() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		System.out.format("Name: %-10s\tValue: %-1s\n", this.name, formatter.format(this.valueTT));
	}
}
