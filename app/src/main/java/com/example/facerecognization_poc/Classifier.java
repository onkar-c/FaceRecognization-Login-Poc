/* Copyright 2015 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.example.facerecognization_poc;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import androidx.core.util.Pair;

import com.example.facerecognization_poc.wrapper.FaceNet;
import com.example.facerecognization_poc.wrapper.MTCNN;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Classifier {

    public static final int EMBEDDING_SIZE = 512;
    private static Classifier classifier;

    private MTCNN mtcnn;
    private FaceNet faceNet;

    private Classifier() {
    }

    static Classifier getInstance(AssetManager assetManager,
                                  int inputHeight,
                                  int inputWidth) {
        if (classifier != null) return classifier;

        classifier = new Classifier();

        classifier.mtcnn = MTCNN.create(assetManager);
        classifier.faceNet = FaceNet.create(assetManager, inputHeight, inputWidth);

        return classifier;
    }

    /**
     * Used to check if the given image matches the trained data.
     * input image vectors are obtained and distance between that vectors and trained vectors are checked.
     * if threshold value < 0.9 for more than 50% of training data then it is a match or else not a match
     * @param bitmap input image to be checked
     * @return match or not a match
     */
    boolean recognizeImage(Bitmap bitmap) {
        synchronized (this) {

                Rect rect = getRect(bitmap);
                TrainingData td = new TrainingData();
                td.emb_array = new float[EMBEDDING_SIZE];
                faceNet.getEmbeddings(bitmap, rect).get(td.emb_array);
                int count = 0;
                ArrayList<TrainingData> trainingData = new Gson().fromJson(CoreSharedHelper.getInstance().getTrainingData(), new TypeToken<List<TrainingData>>() {
                }.getType());
                if(trainingData != null && trainingData.size() > 0) {
                    for (TrainingData t : trainingData) {
                        if (distance(t.emb_array, td.emb_array) < 0.9) count++;
                    }
                    if (count >= trainingData.size() / 2)
                        return true;
                } else
                    return false;
        }
        return false;
    }


    /**
     * Used to train the model for face login
     * @param bitmap input image
     */
    void trainModel(Bitmap bitmap) {
        Rect rect = getRect(bitmap);
        TrainingData td = new TrainingData();
        td.emb_array = new float[EMBEDDING_SIZE];
        faceNet.getEmbeddings(bitmap, rect).get(td.emb_array);
        ArrayList<TrainingData> trainingData = new Gson().fromJson(CoreSharedHelper.getInstance().getTrainingData(), new TypeToken<List<TrainingData>>() {
        }.getType());
        if (trainingData == null)
            trainingData = new ArrayList<>();
        trainingData.add(td);

        CoreSharedHelper.getInstance().saveTrainingData(new Gson().toJson(trainingData));
    }

    /**
     * calculate 512 vectors of the face in given image
     * @param bitmap input image
     * @return 512 vectors
     */
    float[] getDistance(Bitmap bitmap) {
        Rect rect = getRect(bitmap);
        float[] emb_array = new float[EMBEDDING_SIZE];
        faceNet.getEmbeddings(bitmap, rect).get(emb_array);
        return emb_array;
    }

    /**
     * This function detects the face from image and returns the rectangular box.
     * @param bitmap input image
     * @return Rectangular box
     */
    private Rect getRect(Bitmap bitmap){
        Pair[] faces = mtcnn.detect(bitmap);
        Rect rect = new Rect();
        float max = 0f;
        for (Pair face : faces) {
            Float prob = (Float) face.second;
            if (prob != null && prob > max) {
                max = prob;
                RectF rectF = (RectF) face.first;
                Objects.requireNonNull(rectF).round(rect);
            }
        }
        return rect;
    }

    /**
     * calculate the distance between all the vectors and return threshold value for the same
     * @param vectorsArray1 512 vectors of image 1
     * @param vectorsArray2 512 vectors of image 2
     * @return returns the threshold value between 2 vector arrays.
     */
    float distance(float[] vectorsArray1, float[] vectorsArray2) {
        float[] sub = new float[512];
        float threshold = 0;
        for (int i = 0; i < vectorsArray1.length; i++) {
            sub[i] = vectorsArray1[i] - vectorsArray2[i];
            sub[i] *= sub[i];
            threshold += sub[i];
        }
        Log.d("distance", "" + threshold);
        return threshold;
    }
}
