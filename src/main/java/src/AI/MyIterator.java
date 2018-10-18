package src.AI;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.jetbrains.annotations.NotNull;
import org.nd4j.linalg.api.buffer.FloatBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import src.grid.GridItem;

public class MyIterator extends RecordReaderDataSetIterator {
    private GridItem[][][][] G;
    private int labelIndex;
    final private int drivingIndex = 7;

    MyIterator(RecordReader reader, int size, int labelIndex, int numClasses, GridItem[][][][] G) {
        super(reader, size, labelIndex, numClasses);
        this.G = G;
        this.labelIndex = labelIndex;
    }

    /**
     * Converts {@code items} to floats. Floats can be directly added to a DataSet.
     *
     * @param items GridItem[] to convert
     * @return converted GridItem[] to float[]
     */
    private float[] gridItemsToFloats(@NotNull GridItem[] items) {
        float[] ret = new float[AINN.GRIDSIZE];

        for (int i = 0; (i < items.length && i < AINN.GRIDSIZE); i++) {
            ret[i] = (float) items[i].getSimpleRepr();
        }

        return ret;
    }

    /**
     * Concatenates two float arrays.
     *
     * @param first  the first array
     * @param second the second array
     * @return concatenation of [..x.., ..y..] where x in first and y in second.
     */
    private float[] concatFloatArr(@NotNull float[] first, @NotNull float[] second) {
        float[] ret = new float[first.length + second.length];

        System.arraycopy(first, 0, ret, 0, first.length);
        System.arraycopy(second, 0, ret, first.length, second.length);

        return ret;
    }

    /**
     * Finds and returns next dataset based on CSV file (from {@link CSVRecordReader})
     * and items in the {@link GridItem} array.
     *
     * @return DataSet
     */
    @Override
    public DataSet next() {
        if (super.hasNext()) {
            DataSet dataFromCSV = super.next();
            float[][][] gridData = new float[AINN.GRIDSIZE][AINN.GRIDSIZE][AINN.GRIDSIZE];

            for (int i = 0; (i < G.length && i < AINN.GRIDSIZE); i++) {
                int j = AINN.GRIDSIZE / 2;
                for (int k = 0; (k < G[i][j].length && k < AINN.GRIDSIZE); k++) {
                    gridData[i][k] = gridItemsToFloats(G[i][j][k]);
                }
            }


            // Create array of INDArray to hold correct data for new dataset DS.
            int size = dataFromCSV.numExamples();
            INDArray[] data = new INDArray[size];
            float[] floatData;

            for (int i = 0; i < size; i++) {
                data[i] = Nd4j.zeros(AINN.GRIDSIZE + labelIndex);
                floatData = dataFromCSV.get(i).getFeatures().toFloatVector();

                for (float[][] aGridData : gridData) {
                    for (float[] anAGridData : aGridData) {

                        // Logger.write(Arrays.toString(anAGridData));

                        float[] finalData = concatFloatArr(floatData, anAGridData);
                        FloatBuffer floatBufferForData = new FloatBuffer(finalData);
                        floatBufferForData.setData(finalData);

                        // Logger.write(new Object[]{"floatBufferForData:", floatBufferForData.toString().replaceAll(",", ", ")}, Logger.Type.DEBUG);

                        data[i].setData(floatBufferForData);
                    }
                }
            }

            // Populate new DataSet
            DataSet DS = new DataSet(data[0], dataFromCSV.get(0).getLabels());

            for (int i = 1; i < size; i++) {
                DS.addFeatureVector(data[i]);
            }

            // set LabelNames for completeness sake
            DS.setLabelNames(labelIndex == drivingIndex ? AINN.turnLabelNames : AINN.driveLabelNames);

        /*
        Logger.write(new Object[]{
                "MyIterator.next() gives:",
                DS.getFeatures().length() + "\n\t" + DS.getFeatures().toString().replaceAll("\n", GS.LS),
                DS.getLabels().length() + "\n\t" + DS.getLabels().toString().replaceAll("\n", GS.LS),
        }, Logger.Type.DEBUG);
        */

            // In case size of DS is not equal to number of inputs
            if (DS.getFeatures().length() != 70) {
                // Pad with zeroes.
                int l = ((int) DS.getFeatures().length());
                DS.addFeatureVector(Nd4j.zeros(70 - l));
            }

            return DS;
        }
        return null;
    }

    /**
     * @return {@link RecordReaderDataSetIterator} {@code hasNext()}.
     */
    @Override
    public boolean hasNext() {
        return super.hasNext();
    }
}
