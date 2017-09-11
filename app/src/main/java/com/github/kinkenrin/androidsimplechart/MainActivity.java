package com.github.kinkenrin.androidsimplechart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt_pieView).setOnClickListener(this);
        findViewById(R.id.bt_horizontalBarView).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.bt_horizontalBarView:
                intent = new Intent(getApplicationContext(), HorizontalBarActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_pieView:
                intent = new Intent(getApplicationContext(), PieActivity.class);
                startActivity(intent);
                break;
        }
    }
}
