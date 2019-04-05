package com.example.order_with.Start.VoiceVer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.order_with.R;
import com.example.order_with.menuItem.Menu;
import com.example.order_with.menuItem.MenuAdapter;

import java.util.ArrayList;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class VoiceOrderFinal extends AppCompatActivity {
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
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        ArrayList<Menu> menuList = intent.getParcelableArrayListExtra("clickedItem");
        menuList = intent.getParcelableArrayListExtra("clickedItem");

        String voice1 = "장바구니에는";
        String voice2 = "가 있습니다. 맞으면 결제, 틀리면 다시를 말해주세요.";
        String menu = " ";
        for (int i = 0; i < menuList.size(); i++) {
            menu += menuList.get(i).getTitle();
        }
        startVoice = voice1 + menu + voice2;


        MenuAdapter adapter = new MenuAdapter(menuList);
        adapter.notifyItemInserted(0);
        recyclerView.setAdapter(adapter);
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
