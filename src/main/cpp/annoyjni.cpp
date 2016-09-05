#include <stdio.h>
#include "annoyjni.h"
#include "annoylib.h"
#include "kissrandom.h"

/*
 * Class:     annoy4j_AnnoyJNI
 * Method:    alloc
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_annoy4j_AnnoyJNI_alloc
  (JNIEnv *env, jclass cls, jint jf, jint jm) {
  AnnoyIndexInterface<int32_t, float> *ptr = 0;
  if (jm == 1) {
     ptr = new AnnoyIndex<int32_t, float, Angular, Kiss64Random>(jf);
  } else {
     ptr = new AnnoyIndex<int32_t, float, Euclidean, Kiss64Random>(jf);
  }
  return (long) ptr;
}

/*
 * Class:     annoy4j_AnnoyJNI
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_annoy4j_AnnoyJNI_free
  (JNIEnv *env, jclass cls, jlong handle) {
  AnnoyIndexInterface<int32_t, float> *ptr = (AnnoyIndexInterface<int32_t, float> *)handle;
  delete ptr;
}

/*
 * Class:     annoy4j_AnnoyJNI
 * Method:    addItem
 * Signature: (JI[F)V
 */
JNIEXPORT void JNICALL Java_annoy4j_AnnoyJNI_addItem
  (JNIEnv *env, jclass cls, jlong handle, jint jitem, jfloatArray jv) {
  AnnoyIndexInterface<int32_t, float> *ptr = (AnnoyIndexInterface<int32_t, float> *)handle;
  jfloat* v = env->GetFloatArrayElements(jv, 0);
  ptr->add_item(jitem, v);
  env->ReleaseFloatArrayElements(jv, v, 0);
}

/*
 * Class:     annoy4j_AnnoyJNI
 * Method:    build
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_annoy4j_AnnoyJNI_build
  (JNIEnv *env, jclass cls, jlong handle, jint jtrees) {
  AnnoyIndexInterface<int32_t, float> *ptr = (AnnoyIndexInterface<int32_t, float> *)handle;
  ptr->build(jtrees);
}

/*
 * Class:     annoy4j_AnnoyJNI
 * Method:    save
 * Signature: (JLjava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_annoy4j_AnnoyJNI_save
  (JNIEnv *env, jclass cls, jlong handle, jstring jfilename) {
  AnnoyIndexInterface<int32_t, float> *ptr = (AnnoyIndexInterface<int32_t, float> *)handle;
  jboolean r;
  const char *f = env->GetStringUTFChars(jfilename, 0);
  r = ptr->save(f);
  env->ReleaseStringUTFChars(jfilename, f);
  return r;
}

/*
 * Class:     annoy4j_AnnoyJNI
 * Method:    unload
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_annoy4j_AnnoyJNI_unload
  (JNIEnv *env, jclass cls, jlong handle) {
  AnnoyIndexInterface<int32_t, float> *ptr = (AnnoyIndexInterface<int32_t, float> *)handle;
  ptr->unload();
}

/*
 * Class:     annoy4j_AnnoyJNI
 * Method:    load
 * Signature: (JLjava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_annoy4j_AnnoyJNI_load
  (JNIEnv *env, jclass cls, jlong handle, jstring jfilename) {
  AnnoyIndexInterface<int32_t, float> *ptr = (AnnoyIndexInterface<int32_t, float> *)handle;
  jboolean r;
  const char *f = env->GetStringUTFChars(jfilename, 0);
  r = ptr->load(f);
  env->ReleaseStringUTFChars(jfilename, f);
  return r;
}

/*
 * Class:     annoy4j_AnnoyJNI
 * Method:    getDistance
 * Signature: (JII)F
 */
JNIEXPORT jfloat JNICALL Java_annoy4j_AnnoyJNI_getDistance
  (JNIEnv *env, jclass cls, jlong handle, jint ji, jint jj) {
  AnnoyIndexInterface<int32_t, float> *ptr = (AnnoyIndexInterface<int32_t, float> *)handle;
  return ptr->get_distance(ji, jj);
}

/*
 * Class:     annoy4j_AnnoyJNI
 * Method:    getNnsByItem
 * Signature: (JIII[I[F)V
 */
JNIEXPORT void JNICALL Java_annoy4j_AnnoyJNI_getNnsByItem
  (JNIEnv *env, jclass cls, jlong handle, jint jitem, jint jn, jint jk, jintArray jresult, jfloatArray jdistances) {
  AnnoyIndexInterface<int32_t, float> *ptr = (AnnoyIndexInterface<int32_t, float> *)handle;
  vector<int32_t> result;
  vector<float> distances;
  ptr->get_nns_by_item(jitem, jn, jk, &result, &distances);
  env->SetIntArrayRegion(jresult, 0, result.size(), &result[0]);
  env->SetFloatArrayRegion(jdistances, 0, distances.size(), &distances[0]);
}

/*
 * Class:     annoy4j_AnnoyJNI
 * Method:    getNnsByVector
 * Signature: (J[FII[I[F)V
 */
JNIEXPORT void JNICALL Java_annoy4j_AnnoyJNI_getNnsByVector
  (JNIEnv *env, jclass cls, jlong handle, jfloatArray jv, jint jn, jint jk, jintArray jresult, jfloatArray jdistances) {
  AnnoyIndexInterface<int32_t, float> *ptr = (AnnoyIndexInterface<int32_t, float> *)handle;
  jfloat* v = env->GetFloatArrayElements(jv, 0);
  vector<int32_t> result;
  vector<float> distances;
  ptr->get_nns_by_vector(v, jn, jk, &result, &distances);
  env->ReleaseFloatArrayElements(jv, v, 0);
  env->SetIntArrayRegion(jresult, 0, result.size(), &result[0]);
  env->SetFloatArrayRegion(jdistances, 0, distances.size(), &distances[0]);
}

/*
 * Class:     annoy4j_AnnoyJNI
 * Method:    getNItems
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_annoy4j_AnnoyJNI_getNItems
  (JNIEnv *env, jclass cls, jlong handle) {
  AnnoyIndexInterface<int32_t, float> *ptr = (AnnoyIndexInterface<int32_t, float> *)handle;
  return (jint)ptr->get_n_items();
}

/*
 * Class:     annoy4j_AnnoyJNI
 * Method:    verbose
 * Signature: (JZ)V
 */
JNIEXPORT void JNICALL Java_annoy4j_AnnoyJNI_verbose
  (JNIEnv *env, jclass cls, jlong handle, jboolean v) {
  AnnoyIndexInterface<int32_t, float> *ptr = (AnnoyIndexInterface<int32_t, float> *)handle;
  ptr->verbose(v);
}

/*
 * Class:     annoy4j_AnnoyJNI
 * Method:    getItem
 * Signature: (JII)[F
 */
JNIEXPORT jfloatArray JNICALL Java_annoy4j_AnnoyJNI_getItem
  (JNIEnv *env, jclass cls, jlong handle, jint jitem, jint dimension) {
  AnnoyIndexInterface<int32_t, float> *ptr = (AnnoyIndexInterface<int32_t, float> *)handle;
  vector<float> v(dimension);
  ptr->get_item(jitem, &v[0]);
  jfloatArray jfarray = env->NewFloatArray(v.size());
  env->SetFloatArrayRegion(jfarray, 0, v.size(), &v[0]);
  return jfarray;
}