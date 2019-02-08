package ec.gnp;

import ec.EvolutionState;

import java.io.Serializable;
import java.util.ArrayList;

public class GnpSubnode extends GnpNetworkElement implements Serializable {

    public static final String P_SUB_NODE = "subNode";
    private int functionId = -1;
    private Double Q = 0.0;
    protected int nodeType;
    private ArrayList<GnpSubnodeParameter> subnodeParameters = new ArrayList<>();
    private EvolutionState state;
    private GnpInitializer init;

    public void setup (Integer nodeType, int id, double[] genome, int startGene, final EvolutionState state, Double Q) {

        this.id = id;
        this.genome = genome;
        this.startGene = startGene;
        this.nodeType = nodeType;
        this.state = state;
        this.init = ((GnpInitializer) state.initializer);
        if (Q != null) {
            this.Q = Q;
        }

    }

    public Object copy(double[] genome) {

        GnpSubnode myobj = (GnpSubnode) state.parameters.getInstanceForParameter(init.defaultBase().push(P_SUB_NODE), null, GnpNetworkElement.class);

        myobj.setup(this.nodeType, this.id, genome, this.startGene, this.state, this.Q);

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

    public ArrayList<GnpSubnodeParameter> getSubnodeParameters() {
        return subnodeParameters;
    }

    public int getId() {
        return id;
    }

    public Double getQ() {
        return Q;
    }

    public void setQ(Double q) {
        Q = q;
    }

    public int getStartGene() {
        return startGene;
    }

    public void afterSubnodeFunctionChanged() {
        //a hook to be overridden
    }
}
