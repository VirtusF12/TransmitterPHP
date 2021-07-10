package com.example.virtus.transmitter;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;

import HttpRequest.*;
import Robot.Robot;

class MyXAxisValueFormatter implements IAxisValueFormatter {

    private String[] mValues;

    public MyXAxisValueFormatter(String[] values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        Log.d("TEG_T", "Значение: " + String.valueOf(value));
        return mValues[(int)Math.floor((double) value)];
    }

}

public class HorizontalBarChartActivity extends AppCompatActivity {

    private static final String TEG_HorizontalBarChartActivity= "TEG_HorizontalBarChartActivity";

    private HorizontalBarChart horizontalBarChart;
    private String author, subject, date, myLogin;
    private HttpRequest httpRequest;
    private ArrayList<String> listUsers, listExec;
    private ArrayList<Integer> listValue;

    /**
     * Получение данных от GraphActivity: author, subject, date
     */
    private void getDate(){
        try{

            author = getIntent().getStringExtra("author");
            subject = getIntent().getStringExtra("subject");
            date = getIntent().getStringExtra("date");
            myLogin = getIntent().getStringExtra("myLogin");

            Log.d(TEG_HorizontalBarChartActivity,"Полученные данные: " + author + ", " + subject + ", " + date);
        }   catch (Exception ex){
            Log.d(TEG_HorizontalBarChartActivity, "Данные не получены UserActivity: " + ex.toString() );
        }
    }

    private void initialize(){

        horizontalBarChart = (HorizontalBarChart) findViewById(R.id.horizontalBarChart);
        listUsers = new ArrayList<>();
        listExec = new ArrayList<>();
        listValue = new ArrayList<>();
    }

    private void checkInternet(){
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        if (!Robot.isOnline(getApplicationContext()))
            Toast.makeText(getApplicationContext(), "Нет соединения с интернетом.",Toast.LENGTH_SHORT).show();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_bar_chart);

        getDate();
        initialize();
        checkInternet();

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        final String url = "http://195.133.146.149/?part=board_manage_task&action=get&detalAction=getListUser&author="+author+"&subject="+subject+"&date="+date;
        httpRequest = new HttpRequest(url, StateRequest.GET, getApplicationContext());
        httpRequest.execute();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        listUsers.add("");
        innerParserListDate(httpRequest.getRequest(), listUsers);

        for (String t : listUsers)
            Log.d(TEG_HorizontalBarChartActivity, "---> " + t);

        for (String user : listUsers){
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            final String url$get = "http://195.133.146.149/?part=board_manage_task&action=get&detalAction=getListTaskExec&author="+myLogin+"&subject="+subject+"&date="+date+"&user="+user;
            httpRequest = new HttpRequest(url$get, StateRequest.GET, getApplicationContext());
            httpRequest.execute();
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            innerParserListDate(httpRequest.getRequest(), listExec);
            listExec.add("stop");
        }

        Log.d(TEG_HorizontalBarChartActivity, "*******************");

        for (String t : listExec)
            Log.d(TEG_HorizontalBarChartActivity, "---> " + t);

        setValue();

        for (int i = 0; i < listUsers.size(); i++)
            Log.d(TEG_HorizontalBarChartActivity, "user: " + listUsers.get(i) + ", value: " + listValue.get(i));

        try {
            // установка цвета через barDataSet
            BarDataSet set1 = new BarDataSet(getDataSet(), "The year 2018");

            int countStudent = 17;
            for (int i = 0; i < countStudent; i++)
                set1.setColors(Color.parseColor("#009688"));


            //set1.setColors( Color.parseColor("#97f791"), Color.parseColor("#6dd666"), Color.parseColor("#6ae562"), Color.parseColor("#53e04a"), Color.parseColor("#3bd831"), Color.parseColor("#0b8203"));
            // добавление dataSet в список
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            // создание barData и передача в него списка
            BarData data = new BarData(dataSets);
            data.setBarWidth(0.9f);

            // hide Y-axis
            YAxis left = horizontalBarChart.getAxisLeft();
            left.setDrawLabels(false);
            // custom X-axis labels
            String[] values = new String[]
                    {       "", "alter1", "mihkov0072", "serg3",
                            "olt4", "Kovalenko M5", "amigo6",
                            "five7", "mirin8", "olen9",
                            "viano10", "fif11", "georg12",
                            "pip13", "seor14", "ores15",
                            "derd16", "ertly17"};

            String[] arrUsers = listUsers.toArray(new String[listUsers.size()]);
            for (String u : arrUsers)
                Log.d(TEG_HorizontalBarChartActivity, "u: " + u);

            XAxis xAxis = horizontalBarChart.getXAxis();
            // xAxis.setValueFormatter(new MyXAxisValueFormatter(values));
            xAxis.setValueFormatter(new MyXAxisValueFormatter(arrUsers));


            // left.setMaxWidth(10);
            // заполнение графиком
            horizontalBarChart.setData(data);

            // подписание к графику
            Description description = new Description();
            description.setText("Rating");
            horizontalBarChart.setDescription(description);
            // скрыть легенду
            horizontalBarChart.getLegend().setEnabled(false);
            horizontalBarChart.animateY(1000);
            horizontalBarChart.invalidate();

        } catch (Exception ex){
            Log.d("TEG_test", "Ошибка: ex = " + ex.toString());
        }
    }

    private void setValue(){

        int position = 0, j = 0;

        for (int i = 0; i < listUsers.size(); i++){

            int partSum = 0;
            for (j = position; j < listExec.size(); j++){

                if (listExec.get(j).equals("stop")){
                    position = j;
                    position++;
                    break;
                } else {
                    partSum += Integer.parseInt(listExec.get(j));
                }
            }
            listValue.add(partSum);
        }
    }

    /**
     * Внутренний парсер
     * @param text httpRequest от сервера
     * @param innerList список куда заносить данные
     */
    private void innerParserListDate(String text, ArrayList<String> innerList){

        StringBuilder sb;
        char[] arr = text.toCharArray();
        for (int i = 0; i < text.length(); i++){
            char current = arr[i];
            if (current == '\''){
                sb = new StringBuilder();
                int j = i + 1;
                while (arr[j] != '\'' && j < text.length()){
                    sb.append(arr[j]);
                    j++;
                }
                i = j;
                innerList.add(sb.toString());
            }
        }
    }

    private ArrayList<BarEntry> getDataSet() {

        ArrayList<BarEntry> list = new ArrayList<>();

        for (int i = 0; i < listValue.size(); i++){

            list.add(new BarEntry((float)i, (float) listValue.get(i)));
        }

        /*
        list.add(new BarEntry(1.0f, 4.0f));
        list.add(new BarEntry(2.0f, 1.0f));
        list.add(new BarEntry(3.0f, 7.0f));
        list.add(new BarEntry(4.0f, 3.0f));
        list.add(new BarEntry(5.0f, 8.0f));
        list.add(new BarEntry(6f, 12f));
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        list.add(new BarEntry(7f, 4.0f));
        list.add(new BarEntry(8f, 1.0f));
        list.add(new BarEntry(9f, 7.0f));
        list.add(new BarEntry(10f, 3.0f));
        list.add(new BarEntry(11f, 8.0f));
        list.add(new BarEntry(12f, 12f));
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        list.add(new BarEntry(13f, 4.0f));
        list.add(new BarEntry(14f, 1.0f));
        list.add(new BarEntry(15f, 7.0f));
        list.add(new BarEntry(16f, 3.0f));
        list.add(new BarEntry(17f, 8.0f));
        */

        return list;
    }
}
