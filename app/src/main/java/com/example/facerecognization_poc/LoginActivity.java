package com.example.facerecognization_poc;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.facerecognization_poc.ProjectConstants.HEIGHT;
import static com.example.facerecognization_poc.ProjectConstants.WIDTH;
import static com.example.facerecognization_poc.ProjectConstants.classifier;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button train, login;
    private static final int TRAIN = 1;
    private static final int LOGIN = 3;
    private ArrayList<TrainingData> trainingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        new CoreSharedHelper(this);
        resetData();
        train = findViewById(R.id.train);
        login = findViewById(R.id.login);
        train.setOnClickListener(this);
        login.setOnClickListener(this);

    }

    private void resetData() {
        trainingData = new Gson().fromJson(CoreSharedHelper.getInstance().getTrainingData(), new TypeToken<List<TrainingData>>() {
        }.getType());

        findViewById(R.id.instruction).setVisibility((trainingData == null || trainingData.size() == 0) ? View.VISIBLE : View.GONE);

    }

    private void clearTrainingDataConformation() {
        new AlertDialog.Builder(this)
                .setMessage("Your previous trained data will be cleared. \n Are you sure you want to clear it?")
                .setPositiveButton("Yes", (arg0, arg1) -> {
                    arg0.dismiss();
                    CoreSharedHelper.getInstance().saveTrainingData("");
                    performFileSearch(TRAIN);
                })
                .setNegativeButton("No", (arg0, arg1) -> arg0.dismiss())
                .show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.train:
                clearTrainingDataConformation();
                break;

            case R.id.login:
//                takePhoto();
                ((TextView) findViewById(R.id.result)).setText("");
                if (trainingData == null || trainingData.size() == 0)
                    Toast.makeText(this, "please provide training data", Toast.LENGTH_SHORT).show();
                else {
                    ((TextView) findViewById(R.id.result)).setText("");
                    startActivityForResult(new Intent(this, CameraView.class), LOGIN);
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == TRAIN) {
            ClipData clipData = data.getClipData();
            if (clipData != null && clipData.getItemCount() > 0) {
                for (int i = 0; i < Objects.requireNonNull(clipData).getItemCount(); i++) {
                    try {
                        classifier.trainModel(getBitmapFromUri(clipData.getItemAt(i).getUri()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                resetData();
            }
        } else if (resultCode == RESULT_OK && requestCode == LOGIN) {
            ((TextView) findViewById(R.id.result)).setText(data.getStringExtra("result"));
        }

//        else if (resultCode == RESULT_OK && requestCode == 2) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(extras).get("data");
//            if (classifier.recognizeImage(Bitmap.createScaledBitmap(Objects.requireNonNull(imageBitmap), WIDTH, HEIGHT, true)))
//                Toast.makeText(this, "match", Toast.LENGTH_LONG).show();
//            else
//                Toast.makeText(this, "Not a match", Toast.LENGTH_LONG).show();
//
//        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Bitmap.createScaledBitmap(Objects.requireNonNull(bitmap), WIDTH, HEIGHT, true);
    }


//    public void takePhoto() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, 2);
//    }


    public void performFileSearch(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }
}
