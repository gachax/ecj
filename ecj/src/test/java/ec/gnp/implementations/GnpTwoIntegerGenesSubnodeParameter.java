package ec.gnp.implementations;

import ec.gnp.GnpInitializer;
import ec.gnp.GnpSubnodeParameter;
import ec.gnp.GnpGeneDescriptor;

import java.util.*;

public class GnpTwoIntegerGenesSubnodeParameter extends GnpSubnodeParameter {

    private GnpGeneDescriptor intGene1;
    private GnpGeneDescriptor intGene2;

    public GnpTwoIntegerGenesSubnodeParameter() {
    }

    public GnpTwoIntegerGenesSubnodeParameter(GnpGeneDescriptor intGene1, GnpGeneDescriptor intGene2) {
        this.intGene1 = intGene1;
        this.intGene2 = intGene2;
    }

    public int getI1() {
        return getIntGeneValue(intGene1);
    }

    public int getI2() {
        return getIntGeneValue(intGene2);
    }

    @Override
    public GnpSubnodeParameter newInstance() {
        return new GnpTwoIntegerGenesSubnodeParameter(intGene1, intGene2);
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
