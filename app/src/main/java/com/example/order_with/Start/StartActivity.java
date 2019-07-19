package com.example.order_with.Start;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import com.example.order_with.Start.VoiceVer.VoiceSTTOrder;
import com.example.order_with.Start.VoiceVer.VoiceSpeakingMenu;
import com.example.order_with.menuItem.Menu;
import com.example.order_with.menuItem.indexMenu;
import com.example.order_with.menuItem.serverMenu;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StartActivity extends AppCompatActivity {
    private TextToSpeech tts;
    String startVoice = "음성이 필요하시면 기계 하단에 이어폰을 꽂아주세요.";
    final int PERMISSION = 1;
    RequestQueue requestQueue;
    ArrayList<serverMenu> items;
    ArrayList<indexMenu> indexmenu;

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
                //intent.putExtra("indexmenu", indexmenu);
                startActivity(intent);
            }
        });

        startVoiceVer();
    }

    class RequestThread extends Thread {
        @Override
        public void run() {
            String url = "http://192.168.35.160:9000/menu";
            //String url2 = "http://192.168.35.160:9000/index";
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
                            Log.d(TAG, "onErrorResponse : " + String.valueOf(error));
                            Log.d("onErrorResponse : ", "[" + error.getMessage() + "]");

                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                    return params;
                }
            };

        /*    StringRequest request2 = new StringRequest(
                    Request.Method.GET,
                    url2,
                    new Response.Listener<String>() { // String으로 응답을 받으면 실행(정상 실행)
                        @Override
                        public void onResponse(String response2) {
                            processResponse2(response2);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onErrorResponse : " + String.valueOf(error));
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                    return params;
                }
            }; */

            // 이전 결과가 있더라도 새로 요청해서 응답을 보여줌
            request.setShouldCache(false);
            //  request2.setShouldCache(false);
            requestQueue.add(request);
            //   requestQueue.add(request2);
        }
    }

    public void processResponse(String response) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();

        JsonArray jsonArray = (JsonArray) parser.parse(response);
        int num = jsonArray.size();
        //println("메뉴 이름 반복문 : " + ((JsonObject) jsonArray.get(i)).get("name").getAsString());
        items = new ArrayList<serverMenu>();
        for (int i = 0; i < jsonArray.size(); i++) {//get item here
            items.add(i, new serverMenu(((JsonObject) jsonArray.get(i)).get("name").getAsString(),
                    ((JsonObject) jsonArray.get(i)).get("price").getAsString()));
            Log.d("starttt", items.get(i).getTitle());
        }

    }

    public void processResponse2(String response2) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();

        JsonArray jsonArray = (JsonArray) parser.parse(response2);
        //println("메뉴 이름 반복문 : " + ((JsonObject) jsonArray.get(i)).get("name").getAsString());
        indexmenu = new ArrayList<indexMenu>();
        for (int i = 0; i < jsonArray.size(); i++) {//get item here
            indexmenu.add(new indexMenu(((JsonObject) jsonArray.get(i)).get("character").getAsString(),
                    ((JsonObject) jsonArray.get(i)).get("count").getAsInt()));
            //indexmenu.get(i).getCharacter()
            Log.d("starttt", indexmenu.get(i).getCharacter());
            int count = indexmenu.get(i).getCount();
            String countStr = Integer.toString(count);
            Log.d("starttt", countStr);
        }

        Log.d("starttt", indexmenu.get(0).getCharacter());

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
