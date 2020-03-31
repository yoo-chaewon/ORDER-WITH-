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
import com.example.order_with.Core.MenuAPI
import com.example.order_with.Core.NetworkCore
import com.example.order_with.Data.Menus
import com.example.order_with.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_listen_menu.*
import java.util.*

class ListenMenuActivity : AppCompatActivity() {
    private var tts: TextToSpeech? = null
    private var mRecognizer: SpeechRecognizer? = null
    private var matches: ArrayList<String>? = null
    internal var menuVoice: String? = null


    val addVoice1 = "메뉴안내를 시작하겠습니다. 메뉴 듣기를 중단하고 주문하고자 하면 화면 아무곳을 터치해 주세요. 메뉴에는"
    val addVoice2 = "가 있습니다. 다시 들으려면 메뉴, 주문하고자 하면 주문을 말해주세요"
    val voice3 = "다시 들으려면 메뉴, 주문하고자 하면 주문을 말해주세요"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listen_menu)
        layoutListenMenu.setOnClickListener {
            tts!!.stop()
            tts!!.shutdown()

            val delayHandler = Handler()
            delayHandler.postDelayed({ makeVoice("주문하고자 하면 주문, 다시 들으려면 메뉴 말해주세요") }, 1000)
        }
        getData()
    }

    fun makeVoice(voice: String){
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



    fun getData(){

        NetworkCore.getNetworkCore<MenuAPI>()
            .getMenuData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                makeVoice(MakingVoiceMenu(it))
            },{
                it.printStackTrace()
            })
    }
    internal inner class STTThread : Thread() {
        override fun run() {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                ivListeningMenuMic.setImageDrawable(resources.getDrawable(R.drawable.ic_mic_black_24dp))
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
            ivListeningMenuMic.setImageDrawable(resources.getDrawable(R.drawable.ic_mic_none_black_24dp))
        }

        override fun onError(error: Int) {
            ivListeningMenuMic.setImageDrawable(resources.getDrawable(R.drawable.ic_mic_none_black_24dp))
            when (error) {
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> makeVoice(voice3)
                SpeechRecognizer.ERROR_NO_MATCH -> makeVoice(voice3)
            }
        }

        override fun onResults(results: Bundle) {
            matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            NextActivity(matches!!.get(0))
        }

        override fun onPartialResults(partialResults: Bundle) {}

        override fun onEvent(eventType: Int, params: Bundle) {}
    }

    private fun NextActivity(input: String) {
        if (input == "메뉴" || input == "메뉴판" || input == "맨유") {//replay menu
            makeVoice(addVoice1 + menuVoice + addVoice2)
        } else if (input == "주문") {// go order page
            val intent = Intent(this, OrderMenuActivity::class.java)
            startActivity(intent)
        } else {//Not menu or order
            makeVoice("메뉴판 혹은 주문으로 다시 한번 말씀해 주세요")
        }
    }

    private fun MakingVoiceMenu(menus: Menus): String {
        menuVoice = " "
        for (item in menus.Menus){
            menuVoice += item.name + "/"
        }

        return addVoice1 + menuVoice + addVoice2
    }
}
