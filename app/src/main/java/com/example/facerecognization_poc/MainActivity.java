package com.example.facerecognization_poc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView image1, image2;
    Button compare;
    private Classifier classifier;
    private static final int FACE_SIZE = 160;
    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    float[] image1array, image2array;

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
        try {
            classifier = Classifier.getInstance(getAssets(), FACE_SIZE, FACE_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.image1:
                performFileSearch(1);
                break;
            case R.id.image2:
                performFileSearch(2);
                break;
            case R.id.compare:
                if(image1array == null || image1array.length == 0 || image2array == null || image2array.length == 0) {
                    Toast.makeText(this, "Select both the images", Toast.LENGTH_LONG).show();
                }
                if (classifier.distance(image1array, image2array) > 0.9)
                    Toast.makeText(this, "Different persons", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this, "Same Person", Toast.LENGTH_LONG).show();

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();

                try {
                Bitmap  bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    if(requestCode == 1) {
                        image1.setImageBitmap(bitmap);
                        image1array = classifier.getDistance( Bitmap.createScaledBitmap(bitmap, WIDTH,HEIGHT, true));
                    }else {
                        image2.setImageBitmap(bitmap);
                        image2array = classifier.getDistance(Bitmap.createScaledBitmap(bitmap, WIDTH,HEIGHT, true));
                    }
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
