package annoy4j;

public class AnnoyResult {
    int[] result = null;
    float[] distances = null;

    public int[] getResult() {
        return result;
    }
    public float[]  getDistances() {
        return distances;
    }

    public AnnoyResult(int n) {
        result = new int[n];
        distances = new float[n];
    }
}
