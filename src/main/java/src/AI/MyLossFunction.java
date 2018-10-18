package src.AI;

import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import org.nd4j.linalg.primitives.Pair;

public class MyLossFunction implements ILossFunction {

    @Override
    public double computeScore(INDArray indArray, INDArray indArray1, IActivation iActivation, INDArray indArray2, boolean b) {
        return 0;
    }

    @Override
    public INDArray computeScoreArray(INDArray indArray, INDArray indArray1, IActivation iActivation, INDArray indArray2) {
        return null;
    }

    @Override
    public INDArray computeGradient(INDArray indArray, INDArray indArray1, IActivation iActivation, INDArray indArray2) {
        return null;
    }

    @Override
    public Pair<Double, INDArray> computeGradientAndScore(INDArray indArray, INDArray indArray1, IActivation iActivation, INDArray indArray2, boolean b) {
        return null;
    }

    @Override
    public String name() {
        return null;
    }
}
