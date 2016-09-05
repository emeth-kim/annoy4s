package annoy4j;

public class Annoy {

  private long handle = 0L;
  private Metric metric = Metric.Angular;
  private int dimension = 0;

  public Annoy(int dimension, Metric metric) {
    this.dimension = dimension;
    this.metric = metric;
    if (metric == Metric.Angular) {
      handle = AnnoyJNI.alloc(dimension, 1);
    } else if (metric == Metric.Euclidean) {
      handle = AnnoyJNI.alloc(dimension, 2);
    }
  }

  public void addItem(int index, float[] vector) {
    assert vector.length == dimension;
    AnnoyJNI.addItem(handle, index, vector);
  }

  public void build(int trees) {
    AnnoyJNI.build(handle, trees);
  }

  public boolean save(String filename) {
    return AnnoyJNI.save(handle, filename);
  }

  public void unload() {
    AnnoyJNI.unload(handle);
  }

  public boolean load(String filename) {
    return AnnoyJNI.load(handle, filename);
  }

  public float getDistance(int i, int j) {
    return AnnoyJNI.getDistance(handle, i, j);
  }

  public AnnoyResult getNnsByItem(int index, int n, int k) {
    AnnoyResult r = new AnnoyResult(n);
    AnnoyJNI.getNnsByItem(handle, index, n, k, r.result, r.distances);
    return r;
  }

  public AnnoyResult getNnsByItem(int index, int n) {
    return getNnsByItem(index, n, -1);
  }

  public AnnoyResult getNnsByVector(float[] vector, int n, int k) {
    AnnoyResult r = new AnnoyResult(n);
    AnnoyJNI.getNnsByVector(handle, vector, n, k, r.result, r.distances);
    return r;
  }

  public AnnoyResult getNnsByVector(float[] vector, int n) {
    return getNnsByVector(vector, n, -1);
  }

  public int getNItems() {
    return AnnoyJNI.getNItems(handle);
  }

  public void verbose(boolean v) {
    AnnoyJNI.verbose(handle, v);
  }

  public float[] getItem(int index) {
    return AnnoyJNI.getItem(handle, index, dimension);
  }

  public void close() {
    if (handle != 0L)
      AnnoyJNI.free(handle);
  }

}
