package com.example.order_with.Start.VoiceVer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.order_with.R;
import com.example.order_with.menuItem.Menu;
import com.example.order_with.menuItem.MenuAdapter;

import java.util.ArrayList;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class VoiceSTTOrder extends AppCompatActivity implements MenuAdapter.MyClickListener {
    private TextToSpeech tts;
    String startVoice = "주문하실 메뉴를 한개만 말씀해 주세요.";
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicesttorder);
        img_mic = (ImageView) findViewById(R.id.img_voicesttorder);
        button = (Button) findViewById(R.id.button);

        Intent intent = getIntent();
        item1 = intent.getParcelableArrayListExtra("menuToOrder");

        Intent intent2 = getIntent();
        item2 = intent2.getParcelableArrayListExtra("spmenutoOreder");

        if (item1 != null){
            items = item1;
        }else {
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
                Intent intent = new Intent(VoiceSTTOrder.this, VoiceOrderFinal.class);
                intent.putExtra("clickedItem",menuList);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        VoiceStarting();

        delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                img_mic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic));
                StartSTT();
            }
        }, 4000);
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
        /*
        * tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
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
                            Log.d("dddddddddd", "음성 실행 중");
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            STTThread sttThread = new STTThread();
                            sttThread.start();
                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.d("dddddddddd", "음성 에러");
                        }
                    });
                }
            }
        });
        * */
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
            Toast.makeText(getApplicationContext(), matches.get(0), Toast.LENGTH_SHORT).show();
            VoiceMatch(matches.get(0));
        }
        @Override
        public void onPartialResults(Bundle partialResults) {
        }
        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };

    public void VoiceMatch(String match){
        for (int i = 0; i < items.size(); i++){
            if (match.equals(items.get(i).getTitle())){
                Menu voiceSelect = new Menu(items.get(i).getTitle(), items.get(i).getPrice());
                menuList.add(voiceSelect);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onItemClicked(Menu menu, int position) {
        title = menu.getTitle();
        price = menu.getPrice();
        //Intent intent = new Intent(this, NVoiceOrderFinal.class);
        //intent.putExtra("clickedItem",menu);
        Menu selectMenu = new Menu(title, price);
        menuList.add(selectMenu);
        mAdapter.notifyDataSetChanged();
        //startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        tts.stop();
        tts.shutdown();
        delayHandler.removeMessages(0);
    }
}