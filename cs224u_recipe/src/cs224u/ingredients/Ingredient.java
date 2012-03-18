package cs224u.ingredients;

public class Ingredient {

	private String baseIngredient;
	private double quantity;
	private String units;
	
	public Ingredient() {
		super();
	}

	public Ingredient(String baseIngredient, double quantity) {
		super();
		this.baseIngredient = baseIngredient;
		this.quantity = quantity;
	}

	public Ingredient(String baseIngredient, double quantity, String units) {
		super();
		this.baseIngredient = baseIngredient;
		this.quantity = quantity;
		this.units = units;
	}
	
	public String getBaseIngredient() {
		return baseIngredient;
	}
	public void setBaseIngredient(String baseIngredient) {
		this.baseIngredient = baseIngredient;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	
	
	
}
