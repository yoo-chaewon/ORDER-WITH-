package com.example.order_with.Start.NonVoiceVer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Handler;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.example.order_with.R;
import com.example.order_with.menuItem.Menu;
import com.example.order_with.menuItem.MenuAdapter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;


public class NVoiceOrderFinal extends AppCompatActivity {
    private MenuAdapter mAdapter;
    private MenuAdapter mAdapter;
    private TextToSpeech tts;
    String startVoice;
    ArrayList<Menu> menuList;
    Handler delayHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nvoicefinal);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvNVoicefinal);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        ArrayList<Menu> menuList = intent.getParcelableArrayListExtra("clickedItem");
        menuList = intent.getParcelableArrayListExtra("clickedItem");

        MenuAdapter adapter = new MenuAdapter(menuList);
        adapter.notifyItemInserted(0);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MakeString();
        VoiceStarting();
        delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //StartSTT();
            }
        }, 8000);
    }
    public void MakeString() {
        String voice1 = "장바구니에는";
        String voice2 = "가 있습니다. 맞으면 결제, 틀리면 다시를 말해주세요.";
        String menu = " ";
        for (int i = 0; i < menuList.size(); i++){
            menu += menuList.get(i).getTitle();
        }
        startVoice = voice1 + menu + voice2;
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

}
