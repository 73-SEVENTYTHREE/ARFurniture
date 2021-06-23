package com.google.ar.sceneform.samples.ARFurniture;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GoodInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_info);
        //浸入ar展示界面
        Button arView = (Button) findViewById(R.id.arView);
        arView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoodInfoActivity.this, GltfActivity.class);
                startActivity(intent);
            }
        });
    }
}