package com.github.kinkenrin.androidsimplechart;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class PieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie);

        PieChartView pcv_view = (PieChartView) findViewById(R.id.pcv_view);
        List<PieChartView.PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieChartView.PieEntry("技能1", 10, Color.rgb(0, 209, 209)));
        pieEntries.add(new PieChartView.PieEntry("技能2", 16, Color.rgb(255, 179, 104)));
        pcv_view.setPieData(pieEntries);
    }
}
