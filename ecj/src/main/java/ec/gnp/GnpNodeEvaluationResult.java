package ec.gnp;

import java.util.concurrent.atomic.AtomicInteger;

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

    private int evaluationId;

    private int branchId;

    public GnpNodeEvaluationResult(GnpFunctionResult functionResult, GnpSubnode evaluatedSubnode, int branchId) {

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

    public void setEvaluationId(int evaluationId) {
        this.evaluationId = evaluationId;
    }

    public int getEvaluationId() {
        return evaluationId;
    }

    public Double getExecTimeQVlue() {
        return execTimeQVlue;
    }

    public int getBranchId() {
        return branchId;
    }

}
