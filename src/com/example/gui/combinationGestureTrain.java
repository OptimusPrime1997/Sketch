package com.example.gui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import weka.classifiers.Classifier;
import weka.classifiers.meta.MultiClassClassifier;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

public class combinationGestureTrain {
	private Classifier method = null;

	public void Train(Context c) throws Exception {

		method = new MultiClassClassifier();
		InputStream in = c.getAssets().open("combinationGesture.arff");

		ArffLoader arffLoader1 = new ArffLoader();
		arffLoader1.setSource(in);
		Instances instanceTrain = arffLoader1.getDataSet();
		instanceTrain.setClassIndex(instanceTrain.numAttributes() - 1);
		method.buildClassifier(instanceTrain);

	}

	@SuppressLint("SdCardPath")
	public double[] TrainResult(String path) {
		double[] result = new double[50];
		for (int i = 0; i < result.length; i++) {
			result[i] = -1;
		}
		File tranFile = new File(path);
		ArffLoader arffLoader2 = new ArffLoader();
		try {
			arffLoader2.setFile(tranFile);
			Instances instanceTest = arffLoader2.getDataSet();
			instanceTest.setClassIndex(instanceTest.numAttributes() - 1);
			int sum = instanceTest.numInstances();
			for (int i = 0; i < sum; i++) {
				result[i] = (method.classifyInstance(instanceTest.instance(i)));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.w("length", result.length + "");
		return result;
	}

}
