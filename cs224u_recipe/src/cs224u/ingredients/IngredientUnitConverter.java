package cs224u.ingredients;

public class IngredientUnitConverter {

	private static final double TEASPOON_TO_ML = 4.92892159;
	private static final double TABLESPOON_TO_ML = 14.7867648;
	private static final double CUP_TO_ML = 236.588237;
	private static final double PINT_TO_ML = 473.176473;
	private static final double QUART_TO_ML = 2 * PINT_TO_ML;
	private static final double GAL_TO_ML = 4 * QUART_TO_ML;
	private static final double L_TO_ML = 1000;
	private static final double LBS_TO_GRAM = 453.59237;
	private static final double OZ_TO_GRAM = 28.3495231;
	private static final double MG_TO_GRAM = 0.001;
	private static final double KG_TO_GRAM = 1000;

	private static final String LIQUID_NORM_MEASURE = "ml";
	private static final String WEIGHT_NORM_MEASURE = "g";

	public static IngredientQuantity convert(IngredientQuantity iq) {
		IngredientQuantity converted = new IngredientQuantity();
		
		String unit = iq.getUnit();
		double quantity = iq.getQuantity();
		
		double unitChangeMod = 1;
		String normUnit = unit;

		/* liquid */
		if (unit.matches("teaspoon(s)?|t(s)?|tsp(s)?")) {
			unitChangeMod = TEASPOON_TO_ML;
			normUnit =  LIQUID_NORM_MEASURE;
		} 
		else if (unit.matches("tablespoon(s)?|T(s)?|tbl(s)?|tbs(s)?|tbsp(s)?")) {
			unitChangeMod = TABLESPOON_TO_ML;
			normUnit =  LIQUID_NORM_MEASURE;
		} 
		else if (unit.matches("cup(s)?|c(s)?")) {
			unitChangeMod = CUP_TO_ML;	
			normUnit =  LIQUID_NORM_MEASURE;
		} 
		else if (unit.matches("pint(s)?|p(s)?|pt(s)?|fl pt(s)?")) {
			unitChangeMod = PINT_TO_ML;
			normUnit =  LIQUID_NORM_MEASURE;
		} 
		else if (unit.matches("quart(s)?|q(s)?|qt(s)?|fl qt(s)?")) {
			unitChangeMod = QUART_TO_ML;
			normUnit =  LIQUID_NORM_MEASURE;
		} 
		else if (unit.matches("gallon(s)?|gal(s)?")) {
			unitChangeMod = GAL_TO_ML;
			normUnit =  LIQUID_NORM_MEASURE;
		} 
		else if (unit.matches("ml(s)?|milliliter(s)?|millilitre(s)?|cc(s)?|mL(s)?")) {
			normUnit =  LIQUID_NORM_MEASURE;
		} 
		else if (unit.matches("l(s)?|liter(s)?|litre(s)?|L(s)?")) {
			unitChangeMod = L_TO_ML;
			normUnit =  LIQUID_NORM_MEASURE;
		} 

		/* pounds and grams */
		else if (unit.matches("pound(s)?|lb(s)?|#(s)?")) {
			unitChangeMod = LBS_TO_GRAM;
			normUnit =  WEIGHT_NORM_MEASURE;
		}
		else if (unit.matches("ounce(s)?|oz(s)?")) {
			unitChangeMod = OZ_TO_GRAM;
			normUnit =  WEIGHT_NORM_MEASURE;
		}
		else if (unit.matches("mg(s)?|milligram(s)?|milligramme(s)?")) {
			unitChangeMod = MG_TO_GRAM;
			normUnit =  WEIGHT_NORM_MEASURE;
		}
		else if (unit.matches("g(s)?|gram(s)?|gramme(s)?")) {
			normUnit =  WEIGHT_NORM_MEASURE;
		}
		else if (unit.matches("kg(s)?|kilogram(s)?|kilogramme(s)?")) {
			unitChangeMod = KG_TO_GRAM;
			normUnit =  WEIGHT_NORM_MEASURE;
		}
		
		double newQuant = quantity * unitChangeMod;
		
		converted.setUnit(normUnit);
		converted.setQuantity(newQuant);
		
		return converted;
	}

}
