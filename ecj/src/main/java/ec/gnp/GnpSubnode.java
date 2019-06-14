package ec.gnp;

import ec.EvolutionState;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.Serializable;

public class GnpSubnode extends GnpNetworkElement implements Serializable {

    public static final String P_SUB_NODE = "subNode";
    private int functionId = -1;
    private double Q = 0.0;
    protected int nodeType;
    protected GnpNode parentNode;
    private ObjectArrayList<GnpSubnodeParameter> subnodeParameters;
    private EvolutionState state;
    private GnpInitializer init;

    public void setup (GnpNode parentNode, int nodeType, int id, double[] genome, int startGene, final EvolutionState state, double Q) {

        this.id = id;
        this.genome = genome;
        this.startGene = startGene;
        this.nodeType = nodeType;
        this.state = state;
        this.init = ((GnpInitializer) state.initializer);
        this.Q = Q;
        this.parentNode = parentNode;

        subnodeParameters = new ObjectArrayList<>();

    }

    public Object copy(GnpNode parentNode, double[] genome) {

        GnpSubnode myobj = init.newGnpSubnodeInstance();

        myobj.setup(parentNode, this.nodeType, this.id, genome, this.startGene, this.state, this.Q);

        for (GnpSubnodeParameter subnodeParameter : subnodeParameters) {
            myobj.subnodeParameters.add((GnpSubnodeParameter) subnodeParameter.copy(genome));
        }

        return myobj;

    }

        public void addSubnodeParameter(GnpSubnodeParameter subnodeParameter) {
        subnodeParameters.add(subnodeParameter);
    }

    public int getFunctionId() {

        if (functionId == -1) {

            if (nodeType == GnpNode.JUDGEMENT_NODE) {
                functionId = (int) genome[startGene];
            }

            if (nodeType == GnpNode.PROCESSING_NODE) {
                functionId = (int) genome[startGene + 1];
            }

        }

        return functionId;

    }

    public ObjectArrayList<GnpSubnodeParameter> getSubnodeParameters() {
        return subnodeParameters;
    }

    public int getId() {
        return id;
    }

    public double getQ() {
        return Q;
    }

    public void setQ(double q) {

        Q = q;
        parentNode.resetMaxQValuedSubnode();

    }

    public int getStartGene() {
        return startGene;
    }

    public void afterSubnodeFunctionChanged() {
        //a hook to be overridden
    }
}
