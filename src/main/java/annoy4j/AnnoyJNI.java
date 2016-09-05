package annoy4j;

import com.github.fommil.jni.JniLoader;

public class AnnoyJNI {
  static {
    try {
      JniLoader.load("darwin/libannoy.dylib");
    } catch (UnsatisfiedLinkError e) {
      System.err.println("Native code library failed to load.");
    }
  }

  native public static long alloc(int f, int metric);

  native public static void free(long handle);

  native public static void addItem(long handle, int index, float[] vector);

  native public static void build(long handle, int trees);

  native public static boolean save(long handle, String filename);

  native static void unload(long handle);

  native static boolean load(long handle, String filename);

  native public static float getDistance(long handle, int i, int j);

  native public static void getNnsByItem(long handle, int index, int n, int k, int[] result, float[] distances);

  native public static void getNnsByVector(long handle, float[] vector, int n, int k, int[] result, float[] distances);

  native public static int getNItems(long handle);

  native public static void verbose(long handle, boolean v);

  native public static float[] getItem(long handle, int index, int dimension);

}
