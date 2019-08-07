package com.example.order_with.Start.VoiceVer;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.order_with.R;
import com.example.order_with.menuItem.Menu;
import com.example.order_with.menuItem.MenuAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;

public class VoiceSpeakingMenu extends AppCompatActivity implements MenuAdapter.MyClickListener {
    private TextToSpeech tts;
    String addVoice1 = "메뉴안내를 시작하겠습니다. 메뉴 듣기를 중단하고 싶으면 화면 아무곳을 터치해 주세요. 메뉴에는";
    String addVoice2 = "가 있습니다. 다시 들으려면 메뉴, 주문하고자 하면 주문을 말해주세요";
    String voice3 = "다시 들으려면 메뉴, 주문하고자 하면 주문을 말해주세요";
    Intent intent;
    SpeechRecognizer mRecognizer;
    ImageView img_mic;
    ArrayList<String> matches;
    private LinearLayoutManager selectLayoutManager;
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

        MenuAdapter adapter = new MenuAdapter(items);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.layout_voiceSpeakingmenu);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//음성 중단//다시 듣고 싶으면 메뉴, 주문하시려면 주문을 말해주세요
                tts.stop();
                tts.shutdown();

                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        VoiceStarting("주문하고자 하면 주문, 다시 들으려면 메뉴 말해주세요");
                    }
                }, 1000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        VoiceStarting(MakingVoiceMenu());
    }

    private String MakingVoiceMenu() {
        menuVoice = " ";

        for (int i = 0; i < items.size(); i++) {
            menuVoice += items.get(i).getTitle() + "  ,  ";

           // tts.playSilentUtterance(2000, QUEUE_ADD, null);
        }
        String resultVoice = addVoice1 + menuVoice  + addVoice2;
        return resultVoice;
    }

    private void VoiceStarting(final String mvoice) {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==tts.SUCCESS) {
                    tts.setLanguage(Locale.KOREAN);
                    tts.setSpeechRate((float)0.5);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts.speak(mvoice, TextToSpeech.QUEUE_FLUSH, null, this.hashCode() + "");
                        //tts.playSilentUtterance(2000, tts.QUEUE_ADD, null);
                    } else {
                        HashMap<String, String> map = new HashMap<>();
                        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
                        tts.speak(mvoice, TextToSpeech.QUEUE_FLUSH, map);
                    }

                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            STTThread sttThread = new STTThread();
                            sttThread.start();
                        }

                        @Override
                        public void onError(String utteranceId) {
                        }
                    });
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
            },1000);
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
            switch (error) {
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    VoiceStarting(voice3);
                    break;
            }
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
    }

    private void NextActivity(String input) {
        if (input.equals("메뉴") || input.equals("메뉴판") || input.equals("맨유")) {//replay menu
            VoiceStarting(addVoice1 + menuVoice + addVoice2);
        } else if (input.equals("주문")) {// go order page
            Intent intent = new Intent(this, VoiceSTTOrder.class);
            intent.putExtra("spmenutoOreder", items);
            startActivity(intent);
        } else {//Not menu or order
            VoiceStarting("메뉴판 혹은 주문으로 다시 한번 말씀해 주세요");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        tts.stop();
        tts.shutdown();
    }
}