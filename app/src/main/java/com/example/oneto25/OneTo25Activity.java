package com.example.oneto25;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class OneTo25Activity extends AppCompatActivity {
    TextView tv;
    TextView stopWatch;
    Button btnRe;
    Button btnStart;
    Button[] btns = new Button[25];

    int cnt = 1;
    int m=0;
    int s=0;
    int ms=0;
    int time=0;
    Drawable btnBack;

    boolean isRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oneto25);

        tv = findViewById(R.id.tv);
        stopWatch=findViewById(R.id.tvStopWatch);
        btnStart=findViewById(R.id.btnStart);
        btnRe = findViewById(R.id.btnRe);

        showRecord();

        for(int i=0;i<btns.length;i++){
            btns[i] = findViewById(R.id.btn01+i);
            btns[i].setClickable(false);
        }
        btnBack = btns[0].getBackground();


        initial();
    }

    public void clickRetry(View v){
        initial();
        btnRe.setEnabled(false);
        clickStart(v);
    }

    public void clickBtn(View v){
        Button btn = (Button)v;
        int num = Integer.parseInt(btn.getTag().toString());

        if (num==cnt){
            btn.setText("OK");
            btn.setTextColor(Color.DKGRAY);
            btn.setBackgroundColor(Color.WHITE);
            cnt++;
        }

        if (cnt > 25) {
            isRun=false;
            int result=ms+(s*100)+(m*6000);

            btnRe.setEnabled(true);
            btnRe.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.GONE);

            SharedPreferences sharedPreferences= getSharedPreferences("record1", MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            int record=sharedPreferences.getInt("25", 0);
            if(result<record||record==0){
                editor.putInt("25", result );
                editor.commit();
                tv.setText("최고 기록 " +m+" : "+s+" : "+ms);
                new AlertDialog.Builder(this).setMessage("축하합니다! \n새 기록을 달성했습니다").show();
            }
        }
    }

    void initial(){
        cnt=1;

        ArrayList<Integer> list = new ArrayList<>();
        for (int i=1;i<=25;i++){
            list.add(i);
        }
        Collections.shuffle(list);

        for (int i=0;i<btns.length;i++){
            btns[i].setText( list.get(i) +"");
            btns[i].setTextColor(Color.WHITE);
            btns[i].setBackground(btnBack);
            btns[i].setTag( list.get(i) );
        }
    }

    public void clickStart(View view) {
        time=0;
        TimeThread timeThread= new TimeThread();
        timeThread.start();

        for(int i=0; i<btns.length;i++){
            btns[i].setClickable(true);
        }
        btnStart.setEnabled(false);
    }

    public void clickMain(View view) {
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    class TimeThread extends Thread{
        @Override
        public void run() {
            isRun=true;
            while (isRun){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Message message=new Message();
                        message.arg1=time++;
                        ms=message.arg1%100;
                        s=(message.arg1/100)%60;
                        m=(message.arg1/100)/60;
                        String result=String.format("%02d : %02d : %02d", m, s, ms );
                        stopWatch.setText(result);
                    }
                });
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showRecord(){
        SharedPreferences sharedPreferences=getSharedPreferences("record1", MODE_PRIVATE);
        int highscore=sharedPreferences.getInt("25", 0);
        int min=highscore/6000;
        int sec=highscore%6000/100;
        int mil=highscore%6000%100;

        tv.setText("최고 기록 " +min+" : "+sec+" : "+mil);
    }
}
