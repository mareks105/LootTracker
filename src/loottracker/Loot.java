package loottracker;

public class Loot extends Item {
	private boolean isSold = false;
	
	
	public Loot(String name, double valueTT) {
		super(name, valueTT);
	}
	
	public void setSoldWithMarkup(double markup) {
		this.markup = markup;
		isSold = true;
	}
	
	
	
	public boolean isSold() {
		return isSold;
	}
}
