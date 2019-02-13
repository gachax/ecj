package ec.gnp.implementations;

import ec.gnp.GnpInitializer;
import ec.gnp.GnpSubnodeParameter;
import ec.gnp.GnpGeneDescriptor;

import java.util.*;

public class GnpTwoIntegerGenesSubnodeParameter extends GnpSubnodeParameter {

    private GnpGeneDescriptor intGene1;
    private GnpGeneDescriptor intGene2;

    public int getI1() {
        return getIntGeneValue(intGene1);
    }

    public int getI2() {
        return getIntGeneValue(intGene2);
    }

    public Object copy(double[] genome) {
        GnpTwoIntegerGenesSubnodeParameter newObj = new GnpTwoIntegerGenesSubnodeParameter();
        newObj.setup(id, genome, startGene, state);
        return newObj;
    }

    @Override
    public void setupGenes() {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("mutation-type", "integer-reset");
        parameters.put("mutation-prob", "0.2");
        parameters.put(GnpInitializer.P_MIN_GENE, "-1");
        parameters.put(GnpInitializer.P_MAX_GENE, "100");
        intGene1 = new GnpGeneDescriptor(int.class, parameters);
        addGene(intGene1);
        intGene2 = new GnpGeneDescriptor(int.class, parameters);
        addGene(intGene2);

    }
}
