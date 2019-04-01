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
import com.example.order_with.R;
import com.example.order_with.Start.NonVoiceVer.NVoiceOrderFinal;
import com.example.order_with.menuItem.Menu;
import com.example.order_with.menuItem.MenuAdapter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Locale;

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
    private int count = -1;
    private ArrayList<Menu> menuList;
    private Button button;
    Handler delayHandler;
    String Menutts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicespeakingmenu);
        img_mic = (ImageView) findViewById(R.id.img_voicespeakingmenu);
        button = (Button) findViewById(R.id.button);

        Menutts = readRawTextFile(this);
        Menutts = readRawTextFile(this);
        ArrayList<Menu> items = new ArrayList<Menu>();
        for (int i = 0; i < 15; i++) {//get item here
            items.add(new Menu("유채" + i, "바보" + i));
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_voicespeakingmenu);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);

        selectLayoutManager = new LinearLayoutManager(this);
        selectLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        ListrecyclerView = (RecyclerView) findViewById(R.id.rv_addmenu);
        ListrecyclerView.setLayoutManager(selectLayoutManager);

        menuList = new ArrayList<Menu>();
        mAdapter = new MenuAdapter(menuList);
        ListrecyclerView.setAdapter(mAdapter);

        MenuAdapter adapter = new MenuAdapter(items);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VoiceSpeakingMenu.this, NVoiceOrderFinal.class);
                intent.putExtra("clickedItem",menuList);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        getKeywordArray();
        putKeyword(Menutts);

        delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                img_mic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic));
                StartSTT();
            }
        }, 22000);
    }
    public void getKeywordArray() {
        mGroupList = new ArrayList<ArrayList<String>>();

        for (int i = 0; i < keywordArray.size(); i++) {
            mChildList = new ArrayList<String>();
            mChildList.add(keywordArray.get(i));
            mGroupList.add(mChildList);
        }
        Log.d("mGroupList", "" + mGroupList);
    }

    String result = " ";
    public void putKeyword(String menu) {
        for (int i = 0; i < mGroupList.size(); i++) {
            result = menu;
            VoiceStarting(addVoice1 + result + addVoice2);
        }
    }

    public String readRawTextFile(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.miso_menu);
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);

        String keyword;
        keywordArray = new ArrayList<String>();
        StringBuilder text = new StringBuilder();

        try {
            while ((keyword = buffreader.readLine()) != null) {
                keywordArray.add(keyword);
                text.append(keyword);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        Log.d("keywordArray", "" + keywordArray);
        return text.toString();
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

        Toast.makeText(this, "ItemName" + menu.getTitle(), Toast.LENGTH_SHORT).show();
        Menu selectMenu = new Menu("메뉴이름" + title, "가격" + price);
        menuList.add(selectMenu);
        mAdapter.notifyDataSetChanged();
    }


    private void NextActivity(String input) {
        if (input.equals("메뉴")||input.equals("메뉴판")||input.equals("맨유")) {//replay menu
            delayHandler.removeMessages(0);
            VoiceStarting(addVoice1 + result + addVoice2);
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