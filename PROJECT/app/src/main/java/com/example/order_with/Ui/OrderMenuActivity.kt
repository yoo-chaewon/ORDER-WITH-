package com.example.order_with.Ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.order_with.Adapter.MenuAdapter
import com.example.order_with.Core.MenuAPI
import com.example.order_with.Core.NetworkCore
import com.example.order_with.Data.Menu
import com.example.order_with.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_listen_menu.*
import kotlinx.android.synthetic.main.activity_order_menu.*
import java.util.*
import kotlin.collections.ArrayList

class OrderMenuActivity : AppCompatActivity(), MenuAdapter.MyClickListener {
    override fun onItemClicked(menu: Menu, position: Int) {
        val selectMenu = Menu(menu.name, menu.num, menu.price)
        menuList!!.add(selectMenu)
        mAdapter!!.notifyDataSetChanged()
    }

    val startVoice = "주문하실 메뉴를 한개만 말씀해 주세요."
    val ordermoreVoice = "추가로 주문 할 것이 있으면 메뉴를 말하시고, 결제하려면 결제를 말하세요"
    private var mAdapter: MenuAdapter? = null
    private var menuList: ArrayList<Menu>? = null
    var selectedItems: ArrayList<Menu>? = null

    private var tts: TextToSpeech? = null
    private var mRecognizer: SpeechRecognizer? = null
    private var matches: java.util.ArrayList<String>? = null
    internal var flag: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_menu)

        btnOrder.setOnClickListener {
            val intent = Intent(this, ReciptActivity::class.java)
            intent.putExtra("clickedItem", selectedItems)
            startActivity(intent)
        }

        val layoutManager = GridLayoutManager(this, 3)
        rvMenuList.setLayoutManager(layoutManager)

        val selectLayoutManager = LinearLayoutManager(this)
        selectLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL)
        rvMenuCart.setLayoutManager(selectLayoutManager)

        getData()

    }

    override fun onResume() {
        super.onResume()
        if (flag == 0) {
            makeVoice(startVoice)

        }
    }


    fun makeVoice(voice: String) {
        tts = TextToSpeech(this, object : TextToSpeech.OnInitListener {
            override fun onInit(status: Int) {
                if (status == TextToSpeech.SUCCESS) {
                    tts!!.setLanguage(Locale.KOREAN)
                    tts!!.setSpeechRate(1.toFloat())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts!!.speak(voice, TextToSpeech.QUEUE_FLUSH, null, this.hashCode().toString() + "")
                        //tts.playSilentUtterance(5000, tts.QUEUE_ADD, null);
                    } else {
                        val map = HashMap<String, String>()
                        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "MessageId"
                        tts!!.speak(voice, TextToSpeech.QUEUE_FLUSH, map)
                    }

                    tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String) {}

                        override fun onDone(utteranceId: String) {
                            val sttThread = STTThread()
                            sttThread.start()
                        }

                        override fun onError(utteranceId: String) {}
                    })
                }
            }
        })
    }

    internal inner class STTThread : Thread() {
        override fun run() {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                ivOrderMic.setImageDrawable(resources.getDrawable(R.drawable.ic_mic_black_24dp))
                StartSTT()
            }, 1000)
        }
    }

    private fun StartSTT() {
        intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
        mRecognizer!!.setRecognitionListener(listener)
        mRecognizer!!.startListening(intent)
    }

    var listener: RecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray) {}
        override fun onEndOfSpeech() {
            ivOrderMic.setImageDrawable(resources.getDrawable(R.drawable.ic_mic_none_black_24dp))
        }

        override fun onError(error: Int) {
            ivOrderMic.setImageDrawable(resources.getDrawable(R.drawable.ic_mic_none_black_24dp))
            when (error) {
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> if (flag == 1) {
                    makeVoice("추가로 주문 할 것이 있으면 메뉴를 말하시고, 결제하려면 결제를 말하세요")
                } else {
                    makeVoice(startVoice)
                }
                SpeechRecognizer.ERROR_NO_MATCH -> if (flag == 1) {
                    makeVoice("추가로 주문 할 것이 있으면 메뉴를 말하시고, 결제하려면 결제를 말하세요")
                } else {
                    makeVoice(startVoice)
                }
            }
        }

        override fun onResults(results: Bundle) {
            matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            VoiceMatch(matches!!.get(0))
            makeVoice(ordermoreVoice)
        }

        override fun onPartialResults(partialResults: Bundle) {}

        override fun onEvent(eventType: Int, params: Bundle) {}
    }

    fun VoiceMatch(match: String) {
        var i = 0

        if (match == "결제") {
            val intent = Intent(this, ReciptActivity::class.java)
            intent.putExtra("clickedItem", selectedItems)
            startActivity(intent)
        } else {

            while (i < menuList!!.size) {
                if (match == menuList!!.get(i).name) {
                    selectedItems!!.add(menuList!!.get(i))
                    mAdapter!!.notifyDataSetChanged()
                    break
                }
                i++
            }


            if (i == menuList!!.size) {
                Log.d("Test", "Nothing Match")
                val intent2 = Intent(this, MenuRecommendActivity::class.java)
                intent2.putExtra("menu_name", match)
                intent2.putExtra("menu_fromSTT", menuList)
                startActivityForResult(intent2, 3000)
            }
        }
    }


    fun getData() {
        NetworkCore.getNetworkCore<MenuAPI>()
            .getMenuData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                menuList = it.Menus
                val adapter = MenuAdapter(menuList!!)
                rvMenuList.setAdapter(adapter)

                selectedItems = ArrayList()
                mAdapter = MenuAdapter(selectedItems!!)
                rvMenuCart.setAdapter(mAdapter)
                mAdapter!!.setOnItemClickListener(this)
            }, {
                it.printStackTrace()
            })
    }
}
