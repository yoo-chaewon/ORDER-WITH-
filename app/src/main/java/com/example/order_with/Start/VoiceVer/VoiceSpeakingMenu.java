package com.example.order_with.Start.VoiceVer;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.order_with.R;
import com.example.order_with.Start.NonVoiceVer.NVoiceOrderFinal;
import com.example.order_with.menuItem.Menu;
import com.example.order_with.menuItem.MenuAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.speech.tts.TextToSpeech.ERROR;

public class VoiceSpeakingMenu extends AppCompatActivity implements MenuAdapter.MyClickListener {
    private TextToSpeech tts;
    String addVoice1 = "메뉴에는";
    String addVoice2 = "가 있습니다. 다시 들으려면 메뉴, 주문하고자 하면 주문을 말해주세요";
    private String title;
    private String price;
    Intent intent;
    SpeechRecognizer mRecognizer;
    public ArrayList<String> keywordArray;
    private ArrayList<ArrayList<String>> mGroupList = null;
    private ArrayList<String> mChildList = null;
    ImageView img_mic;
    ArrayList<String> matches;
    private MenuAdapter mAdapter;
    private RecyclerView ListrecyclerView;
    private LinearLayoutManager selectLayoutManager;
    private ArrayList<Menu> slectedMemu;
    private Button button;
    Handler delayHandler;
    ArrayList<Menu> items;
    String menuVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicespeakingmenu);
        img_mic = (ImageView) findViewById(R.id.img_voicespeakingmenu);

        Intent intent = getIntent();
        items = intent.getParcelableArrayListExtra("servermenu");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_voicespeakingmenu);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        selectLayoutManager = new LinearLayoutManager(this);
        selectLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        ListrecyclerView = (RecyclerView) findViewById(R.id.rv_addmenu);
        ListrecyclerView.setLayoutManager(selectLayoutManager);

        MenuAdapter adapter = new MenuAdapter(items);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        slectedMemu = new ArrayList<Menu>();
        mAdapter = new MenuAdapter(slectedMemu);
        ListrecyclerView.setAdapter(mAdapter);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VoiceSpeakingMenu.this, VoiceOrderFinal.class);
                intent.putExtra("clickedItem",slectedMemu);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        VoiceStarting(MakingVoiceMenu());

        delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                img_mic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic));
                StartSTT();
            }
        }, 8000);
    }

    private String MakingVoiceMenu(){
        menuVoice = " ";
        for (int i = 0; i < items.size(); i++){
            menuVoice += items.get(i).getTitle();
        }
        String resultVoice = addVoice1 + menuVoice + addVoice2;
        return resultVoice;
    }

    private void VoiceStarting(final String mvoice) {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                    tts.speak(mvoice, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

    class STTThread2 extends Thread {
        @Override
        public void run() {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    img_mic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic));
                    StartSTT();
                }
            },3000);
        }
    }
    private void StartSTT() {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        mRecognizer.setRecognitionListener(listener);
        mRecognizer.startListening(intent);
    }
    public RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
        }
        @Override
        public void onBeginningOfSpeech() {
        }
        @Override
        public void onRmsChanged(float rmsdB) {
        }
        @Override
        public void onBufferReceived(byte[] buffer) {
        }
        @Override
        public void onEndOfSpeech() {
            img_mic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_none));
        }
        @Override
        public void onError(int error) {
            img_mic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_none));
            Toast.makeText(getApplicationContext(), "에러 발생", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onResults(Bundle results) {
            matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            NextActivity(matches.get(0));
        }
        @Override
        public void onPartialResults(Bundle partialResults) {
        }
        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };

    @Override
    public void onItemClicked(Menu menu, int position) {
        title = menu.getTitle();
        price = menu.getPrice();
        //Intent intent = new Intent(this, NVoiceOrderFinal.class);
        //intent.putExtra("clickedItem",menu);
        //startActivity(intent);
        Toast.makeText(this, "ItemName" + menu.getTitle(), Toast.LENGTH_SHORT).show();
        Menu selectMenu = new Menu(title, price);
        slectedMemu.add(selectMenu);
        mAdapter.notifyDataSetChanged();
    }

    private void NextActivity(String input) {
        if (input.equals("메뉴")||input.equals("메뉴판")||input.equals("맨유")) {//replay menu
            delayHandler.removeMessages(0);
            VoiceStarting(addVoice1 + menuVoice + addVoice2);
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    img_mic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic));
                    StartSTT();
                }
            }, 22000);
        } else if (input.equals("주문")) {// go order page
            Intent intent = new Intent(this, VoiceSTTOrder.class);
            startActivity(intent);
        } else {//Not menu or order
            VoiceStarting("메뉴판 혹은 주문으로 다시 한번 말씀해 주세요");
            STTThread2 sttThread2 = new STTThread2();
            sttThread2.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        tts.stop();
        tts.shutdown();
        delayHandler.removeMessages(0);
    }
}