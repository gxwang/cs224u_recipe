package cs224u.ingredients;

import java.util.HashSet;

public class Ingredient{
	private String baseIngredient;
	private HashSet<String> properties;
	private IngredientQuantity quantities;
	public static final String NULL = "<NULL>";
	
	public Ingredient() {
		super();
		baseIngredient = NULL;
		properties = new HashSet<String>();	
	}

	public Ingredient(String baseIngredient, IngredientQuantity quantities) {
		super();
		this.baseIngredient = baseIngredient;
		this.quantities = quantities;
	}

	public void addToProps(String prop){
		properties.add(prop);
	}
	
	public HashSet<String> getProps(){
		return properties;
	}
	
	public void setQuant(IngredientQuantity quant){
		quantities = quant;
	}
	
	public IngredientQuantity getQuant(){
		return quantities;
	}
	
	@Override
	public String toString(){
		return "Ingredient: " + baseIngredient + " Quant: " + quantities.toString() + " Properties: " + properties.toString();
	}
	
	public String getBaseIngredient() {
		return baseIngredient;
	}
	public void setBaseIngredient(String baseIngredient) {
		this.baseIngredient = baseIngredient;
	}	
	
}
