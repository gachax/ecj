package ec.gnp;

import ec.EvolutionState;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A genome based parameter for the GnpSubnode. This means parameters undergo the evolution process of mutations etc. and are passed to the GnpFunciton of the GnpSubnode being evaluated.
 * The number of parameters per subnode is set by the gnp.subnodeParameters.count property.
 *
 * @author Gatis Birkens
 */
public abstract class GnpSubnodeParameter extends GnpNetworkElement implements Serializable {

    protected EvolutionState state;
    private double[] localGenome; //reducing performance cost of going through the whole genome of the individual
    Map<GnpGeneDescriptor, Integer> genes = new LinkedHashMap<>();//parameter specific gene configuration


    /**
     * Called after initialization, setupGenes is called in order to register custom genes. Local genome is set as an extract from the genes of the GnpIndividual.
     * @param id ID of the parameter (unique pe the subnode - an index in the list of GnpSubnode subnodeParameters)
     * @param genome the genome of the GnpIndividual
     * @param startGene the gene index in the genome of the individual at which the parameter's genes start
     * @param state EvolutionState
     */
    public void setup(int id, double[] genome, int startGene, EvolutionState state) {

        this.id = id;
        this.genome = genome;
        this.startGene = startGene;
        this.state = state;

        setupGenes();

        setupLocalGenome();

    }

    /**
     * Creates local genome as an extract from the genes of the GnpIndividual. Values should never change because in case of network mutation, it's re-generated.
     */
    private void setupLocalGenome() {

        localGenome = new double[genes.size()];

        for (int i = 0; i < genes.size(); i++){
            localGenome[i] = genome[startGene + i];
        }

    }

    public abstract Object copy(double[] genome);

    /**
     * GnpIndividual is based on DoubleVectorIndividual, so it supports the same mutations and boundries according to the type of the gene (int or double).
     * GnpGeneDescriptor describes each gene and each gene must be registered within the implementation of this method by using addGene(GnpGeneDescriptor gnpGeneDescriptor).
     */
    public abstract void setupGenes();

    /**
     * @param gnpGeneDescriptor GnpGeneDescriptor instance of the gene registered in setupGenes() by addGene(GnpGeneDescriptor gnpGeneDescriptor)
     * @return the value of the int based gene
     */
    protected int getIntGeneValue(GnpGeneDescriptor gnpGeneDescriptor) {
        return (int) localGenome[genes.get(gnpGeneDescriptor)];
    }

    /**
     *
     * @param gnpGeneDescriptor GnpGeneDescriptor instance of the gene registered in setupGenes() by addGene(GnpGeneDescriptor gnpGeneDescriptor)
     * @return the value of the double based gene
     */
    protected double getDoubleGeneValue(GnpGeneDescriptor gnpGeneDescriptor) {
        return localGenome[genes.get(gnpGeneDescriptor)];
    }

    private void setValue(double value, int geneIndex) {

        localGenome[geneIndex] = value;
        genome[startGene + geneIndex] = value;

    }

    protected void setDoubleGeneValue(GnpGeneDescriptor gnpGeneDescriptor, double value) {
        setValue(value, genes.get(gnpGeneDescriptor));
    }

    protected void setIntGeneValue(GnpGeneDescriptor gnpGeneDescriptor, int value) {
        setValue(value, genes.get(gnpGeneDescriptor));
    }

    protected void addGene(GnpGeneDescriptor gnpGeneDescriptor){
        genes.put(gnpGeneDescriptor, genes.size());
    }

    public Map<GnpGeneDescriptor, Integer> getGenes() {
        return genes;
    }

    public EvolutionState getState() {
        return state;
    }

    public void setState(EvolutionState state) {
        this.state = state;
    }

}
