package ec.gnp.implementations;

import ec.gnp.GnpInitializer;
import ec.gnp.GnpSubnodeParameter;
import ec.gnp.GnpGeneDescriptor;

import java.util.*;

public class GnpIntegerAndDoubleGenesSubnodeParameter extends GnpSubnodeParameter {

    private GnpGeneDescriptor intGene;
    private GnpGeneDescriptor doubleGene;

    public int getI1() {
        return  getIntGeneValue(intGene);
    }

    public double getD2() {
        return getDoubleGeneValue(doubleGene);
    }

    public Object copy(double[] genome) {
        GnpIntegerAndDoubleGenesSubnodeParameter newObj = new GnpIntegerAndDoubleGenesSubnodeParameter();
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
        intGene = new GnpGeneDescriptor(int.class, parameters);
        addGene(intGene);

        parameters = new HashMap<>();
        parameters.put("mutation-type", "gauss");
        parameters.put("mutation-stdev", "0.1");
        parameters.put("out-of-bounds-retries", "20");
        parameters.put("mutation-bounded", "false");
        parameters.put(GnpInitializer.P_MIN_GENE, "0.0");
        parameters.put(GnpInitializer.P_MAX_GENE, "50.5");
        doubleGene = new GnpGeneDescriptor(double.class, parameters);
        addGene(doubleGene);
    }
}
