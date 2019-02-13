package ec.gnp;

public class GnpSubnodeParamsGeneMapKeyParsed {

    private int nodeId;
    private int subnodeId;
    private int parameterId;

    public GnpSubnodeParamsGeneMapKeyParsed(int nodeId, int subnodeId, int parameterId) {
        this.nodeId = nodeId;
        this.subnodeId = subnodeId;
        this.parameterId = parameterId;
    }

    public int getNodeId() {
        return nodeId;
    }

    public int getSubnodeId() {
        return subnodeId;
    }

    public int getParameterId() {
        return parameterId;
    }
}
