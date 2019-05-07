package com.example.order_with;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.order_with.menuItem.Menu;
import com.example.order_with.menuItem.MenuAdapter;

import java.util.ArrayList;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class ReciptActivity extends AppCompatActivity {
    private TextToSpeech tts;
    String startVoice;
    Handler delayHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_receipt);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_receipt);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        ArrayList<Menu> menuList;
        menuList = intent.getParcelableArrayListExtra("clickedItem");

        String voice1 = "결제창에는";
        String voice2 = "가 있습니다. 총액은";
        String voice3 = "원 입니다.";
        String menu = " ";
        int sum = 0;
        for (int i = 0; i < menuList.size(); i++) {
            menu += menuList.get(i).getTitle();
            sum += Integer.parseInt(menuList.get(i).getPrice());
        }
        startVoice = voice1 + menu + voice2 + sum + voice3;

        MenuAdapter adapter = new MenuAdapter(menuList);
        adapter.notifyItemInserted(0);
        recyclerView.setAdapter(adapter);

        TextView tv_sum = (TextView)findViewById(R.id.tv_receiptsum);
        tv_sum.append(String.valueOf(sum));
    }

    @Override
    protected void onResume() {
        super.onResume();
        VoiceStarting();
        delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //StartSTT();
            }
        }, 8000);
    }

    private void VoiceStarting() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                    tts.speak(startVoice, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        tts.stop();
        tts.shutdown();
        delayHandler.removeMessages(0);
    }
}