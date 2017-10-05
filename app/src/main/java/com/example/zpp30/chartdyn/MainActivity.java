package com.example.zpp30.chartdyn;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    EditText editY;
    Button btnAddData;
    Button btnviewAll;
    Button btnDelete;
    SoundPool mySound;
    int raygunID;
    MediaPlayer mp;

//    public LineData data = mChart.getData();

    private LineChart mChart;
    private void playmp(float a) {
        float volume = ((a / (mChart.getYChartMax() - mChart.getYChartMin())));
        mySound.play(raygunID, 1, 1, 1, 0, volume);

    }
    private LineData data;

    private ArrayList<Entry> entries = new ArrayList<Entry>();

    public LineData getData(){
        return this.data;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        editY = (EditText)findViewById(R.id.editText_Y);
        btnAddData = (Button)findViewById(R.id.button_add);
        btnviewAll = (Button)findViewById(R.id.button);
        btnDelete= (Button)findViewById(R.id.button_delete);
        mySound = new SoundPool(6, AudioManager.STREAM_NOTIFICATION, 0);
        raygunID = mySound.load(this, R.raw.p1, 1);

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mChart.setDescription("");

        // add an empty data object
        mChart.setData(new LineData());
        mChart.setScaleEnabled(false);

//        mChart.getXAxis().setDrawLabels(false);
//        mChart.getXAxis().setDrawGridLines(false);

        mChart.invalidate();



        btnAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEntry();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeLastEntry();
            }
        });
        mp = MediaPlayer.create(this, R.raw.p1);
        btnviewAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if(data != null){
                    entries.clear();
                    for(int i=0;i<data.getDataSetByIndex(0).getEntryCount();i++){
                        entries.add(data.getDataSetByIndex(0).getEntryForIndex(i));
                    }

                }
                // TODO Auto-generated method stub
                final Timer timer = new Timer();

                // Body Of Timer
                TimerTask time = new TimerTask() {

                    private int v = 0;

                    @Override
                    public void run() {

                        //Perform background work here
                        if (!mp.isPlaying()) {

                            playmp(entries.get(v++).getVal());
//                              playmp(data.getDataSetByIndex(0).getEntryForIndex(v++).getVal());


                            if (v >= entries.size())
                                timer.cancel();
                        }


                    }
                };
                //Starting Timer
                timer.scheduleAtFixedRate(time, 0, 500);



            }
        });

    }

    int[] mColors = ColorTemplate.VORDIPLOM_COLORS;

    private void addEntry() {


        data = mChart.getData();
        if(data != null) {
            int test = Integer.parseInt(editY.getText().toString());
            ILineDataSet set = data.getDataSetByIndex(0);

            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            // add a new x-value first
            data.addXValue(set.getEntryCount() + "");

            // choose a random dataSet
            int randomDataSetIndex = (int) (Math.random() * data.getDataSetCount());
            System.out.println("randomDataSetIndex: "+randomDataSetIndex);

//            data.addEntry(new Entry((float) (Math.random() * 10) + 50f, set.getEntryCount()), randomDataSetIndex);
            data.addEntry(new Entry((float) test, set.getEntryCount()) , 0);

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            mChart.setVisibleXRangeMaximum(6);
            mChart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
//
//            // this automatically refreshes the chart (calls invalidate())
            mChart.moveViewTo(data.getXValCount()-7, 50f, AxisDependency.LEFT);
        }
    }

    private void removeLastEntry() {

        LineData data = mChart.getData();

        if(data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set != null) {

                Entry e = set.getEntryForXIndex(set.getEntryCount() - 1);

                data.removeEntry(e, 0);
                // or remove by index
                // mData.removeEntry(xIndex, dataSetIndex);

                mChart.notifyDataSetChanged();
                mChart.invalidate();
            }
        }
    }

    private void addDataSet() {

        data = mChart.getData();

        if(data != null) {

            int count = (data.getDataSetCount() + 1);

            // create 10 y-vals
            ArrayList<Entry> yVals = new ArrayList<Entry>();

            if(data.getXValCount() == 0) {
                // add 10 x-entries
                for (int i = 0; i < 10; i++) {
                    data.addXValue("" + (i+1));
                }
            }

            for (int i = 0; i < data.getXValCount(); i++) {
                yVals.add(new Entry((float) (Math.random() * 50f) + 50f * count, i));
            }

            LineDataSet set = new LineDataSet(yVals, "DataSet " + count);
            set.setLineWidth(2.5f);
            set.setCircleRadius(4.5f);

            int color = mColors[count % mColors.length];

            set.setColor(color);
            set.setCircleColor(color);
            set.setHighLightColor(color);
            set.setValueTextSize(10f);
            set.setValueTextColor(color);

            data.addDataSet(set);
            mChart.notifyDataSetChanged();
            mChart.invalidate();
        }
    }

    private void removeDataSet() {

        LineData data = mChart.getData();

        if(data != null) {

            data.removeDataSet(data.getDataSetByIndex(data.getDataSetCount() - 1));

            mChart.notifyDataSetChanged();
            mChart.invalidate();
        }
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        float volume= ((e.getVal()/(mChart.getYChartMax()-mChart.getYChartMin()))*5);
        //float volume= (e.getVal()/130)*5;
        mySound.play(raygunID, 1, 1, 1, 0, volume);
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }



    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "DataSet 1");
        set.setLineWidth(2.5f);
        set.setCircleRadius(4.5f);
        set.setColor(Color.rgb(240, 99, 99));
        set.setCircleColor(Color.rgb(240, 99, 99));
        set.setHighLightColor(Color.rgb(190, 190, 190));
        set.setAxisDependency(AxisDependency.LEFT);
        set.setValueTextSize(10f);

        return set;
    }
}