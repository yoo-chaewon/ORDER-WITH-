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

import com.example.order_with.R;
import com.example.order_with.ReciptActivity;
import com.example.order_with.menuItem.Menu;
import com.example.order_with.menuItem.MenuAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

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
            switch (error) {
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    VoiceStarting(startVoice);
                    break;
            }
        }

        @Override
        public void onResults(Bundle results) {
            matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            VoiceMatch(matches.get(0));
            VoiceStarting("추가로 주문 할 것이 있으면 메뉴를 말하시고, 결제하려면 결제를 말하세요");
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };

    public void VoiceMatch(String match) {
        int i;
        if(match.equals("결제")){
            Intent intent = new Intent(VoiceSTTOrder.this, ReciptActivity.class);
            intent.putExtra("clickedItem", menuList);
            startActivity(intent);
        }else {
            for (i = 0; i < items.size(); i++) {
                if (match.equals(items.get(i).getTitle())) {
                    Menu voiceSelect = new Menu(items.get(i).getTitle(), items.get(i).getPrice());
                    menuList.add(voiceSelect);
                    mAdapter.notifyDataSetChanged();
                    break;
                }
            }
            if(i == items.size()) {
                Intent intent2 = new Intent(this, MenuRecommendActivity.class);
                intent2.putExtra("menu_name", match);
                intent2.putExtra("menu_fromSTT", items);
                startActivityForResult(intent2, 3000);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case 3000: {
                    String recommend = data.getStringExtra("recommend");
                    for (int i = 0; i < items.size(); i++) {
                        if (recommend.equals(items.get(i).getTitle())) {
                            menuList.add(items.get(i));
                            mAdapter.notifyDataSetChanged();
                            break;
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
    }
}