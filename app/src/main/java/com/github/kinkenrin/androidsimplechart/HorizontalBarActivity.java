package com.github.kinkenrin.androidsimplechart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class HorizontalBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_bar);
        HorizontalBarView hbv_bar = (HorizontalBarView) findViewById(R.id.hbv_bar);
        List<HorizontalBarView.BarEntry> datas = new ArrayList<>();
        datas.add(new HorizontalBarView.BarEntry("技能1", 10));
        datas.add(new HorizontalBarView.BarEntry("技能2", 16));
        datas.add(new HorizontalBarView.BarEntry("技能3", 8));
        datas.add(new HorizontalBarView.BarEntry("技能4", 12));
        datas.add(new HorizontalBarView.BarEntry("技能5", 15));
        datas.add(new HorizontalBarView.BarEntry("技能6", 3));
        datas.add(new HorizontalBarView.BarEntry("技能7", 22));
        datas.add(new HorizontalBarView.BarEntry("技能8", 0));
        hbv_bar.setDatas(datas);
    }
}
