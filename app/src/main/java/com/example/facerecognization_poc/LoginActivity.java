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

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button train, login, reset;
    private Classifier classifier;
    private static final int TRAIN = 1;
    private static final int LOGIN = 3;
    private static final int FACE_SIZE = 160;
    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        new CoreSharedHelper(this);
        train = findViewById(R.id.train);
        login = findViewById(R.id.login);
        reset = findViewById(R.id.reset);
        train.setOnClickListener(this);
        login.setOnClickListener(this);
        reset.setOnClickListener(this);
        ((TextView)findViewById(R.id.result)).setText("");
        try {
            classifier = Classifier.getInstance(getAssets(), FACE_SIZE, FACE_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.train:
                CoreSharedHelper.getInstance().saveTrainingData("");
                performFileSearch(TRAIN);
                break;

            case R.id.login:
//                takePhoto();
                ((TextView)findViewById(R.id.result)).setText("");
                startActivityForResult( new Intent(this, CameraView.class), LOGIN);
                break;

            case R.id.reset:
                CoreSharedHelper.getInstance().saveTrainingData("");
                ((TextView)findViewById(R.id.result)).setText("");
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == TRAIN) {
            ClipData clipData = data.getClipData();
            if(clipData != null && clipData.getItemCount() > 0) {
                for (int i = 0; i < Objects.requireNonNull(clipData).getItemCount(); i++) {
                    try {
                        classifier.trainModel(getBitmapFromUri(clipData.getItemAt(i).getUri()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else  if (resultCode == RESULT_OK && requestCode == LOGIN) {
            ((TextView)findViewById(R.id.result)).setText(data.getStringExtra("result"));
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
