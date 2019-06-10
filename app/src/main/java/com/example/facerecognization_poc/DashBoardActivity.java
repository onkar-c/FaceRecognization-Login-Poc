package com.example.facerecognization_poc;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DashBoardActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
         findViewById(R.id.faceComparision).setOnClickListener(this);
        findViewById(R.id.faceLogin).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
       exitByBackKey();
    }

    private void exitByBackKey() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to exit application?")
                .setPositiveButton("Yes", (arg0, arg1) -> {
                    arg0.dismiss();
                    super.onBackPressed();
                })
                .setNegativeButton("No", (arg0, arg1) -> arg0.dismiss())
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.faceComparision:
                startActivity(new Intent(DashBoardActivity.this,MainActivity.class));
                break;
            case R.id.faceLogin:
                startActivity(new Intent(DashBoardActivity.this,LoginActivity.class));
                break;
        }

    }
}
