package ec.gnp;

import java.io.Serializable;

/**
 * The network branch/path which links GnpNodes of the Genetic Network/graph.
 * It's part of the GnpNode and links specific GnpSubnode of the GnpNode to another GnpNode in the network.
 *
 * @author Gatis Birkens
 */
public class GnpBranch extends GnpNetworkElement implements Serializable {

    private int connectedNodeId = -1;
    private GnpSubnode subnode;

    GnpBranch(int id, double[] genome, int startGene) {

        this.id = id;
        this.genome = genome;
        this.startGene = startGene;

    }

    public Object copy(double[] genome) {

        //Subnodes are created in GnpNode

        return new GnpBranch(this.id, genome, this.startGene);

    }

    /**
     * Connection to the nodes is a part of the genome as an integer gene which can be mutated.
     * Setting the connected node from the genome to the local variable. It should never change because in case of network mutation, the network re-generated.
     * @return id of the GnpNode from the network
     */
    public int getConnectedNodeId() {

        if (connectedNodeId == -1){
            connectedNodeId = (int) genome[startGene];
        }
        return connectedNodeId;

    }

    /**
     * Used when generating GnpNetwork
     * @param connectedNodeId the node id of the node linked
     */
    public void setConnectedNodeId(int connectedNodeId) {
        genome[startGene] = connectedNodeId;
        this.connectedNodeId = connectedNodeId;
    }

    public int getId() {
        return id;
    }

    public GnpSubnode getSubnode() {
        return subnode;
    }

    public void setSubnode(GnpSubnode subnode) {
        this.subnode = subnode;
    }
}
