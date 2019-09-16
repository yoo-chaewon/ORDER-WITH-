package com.example.order_with.Start.VoiceVer;

import android.Manifest;
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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.order_with.R;
import com.example.order_with.menuItem.Index;
import com.example.order_with.menuItem.Menu;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//TODO 추천메뉴 말해준 이후 음성 반복되도록
public class MenuRecommendActivity extends AppCompatActivity {
    private TextToSpeech tts;
    RequestQueue requestQueue;
    final int PERMISSION = 1;
    ImageView iv_recommend;
    ArrayList<Index> items;
    ArrayList<Menu> menus;
    ArrayList<Menu> recommend;
    ArrayList<Menu> recommend2;
    String input_menu;
    TextView tv_recommend;
    SpeechRecognizer mRecognizer;
    ArrayList<String> matches;
    Intent intent;
    String voice1 = "라는 메뉴는 존재하지 않습니다.";
    String voice2 = "와 유사한 추천 메뉴를 받고 싶으면, 예 그렇지 않으면 아니오.로 답하세요.";
    int flag = 0;
    String result = "";
    String result1 = "";
    String voice3 = "추천 메뉴가 없습니다.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_recommend);

        iv_recommend = (ImageView) findViewById(R.id.iv_recommend);

        recommend = new ArrayList<>();
        recommend2 = new ArrayList<>();
        menus = new ArrayList<>();
        Intent getintent = getIntent();
        menus = getintent.getParcelableArrayListExtra("menu_fromSTT");
        input_menu = getintent.getStringExtra("menu_name");

        requestQueue = Volley.newRequestQueue(this);
        RequestThread requestThread = new RequestThread();
        requestThread.start();
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }
        tv_recommend = (TextView) findViewById(R.id.tv_recommend);

        voice1 = input_menu + voice1 + input_menu + voice2;
    }

    @Override
    protected void onResume() {
        super.onResume();
        VoiceStarting(voice1);
    }


    private void VoiceStarting(final String startVoice) {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == tts.SUCCESS) {
                    tts.setLanguage(Locale.KOREAN);
                    tts.setSpeechRate((float) 1);
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
                            if (startVoice == voice3) {
                                finish();
                            } else {
                                STTThread sttThread = new STTThread();
                                sttThread.start();
                            }
                        }

                        @Override
                        public void onError(String utteranceId) {
                        }
                    });
                }
            }
        });

    }


    private void VoiceStarting2(final String startVoice) {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == tts.SUCCESS) {
                    tts.setLanguage(Locale.KOREAN);
                    tts.setSpeechRate((float) 1);
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
                            STTThread2 sttThread2 = new STTThread2();
                            sttThread2.start();
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
                    iv_recommend.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic));
                    StartSTT();
                }
            }, 1000);
        }
    }

    class STTThread2 extends Thread {
        @Override
        public void run() {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    iv_recommend.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic));
                    StartSTT2();
                }
            }, 1000);
        }
    }

    private void StartSTT() {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        mRecognizer.setRecognitionListener(listener);
        mRecognizer.startListening(intent);
    }

    private void StartSTT2() {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        mRecognizer.setRecognitionListener(listener2);
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
            iv_recommend.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_none));
        }

        @Override
        public void onError(int error) {
            iv_recommend.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_none));
            switch (error) {
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    if (flag == 0) {
                        VoiceStarting(voice1);
                    } else {
                        VoiceStarting(result);
                    }
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    if (flag == 0) {
                        VoiceStarting(voice1);
                    } else {
                        VoiceStarting(result);
                    }
                    break;
            }
        }

        @Override
        public void onResults(Bundle results) {
            int count = 0;
            matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches.get(0).equals("예") || matches.get(0).equals("네")) {
                flag = 1;
                int[] count_arr = new int[menus.size() + 1];
                for (int i = 0; i < input_menu.length(); i++) {
                    char temp = input_menu.charAt(i);
                    for (int j = 0; j < items.size(); j++) {
                        if (items.get(j).getWord() == temp) {
                            count = 1;
                            String[] split = items.get(j).getList().split("/");
                            for (int k = 0; k < split.length; k++) {
                                count_arr[Integer.parseInt(split[k])]++;
                            }
                        } else {
                            VoiceStarting(voice3);
                        }
                    }
                }


                if (count == 1) {
                    int max = -1;
                    for (int i = 0; i < menus.size() + 1; i++) {
                        max = Math.max(max, count_arr[i]);
                    }
                    result = "";

                    for (int i = 0; i < menus.size() + 1; i++) {
                        if (count_arr[i] == max) {
                            recommend.add(menus.get(i));
                        }
                    }

                    max = -1;
                    ArrayList<Integer> count_result = new ArrayList<>();
                    for (int k = 0; k < recommend.size(); k++) {
                        int[][] map = new int[input_menu.length() + 1][recommend.get(k).getTitle().length() + 1];
                        for (int i = 1; i <= input_menu.length(); i++) {
                            for (int j = 1; j <= recommend.get(k).getTitle().length(); j++) {
                                if (input_menu.charAt(i - 1) == recommend.get(k).getTitle().charAt(j - 1)) {
                                    map[i][j] = map[i - 1][j - 1] + 1;
                                } else {
                                    map[i][j] = Math.max(map[i - 1][j], map[i][j - 1]);
                                }
                            }
                        }
                        max = Math.max(max, map[input_menu.length()][recommend.get(k).getTitle().length()]);
                        count_result.add(map[input_menu.length()][recommend.get(k).getTitle().length()]);
                    }

                    for (int i = 0; i < count_result.size(); i++) {
                        Log.d("채옹", recommend.get(i) + "" + count_result.get(i));
                    }

                    while (true) {
                        for (int i = 0; i < count_result.size(); i++) {
                            if (count_result.get(i) == max) {
                                recommend2.add(recommend.get(i));
                                result1 += recommend.get(i).getTitle() + "\n";
                            }
                        }
                        if (recommend2.size() > 4) break;//TODO 갯수 수정
                        if (recommend2.size() == recommend.size())
                            break;//TODO 추천 메뉴 자체가 4개가 되지 않을 경우 모두 추천되고 머시기 졸려서 생각 못하겠음
                        max--;
                    }

                    tv_recommend.setText(result1);
                    result1 = "추천 메뉴로는" + result1 + "가 있습니다. 이 중 주문하실 메뉴를 한개만 말씀해 주세요.";
                    VoiceStarting2(result1);
                }

            } else if (matches.get(0).equals("아니요") || matches.get(0).equals("아니오")) {
                finish();
            } else {
                String result = "예 혹은 아니오로 다시 한번 말씀해주세요.";
                VoiceStarting(result);
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };

    public RecognitionListener listener2 = new RecognitionListener() {
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
            iv_recommend.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_none));
        }

        @Override
        public void onError(int error) {
            iv_recommend.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_none));
            switch (error) {
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    VoiceStarting2(result1);
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    VoiceStarting2(result1);
                    break;
            }

        }

        @Override
        public void onResults(Bundle results) {
            int i;
            matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (i = 0; i < recommend2.size(); i++) {
                for (int j = 0; j < matches.size(); j++) {
                    if (matches.get(j).equals(recommend2.get(i).getTitle())) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("recommend", recommend2.get(i).getTitle());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                        break;
                    }
                }
            }
            if (i == recommend2.size()) {
                String str = "추천 메뉴로 다시 한 번 말씀해주세요.";
                VoiceStarting2(str + result1);
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        tts.stop();
        tts.shutdown();
    }


    class RequestThread extends Thread {
        @Override
        public void run() {
            String url = "http://172.20.10.3:8000/index";
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
        items = new ArrayList<Index>();
        for (int i = 0; i < jsonArray.size(); i++) {//get item here
            items.add(new Index(((JsonObject) jsonArray.get(i)).get("word").getAsCharacter(),
                    ((JsonObject) jsonArray.get(i)).get("count").getAsInt(),
                    ((JsonObject) jsonArray.get(i)).get("list").getAsString()));
        }
    }
}
