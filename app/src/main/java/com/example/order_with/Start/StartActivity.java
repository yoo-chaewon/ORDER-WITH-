package com.example.order_with.Start;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static android.speech.tts.TextToSpeech.ERROR;
import static com.android.volley.VolleyLog.TAG;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.order_with.R;
import com.example.order_with.Start.NonVoiceVer.NVoiceMenu;
import com.example.order_with.Start.VoiceVer.HeadsetReceiver;
import com.example.order_with.Start.VoiceVer.VoiceMenu;
import com.example.order_with.Start.VoiceVer.VoiceSpeakingMenu;
import com.example.order_with.menuItem.Menu;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StartActivity extends AppCompatActivity {
    private TextToSpeech tts;
    String startVoice = "음성이 필요하시면 기계 하단에 이어폰을 꽂아주세요. 이어폰 꽂이는 기계 하단 왼쪽에 있습니다.";
    final int PERMISSION = 1;
    RequestQueue requestQueue;
    ArrayList<Menu> items;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        requestQueue= Volley.newRequestQueue(this);

        RequestThread requestThread = new RequestThread();
        requestThread.start();
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        if ( Build.VERSION.SDK_INT >= 23 ){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }

        VoiceStarting();
        Button btnNonVoiceStart = (Button) findViewById(R.id.btnNonVoiceStart_start);
        btnNonVoiceStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNonVoiceVer();
            }
        });
        Button btnVoiceStart = (Button)findViewById(R.id.btnVoiceStart_start);
        btnVoiceStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VoiceMenu.class);
                startActivity(intent);
            }
        });
        startVoiceVer();
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
            request.setShouldCache(false);
            requestQueue.add(request);
        }
    }

    public void processResponse(String response) {
        JsonParser parser = new JsonParser();

        JsonArray jsonArray = (JsonArray) parser.parse(response);
        items = new ArrayList<Menu>();
        for (int i = 0; i < jsonArray.size(); i++) {//get item here
            items.add(new Menu(((JsonObject) jsonArray.get(i)).get("name").getAsString(),
                    ((JsonObject) jsonArray.get(i)).get("price").getAsString()));
        }
    }

    private void VoiceStarting() {
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
                            tts.playSilentUtterance(5000, tts.QUEUE_ADD, null);
                            VoiceStarting();
                        }

                        @Override
                        public void onError(String utteranceId) {
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    public void startNonVoiceVer() {
        Intent intent = new Intent(this, NVoiceMenu.class);
        intent.putExtra("menutoNonVoice", items);
        startActivity(intent);
    }

    private void startVoiceVer() {
        HeadsetReceiver headsetReceiver = new HeadsetReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headsetReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        tts.stop();
        tts.shutdown();
    }

    @Override
    protected void onResume() {
        super.onResume();
        VoiceStarting();
        startVoiceVer();
    }
}