package ec.gnp;

/**
 * Element in the Genetic Network, for example GnpNode and GnpSubnode.
 * Contains the identifier, the genome of GnpIndividual and the index of the gene of the GnpIndividual at which this network element starts.
 *
 * @author Gatis Birkens
 */
public class GnpNetworkElement {

    protected Integer id;
    protected int startGene;
    protected double[] genome;

}
