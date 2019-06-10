package com.example.facerecognization_poc;

import android.content.Context;
import android.content.SharedPreferences;

public class CoreSharedHelper {
    private static CoreSharedHelper instance;
    private final SharedPreferences sharedPreferences;

    CoreSharedHelper(Context context) {
        instance = this;
        sharedPreferences = context.getSharedPreferences(Constants.NAME, Context.MODE_PRIVATE);
    }

    static CoreSharedHelper getInstance() {
        if (instance == null) {
            throw new NullPointerException("CoreSharedHelper was not initialized!");
        }
        return instance;
    }

    protected void delete(String key) {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        if (sharedPreferences.contains(key)) {
            sharedPreferencesEditor.remove(key).apply();
        }
    }

    protected void savePref(String key, Object value) {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        // delete(key);

        if (value instanceof Boolean) {
            sharedPreferencesEditor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            sharedPreferencesEditor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            sharedPreferencesEditor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            sharedPreferencesEditor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            sharedPreferencesEditor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            sharedPreferencesEditor.putString(key, value.toString());
        } else if (value != null) {
            throw new RuntimeException("Attempting to save non-primitive preference");
        }

        sharedPreferencesEditor.apply();
    }

    void saveTrainingData(String trainingData) {
        savePref(Constants.TRAINING_DATA, trainingData);
    }

    String getTrainingData() {
        return getPref(Constants.TRAINING_DATA, "");
    }

    private <T> T getPref(String key, T defValue) {
        T returnValue = (T) sharedPreferences.getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }

    private class Constants {
        private static final String NAME = "FaceRecognition-Poc";
        private static final String TRAINING_DATA = "trainingData";
    }

}
