package viralPopGen.beast;

import java.util.*;
import beast.core.*;

/**
 * Beast 2 plugin representing a single reaction.
 * 
 * @author Tim Vaughan
 *
 */
@Description("Component reaction of a birth-death model.")
public class Reaction extends Plugin {
	
	public Input<Schema> reactantSchemaInput = new Input<Schema>("reactantSchema", "Reactant schema.");
	public Input<Schema> productSchemaInput = new Input<Schema>("productSchema", "Product schema.");
	
	public Input<List<Double>> rateInput = new Input<List<Double>>("rate",
			"Reaction rate.",
			new ArrayList<Double>());
	
	// True reaction object:
	viralPopGen.Reaction reaction;
	
	public Reaction() {};
	
	@Override
	public void initAndValidate() throws Exception {

		reaction = new viralPopGen.Reaction();

		reaction.setReactantSchema(reactantSchemaInput.get().popSchema);
		reaction.setProductSchema(productSchemaInput.get().popSchema);

		for (int[][] subPopSchema : reactantSchemaInput.get().subPopSchemas)
			reaction.addReactantSubSchema(subPopSchema);
		
		for (int[][] subPopSchema : productSchemaInput.get().subPopSchemas)
			reaction.addProductSubSchema(subPopSchema);
			
		double[] rates = new double[rateInput.get().size()];
		for (int i=0; i<rates.length; i++)
			rates[i] = rateInput.get().get(i);
		reaction.setRate(rates);
	}

}