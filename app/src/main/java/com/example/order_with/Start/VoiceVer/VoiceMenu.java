package com.example.order_with.Start.VoiceVer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.order_with.R;

import java.util.ArrayList;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class VoiceMenu extends AppCompatActivity {
    private TextToSpeech tts;
    String startVoice = "음성 인식 모드 입니다. 메뉴를 듣고 싶으면 메뉴판, 주문하고자 하시면 주문을 말해 주세요";
    Intent intent;
    SpeechRecognizer mRecognizer;
    ArrayList<String> matches;
    ImageView img_mic;
    Handler delayHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicemenu);
        img_mic = (ImageView) findViewById(R.id.img_mic_voicemenu);
        Button btn_menu = (Button) findViewById(R.id.go_menu);
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextActivity("메뉴");
            }
        });
        Button btn_order = (Button) findViewById(R.id.go_order);
        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextActivity("주문");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        VoiceStarting(startVoice);

        delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                img_mic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic));
                StartSTT();
            }
        }, 8000);
    }

    private void VoiceStarting(final String in_voice) {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                    tts.speak(in_voice, TextToSpeech.QUEUE_FLUSH, null);
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
            }, 5000);
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
            Toast.makeText(getApplicationContext(), "에러 발생", Toast.LENGTH_SHORT).show();
            img_mic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_none));
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

    private void NextActivity(String input) {
        if (input.equals("메뉴") || input.equals("맨유") || input.equals("메뉴판")) {
            Intent intent = new Intent(this, VoiceSpeakingMenu.class);
            startActivity(intent);
        } else if (input.equals("주문")) {
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