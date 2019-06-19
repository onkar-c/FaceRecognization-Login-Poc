package com.example.facerecognization_poc;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.facerecognization_poc.ProjectConstants.GET_ANOTHER_FILE_FROM_STORAGE;
import static com.example.facerecognization_poc.ProjectConstants.GET_FILE_FROM_STORAGE;
import static com.example.facerecognization_poc.ProjectConstants.HEIGHT;
import static com.example.facerecognization_poc.ProjectConstants.WIDTH;
import static com.example.facerecognization_poc.ProjectConstants.classifier;


public class FaceComparision extends AppCompatActivity implements View.OnClickListener {
    ImageView image1, image2;
    Button compare;


    float[] image1array, image2array;
    Bitmap image1Bitmap, image2Bitmap;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        compare = findViewById(R.id.compare);
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        compare.setOnClickListener(this);
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image1:
                performFileSearch(GET_FILE_FROM_STORAGE);
                break;
            case R.id.image2:
                performFileSearch(GET_ANOTHER_FILE_FROM_STORAGE);
                break;
            case R.id.compare:
                new LongOperation().execute("");
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (image1array == null || image1array.length == 0 || image2array == null || image2array.length == 0) {
                return "Select both the images";
            }
            if (classifier.distance(image1array, image2array) > 0.9)
                return "Different persons";
            else
                return "Same Person";
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing())
                dialog.dismiss();
            Toast.makeText(FaceComparision.this, result, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {
            if (!dialog.isShowing())
                dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class CalculateVector extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            if (params[0] == GET_FILE_FROM_STORAGE)
                image1array = classifier.getDistance(Bitmap.createScaledBitmap(image1Bitmap, WIDTH, HEIGHT, true));
            else
                image2array = classifier.getDistance(Bitmap.createScaledBitmap(image2Bitmap, WIDTH, HEIGHT, true));
            return params[0];
        }

        @Override
        protected void onPostExecute(Integer imageNo) {
            if (dialog.isShowing())
                dialog.dismiss();
            if (imageNo == GET_FILE_FROM_STORAGE) {
                image1.setImageBitmap(image1Bitmap);
            } else
                image2.setImageBitmap(image2Bitmap);
        }

        @Override
        protected void onPreExecute() {
            if (!dialog.isShowing())
                dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                if (requestCode == GET_FILE_FROM_STORAGE)
                    image1Bitmap = bitmap;
                else
                    image2Bitmap = bitmap;
                new CalculateVector().execute(requestCode);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void performFileSearch(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }
}
