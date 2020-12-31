package ec.gnp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to describe the parameters of the gene of GnpSubnodeParameter.
 * GnpIndividual is based on DoubleVectorIndividual, so GnpGeneDescriptor supports the same mutations and boundries according to the type of the gene (int or double).
 *
 * @author Gatis Birkens
 */
public class GnpGeneDescriptor implements Serializable {

    //private int id;//set and used by GnpSubnodeParameter to make it faster than by using HashMap
    private Class basicType;
    //mutation, crosover etc parameters
    private Map<String, String> parameters;

    /**
     * Constructor
     * @param basicType class of the int or double (int.class, double.class)
     * @param additionalParameters - contain the parameters for the gene according to the possibilities of the DoubleVectorIndividual, for example
     *                              Map<String, String> additionalParameters = new HashMap<>();
     *                              additionalParameters.put("mutation-type", "integer-reset");
     *                              additionalParameters.put("mutation-prob", "0.2");
     *                              additionalParameters.put("min-gene", "-1");
     *                              additionalParameters.put("max-gene", "100");
     *
     */
    public GnpGeneDescriptor(Class basicType, Map<String, String> additionalParameters) {

        this.basicType = basicType;
        this.parameters = additionalParameters;

    }

    public boolean equal(GnpGeneDescriptor dsc){

        return dsc != null
                && basicType.equals(dsc.getBasicType())
                && parameters.equals(dsc.parameters)
                //&& id == dsc.id
                ;

    }

    /*public GnpGeneDescriptor copy() {

        GnpGeneDescriptor newObj = null;
        try {
            newObj = (GnpGeneDescriptor) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        newObj.basicType = basicType;
        newObj.id = id;
        newObj.parameters = new HashMap<>();

        for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
            newObj.parameters.put(entry.getKey(), entry.getValue());
        }

        return newObj;
    }*/

    private Class getBasicType() {
        return basicType;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    /*public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }*/
}
