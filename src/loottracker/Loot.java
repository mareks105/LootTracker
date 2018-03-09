package loottracker;

public class Loot extends Item {
	private boolean isSold = false;
	private double markup;
	
	public Loot(String name, double valueTT) {
		super(name, valueTT);
	}
	
	public void setSoldWithMarkup(double markup) {
		this.markup = valueTT * markup;
		isSold = true;
	}
	
	public double getMarkup() {
		return this.markup;
	}
	
	public boolean isSold() {
		return isSold;
	}
}
