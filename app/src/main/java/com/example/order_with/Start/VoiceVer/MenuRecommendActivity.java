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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MenuRecommendActivity extends AppCompatActivity {
    private TextToSpeech tts;
    RequestQueue requestQueue;
    final int PERMISSION = 1;
    ImageView iv_recommend;
    ArrayList<Index> items;
    ArrayList<Menu> menus;
    ArrayList<Menu> recommend;
    String input_menu;
    TextView tv_recommend;
    SpeechRecognizer mRecognizer;
    ArrayList<String> matches;
    Intent intent;
    String voice1 = "라는 메뉴는 존재하지 않습니다.";
    String voice2 = "와 유사한 추천 메뉴를 받고 싶으면, 예 그렇지 않으면 아니오.로 답하세요.";
    int flag = 0;
    String result = " ";
    String voice3 = "추천 메뉴가 없습니다.";
    String strMin = " ";
    int minDistance;
    int[] arrDis;
    int min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_recommend);

        iv_recommend = (ImageView) findViewById(R.id.iv_recommend);

        recommend = new ArrayList<>();
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
        //VoiceStarting(voice1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        VoiceStarting(voice1);
    }

    public class editDistance {
        public int M[][] = new int[100][100];

        public int getMin(int a, int b, int c) {
            int min = a;

            if(min > b)
                min = b;
            if(min > c)
                min = c;

            return min;
        }

        public void getDistance(String a, String b) {
            for (int i=0; i<a.length(); i++) {
                M[i][0] = i;
            }
            for(int j=0; j<b.length(); j++) {
                M[0][j] = j;
            }
            for(int i=1; i<a.length(); i++) {
                for(int j=1; j<b.length(); j++) {
                    if(a.charAt(i) == b.charAt(j)) {
                        M[i][j] = M[i-1][j-1];
                    } else {
                        M[i][j] = getMin(M[i-1][j], M[i-1][j-1], M[i][j-1]) + 1;
                    }
                }
            }

            minDistance = M[a.length() -1][b.length()-1];
            //strMin = Integer.toString(minDistance);
        }



    }



    private void VoiceStarting(final String startVoice) {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == tts.SUCCESS) {
                    tts.setLanguage(Locale.KOREAN);
                    tts.setSpeechRate((float)0.5);
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
                            if(startVoice == voice3) {
                                finish();
                            }

                            else {
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
                    tts.setSpeechRate((float)0.5);
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
                public void run() {                    iv_recommend.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic));

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
                    if(flag == 0){
                        VoiceStarting(voice1);
                    }else {
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
                        }

                        else {
                            // 추천 메뉴 없을 경우 추가
                            Log.d("match2222", "else 실행");
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

                    arrDis = new int[menus.size()];
                    for (int i = 0; i < menus.size() + 1; i++) {
                        if (count_arr[i] == max) {

                            //========================================================//
                            String a = input_menu;
                            String b = menus.get(i).getTitle();

                            editDistance d = new editDistance();
                            d.getDistance(a,b);

                            //Log.d("mindistance111", "strMin은 " + strMin);

                            arrDis[i] = minDistance;
                            //Log.d("mindistance111", "arrDis는 " + i + " : " + arrDis[i]);

                            //========================================================//

                           recommend.add(menus.get(i)); ////////////////
                           //result = result + menus.get(i).getTitle() + "\n"; ///////////////

                        }

                    }

                    int flag = 0;
                    int min1 = 0;
                    for(int i=0; i<arrDis.length; i++) {
                        if (arrDis[i] != 0) {
                            min1 = arrDis[i];
                        for (i = i + 1; i < arrDis.length; i++) {
                            if (arrDis[i] != 0) {
                                flag = arrDis[i];
                                Log.d("mindistance111", "sss는 " + i + " : " + flag);

                                if (min1 >= flag) {
                                    min = flag;
                                    Log.d("mindistance111", "min11는 " + i + " : " + min);
                                    Log.d("mindistance111", "min11는 " + menus.get(i).getTitle());

                                    result = result + menus.get(i).getTitle() + "\n";
                                    tv_recommend.setText(result);
                                }
                            }
                        }

                    }

                    }

                    result = "추천 메뉴로는" + result + "가 있습니다. 이 중 주문하실 메뉴를 한개만 말씀해 주세요.";
                    VoiceStarting2(result);
                }

                } else if (matches.get(0).equals("아니요") || matches.get(0).equals("아니오")) {
                    finish();
                } else {
                    Log.d("kkkkk", "else 실행2");
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
                        if(flag == 0){
                            //VoiceStarting2(result); // 추천 메뉴로는 치즈김밥이 있습니다.
                        }else {
                            VoiceStarting2(result); // 추천 메뉴로는 치즈김밥이 있습니다.
                        }
                        break;
                }

        }

        @Override
        public void onResults(Bundle results) {
            int i;
            matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (i = 0; i < recommend.size(); i++) {
                Log.d("kkkkk11", matches.get(0));
                if (matches.get(0).equals(recommend.get(i).getTitle())) {
                    Log.d("kkkkk11", "if문 실행");
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("recommend", recommend.get(i).getTitle());
                    setResult(RESULT_OK, resultIntent);

                    finish();
                    break;

                }
            }

            if(i == recommend.size()) {
                String str = "추천 메뉴로 다시 한 번 말씀해주세요.";
                VoiceStarting2(str + result);
                //VoiceStarting2(result);
                //Log.d("kkkkk11","else문 실행");
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
            String url = "http://192.168.35.253:9000/index";
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
