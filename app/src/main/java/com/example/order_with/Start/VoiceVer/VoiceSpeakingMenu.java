package com.example.order_with.Start.VoiceVer;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.order_with.R;
import com.example.order_with.menuItem.Menu;
import com.example.order_with.menuItem.MenuAdapter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicespeakingmenu);
        img_mic = (ImageView) findViewById(R.id.img_voicespeakingmenu);

        String Menutts = readRawTextFile(this);
        getKeywordArray();
        putKeyword(Menutts);

        STTThread sttThread = new STTThread();
        sttThread.start();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_voicespeakingmenu);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Menu> items = new ArrayList<Menu>();
        for (int i = 0; i < 15; i++) {//get item here
            items.add(new Menu("유채" + i, "바보" + i));
        }
        MenuAdapter adapter = new MenuAdapter(items);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
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

    public void putKeyword(String menu) {
        String result = " ";
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

    class STTThread extends Thread {
        @Override
        public void run() {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    img_mic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic));
                    StartSTT();
                }
            }, 18000);
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
        }
        @Override
        public void onError(int error) {
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
    }


    private String returnSTT() {
        //음성이 끝나고 바로 뜰 수 있게
        //음성 받아오기- 메뉴 or 주문
        return "메뉴";
    }

    private void NextActivity(String input) {
        if (input == "메뉴") {//replay menu
        } else if (input == "주문") {// go order page
        } else {//Not menu or order

        }
    }
}