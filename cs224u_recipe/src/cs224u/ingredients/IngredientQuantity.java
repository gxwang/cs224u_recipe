package cs224u.ingredients;

/*
 * Class to hold a quantity and a unit
 */
public class IngredientQuantity implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8377517268438246774L;
	private String unit;
	private double quantity;
	
	public IngredientQuantity(){
		unit = "NONE";
		quantity = -1.0;
	}
	
	public IngredientQuantity(String unit, double quantity){
		this.unit = unit;
		this.quantity = quantity;
	}
	
	public void setUnit(String unit){
		this.unit = unit;
	}
	
	public void setQuantity(double quantity){
		this.quantity = quantity;
	}
	
	public String getUnit(){
		return unit;
	}
	
	public double getQuantity(){
		return quantity;
	}
	
	@Override
	public String toString(){
		return quantity + "_" + unit;
	}
	
}
