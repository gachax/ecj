package ec.gnp;

/**
 * Evaluating the node returns this result, containing all the necessary information for the further process.
 *
 * @author Gatis Birkens
 */
public class GnpNodeEvaluationResult {

    private GnpFunctionResult functionResult;

    private GnpSubnode evaluatedSubnode;

    private Double execTimeQVlue;

    private GnpNode evaluatedNode;

    private Integer evaluationId;

    private Integer branchId;

    public GnpNodeEvaluationResult(GnpFunctionResult functionResult, GnpSubnode evaluatedSubnode, Integer branchId) {

        this.functionResult = functionResult;
        this.evaluatedSubnode = evaluatedSubnode;
        this.execTimeQVlue = evaluatedSubnode.getQ();
        this.branchId = branchId;

    }

    public GnpNodeEvaluationResult(GnpNode evaluatedNode) {
        this.evaluatedNode = evaluatedNode;
    }

    public GnpFunctionResult getFunctionResult() {
        return functionResult;
    }

    public GnpSubnode getEvaluatedSubnode() {
        return evaluatedSubnode;
    }

    public GnpNode getEvaluatedNode() {
        return evaluatedNode;
    }

    public void setEvaluatedNode(GnpNode evaluatedNode) {
        this.evaluatedNode = evaluatedNode;
    }

    public void setEvaluationId(Integer evaluationId) {
        this.evaluationId = evaluationId;
    }

    public Integer getEvaluationId() {
        return evaluationId;
    }

    public Double getExecTimeQVlue() {
        return execTimeQVlue;
    }

    public Integer getBranchId() {
        return branchId;
    }

}
