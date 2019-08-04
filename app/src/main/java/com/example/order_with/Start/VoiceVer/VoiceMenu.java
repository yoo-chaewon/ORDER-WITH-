package com.example.order_with.Start.VoiceVer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.order_with.R;
import com.example.order_with.menuItem.Menu;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class VoiceMenu extends AppCompatActivity {
    private TextToSpeech tts;
    String startVoice = "음성 인식 모드 입니다. 메뉴를 듣고 싶으면 메뉴판, 주문하고자 하시면 주문을 말해 주세요";
    Intent intent;
    SpeechRecognizer mRecognizer;
    ArrayList<String> matches;
    ImageView img_mic;
    ArrayList<Menu> items;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicemenu);
        img_mic = (ImageView) findViewById(R.id.img_mic_voicemenu);

        requestQueue= Volley.newRequestQueue(this);

        RequestThread requestThread = new RequestThread();
        requestThread.start();
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

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
    }

    private void VoiceStarting(final String in_voice) {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if(status==tts.SUCCESS) {
                    tts.setLanguage(Locale.KOREAN);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts.speak(startVoice, TextToSpeech.QUEUE_FLUSH, null, this.hashCode() + "");
                    } else {
                        HashMap<String, String> map = new HashMap<>();
                        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
                        tts.speak(startVoice, TextToSpeech.QUEUE_FLUSH, map);
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
                    VoiceStarting(startVoice);
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

    private void NextActivity(String input) {
        if (input.equals("메뉴") || input.equals("맨유") || input.equals("메뉴판")) {
            Intent intent = new Intent(this, VoiceSpeakingMenu.class);
            intent.putExtra("servermenu",items); //
            startActivity(intent);
        } else if (input.equals("주문")) {
            Intent intent = new Intent(this, VoiceSTTOrder.class);
            intent.putExtra("menuToOrder",items);
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

    class RequestThread extends Thread {
        @Override
        public void run() {
            String url = "http://192.168.219.107:9000/menu";
            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    url,
                    new Response.Listener<String>() { // String으로 응답을 받으면 실행(정상 실행)
                        @Override
                        public void onResponse(String response) {
                            processResponse(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    return params;
                }
            };

            // 이전 결과가 있더라도 새로 요청해서 응답을 보여줌
            request.setShouldCache(false);
            requestQueue.add(request);
        }
    }

    public void processResponse(String response) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = (JsonArray) parser.parse(response);
        items = new ArrayList<Menu>();
        for (int i = 0; i < jsonArray.size(); i++) {//get item here
            items.add(new Menu(((JsonObject) jsonArray.get(i)).get("name").getAsString(),
                    ((JsonObject) jsonArray.get(i)).get("price").getAsString()));
        }
    }
}