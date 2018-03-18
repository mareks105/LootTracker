package loottracker;

/**
 *
 * @author mege9
 */
public abstract class Equipment extends Item {
	private double markup;
	private double endValue;
	
	public Equipment(String name, double startValue, double markup) {
		this(name, startValue, markup, 0.0);
	}
	
	public Equipment(String name, double startValue, double markup, double endValue) {
		super(name, startValue);
		this.markup = markup;
		this.endValue = endValue;
	}
	
	public void setEndValue(double endValue) {
		this.endValue = endValue;
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
}
