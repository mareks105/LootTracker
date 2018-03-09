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
		return getDecayTT(this.endValue);
	}
	
	public double getDecayTT(double endValue) {
		return endValue - this.valueTT;
	}
	
	public double getDecayWithMarkup() {
		return getDecayWithMarkup(this.endValue);
	}
	
	public double getDecayWithMarkup(double endValue) {
		return (endValue - this.valueTT) * this.markup;
	}
}
