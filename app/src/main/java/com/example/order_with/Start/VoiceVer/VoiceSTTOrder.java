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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.order_with.ReciptActivity;
import com.example.order_with.Start.NonVoiceVer.NVoiceMenu;
import com.example.order_with.Start.StartActivity;
import com.example.order_with.menuItem.Menu;
import com.example.order_with.menuItem.MenuAdapter;
import com.example.order_with.menuItem.indexMenu;
import com.example.order_with.menuItem.serverMenu;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.speech.tts.TextToSpeech.ERROR;

public class VoiceSTTOrder extends AppCompatActivity implements MenuAdapter.MyClickListener {
    private TextToSpeech tts;
    String startVoice = "주문하실 메뉴를 한개만 말씀해 주세요.";
    String ordermore = "더 주문하려면 메뉴이름, 결제 하려면 결제를 말해주세요";
    private String title;
    private String price;
    Intent intent;
    SpeechRecognizer mRecognizer;
    ImageView img_mic;
    ArrayList<String> matches;
    Handler delayHandler;
    private MenuAdapter mAdapter;
    private RecyclerView ListrecyclerView;
    private LinearLayoutManager selectLayoutManager;
    private ArrayList<Menu> menuList;
    private Button button;
    ArrayList<Menu> items;
    ArrayList<Menu> item1;
    ArrayList<Menu> item2;

    private Socket socket;
    private BufferedReader networkReader;
    private BufferedWriter networkWriter;
    private String ip = "192.168.35.160"; // IP
    private int port = 9900; // PORT번호
    ArrayList<serverMenu> servermenu;
    ArrayList<indexMenu> indexmenu;
    RequestQueue requestQueue;

    // git test
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicesttorder);

        requestQueue= Volley.newRequestQueue(this);

        RequestThread requestThread = new RequestThread();
        requestThread.start();

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        img_mic = (ImageView) findViewById(R.id.img_voicesttorder);
        button = (Button) findViewById(R.id.button);

        Intent intent = getIntent();
        item1 = intent.getParcelableArrayListExtra("menuToOrder");

        Intent intent2 = getIntent();
        item2 = intent2.getParcelableArrayListExtra("spmenutoOreder");

        /*
        Intent intent3 = getIntent();
        indexmenu = intent3.getParcelableArrayListExtra("indexmenu");

        String indexTest = indexmenu.get(0).getCharacter();
        Log.d("indexTest", indexTest); */

        if (item1 != null) {
            items = item1;
        } else {
            items = item2;
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_voicesttorder);
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
                Intent intent = new Intent(VoiceSTTOrder.this, ReciptActivity.class);
                intent.putExtra("clickedItem", menuList);
                startActivity(intent);
            }
        });
    }

    class RequestThread extends Thread {
        @Override
        public void run() {
            String url = "http://192.168.35.160:9000/index";

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
        //println("메뉴 이름 반복문 : " + ((JsonObject) jsonArray.get(i)).get("name").getAsString());
        indexmenu = new ArrayList<indexMenu>();
        for (int i = 0; i < jsonArray.size(); i++) {//get item here
            indexmenu.add(new indexMenu(((JsonObject) jsonArray.get(i)).get("character").getAsString(),
                    ((JsonObject) jsonArray.get(i)).get("count").getAsInt()));
            //indexmenu.get(i).getCharacter()
            Log.d("ccccccc", indexmenu.get(i).getCharacter());
            int count = indexmenu.get(i).getCount();
            String countStr = Integer.toString(count);
            Log.d("ccccccc", countStr);
        }

        //Log.d("ccccccc", indexmenu.get(0).getCharacter());

    }

    @Override
    protected void onResume() {
        super.onResume();
        VoiceStarting(startVoice);
    }

    private void VoiceStarting(final String startVoice) {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == tts.SUCCESS) {
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
            Toast.makeText(getApplicationContext(), "에러 발생", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            //sendToServer();
            matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String input_menu = matches.get(0);

            VoiceMatch(input_menu);
            VoiceStarting("추가로 주문 할 것이 있으면 메뉴를 말하시고, 결제하려면 결제를 말하세요");
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };
/*
    public void sendToServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setSocket(ip, port);
                    PrintWriter out = new PrintWriter(networkWriter,true);
                    String return_msg = matches.get(0);
                    out.println(return_msg);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }).start();

    }

    // socket 통신 관련 메소드
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        try {
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // socket 통신 관련 메소드
    public void setSocket(String ip, int port) throws IOException {
        try {
            socket = new Socket(ip, port);
            networkWriter =
                    new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            networkReader =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    */

    public void VoiceMatch(String match) {
        if(match.equals("결제")){
            Intent intent = new Intent(VoiceSTTOrder.this, ReciptActivity.class);
            intent.putExtra("clickedItem", menuList);
            startActivity(intent);
        }else {
            for (int i = 0; i < items.size(); i++) {
                if (match.equals(items.get(i).getTitle())) {
                    Menu voiceSelect = new Menu(items.get(i).getTitle(), items.get(i).getPrice());
                    menuList.add(voiceSelect);
                    mAdapter.notifyDataSetChanged();
                }
                else {
                    servermenu = new ArrayList<>();

                    int[] count_arr = new int[servermenu.size() + 1];
                    int[][] index = new int[55205][items.size() *2];

                    ////////////////////////////////////////////////////////////////////////

                    for (i = 44032; i < 55204; i++) {

                        char chr = (char) i;
                        String chrStr = Character.toString(chr);
                        Log.d("indextest", chrStr);

                        int indexTest = index[i][0];
                        String indexStr = Integer.toString(indexTest);
                        Log.d("indextest", indexStr);
                    }

                    ///////////////////////////////////////////////////////////////////

                    for (i = 0; i < match.length(); i++) {
                        //int count = index[i][0];
                        //String countString = Integer.toString(count);
                        //Log.d("ranking", countString);
                        char temp = match.charAt(i);
                        String tempStr = Character.toString(temp);
                        Log.d("ddddddddd", tempStr);

                        int flag = index[(int) temp][0];
                        String flagStr = Integer.toString(flag);
                        Log.d("ddddddddd", flagStr);

                        if (index[(int) temp][0] != 0) {
                            for (int j = 1; j < index[(int) temp][0] + 1; j++) {//
                                int menu_num = index[(int) temp][j];
                                count_arr[menu_num]++;
                            }
                        }

                        else {
                            Log.d("ddddddddd", match);
                            Log.d("ddddddddd", flagStr);
                        }
                    }

                }
            }
        }
    }

    @Override
    public void onItemClicked(Menu menu, int position) {
        title = menu.getTitle();
        price = menu.getPrice();
        Menu selectMenu = new Menu(title, price);
        menuList.add(selectMenu);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tts.stop();
        tts.shutdown();
        //delayHandler.removeMessages(0);
    }
}