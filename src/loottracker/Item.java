package loottracker;

import java.text.*;

public class Item {
	
	protected String name;
	protected double valueTT;
	
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
	
	public void changeValue(double newValue) {
		this.valueTT = newValue;
	}
	
	public void addValue(double value) {
		this.valueTT += value;
	}
	
	public void print() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		System.out.format("%-10s\t%-1s\n", this.name, formatter.format(this.valueTT));
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(! Item.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		final Item other = (Item)obj;
		if((this.name == null) ? (other.name != null) : ! this.name.equals(other.name)) {
			return false;
		}
		if(this.valueTT != other.valueTT) {
			return false;
		}
		return true;
	}
}
