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
import com.example.order_with.R
import kotlinx.android.synthetic.main.activity_voice_start.*
import java.util.*

class VoiceStartActivity : AppCompatActivity(), TextToSpeech.OnInitListener  {
    var startVoice = "음성 인식 모드 입니다. 모든 음성은 효과음 발생 이후 말씀해주세요. 메뉴를 듣고 싶으면 메뉴판, 주문하고자 하시면 주문을 말해 주세요."
    private var tts: TextToSpeech? = null
    private var mRecognizer: SpeechRecognizer? = null
    private var matches: ArrayList<String>? = null


    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.KOREAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "The Language specified is not supported!")
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    makeVoice(startVoice)
                } else {
                    @Suppress("DEPRECATION")
                    tts!!.speak(startVoice, TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        }else{
            Log.e("TTS", "Initialization Failed!")
        }
    }

    fun makeVoice(voice: String){
        val result = tts!!.setLanguage(Locale.KOREAN)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
            Log.e("TTS", "The Language specified is not supported!")
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts!!.setLanguage(Locale.KOREAN)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts!!.speak(voice, TextToSpeech.QUEUE_FLUSH, null, this.hashCode().toString() + "")
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

            } else {
                @Suppress("DEPRECATION")
                tts!!.speak(voice, TextToSpeech.QUEUE_FLUSH, null)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_start)

        tts = TextToSpeech(this, this)
        menuButton.setOnClickListener {
            NextActivity("메뉴")
        }
        orderButton.setOnClickListener {
            NextActivity("주문")
        }
    }


    override fun onResume() {
        super.onResume()
        makeVoice(startVoice)
    }


    override fun onPause() {
        super.onPause()
        tts!!.stop()
        tts!!.shutdown()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }
    }


    private fun NextActivity(input: String) {
        if (input == "메뉴" || input == "맨유" || input == "메뉴판") {
            val intent = Intent(this, ListenMenuActivity::class.java)
            startActivity(intent)
        } else if (input == "주문") {
            val intent = Intent(this, OrderMenuActivity::class.java)
            startActivity(intent)
        } else {//Not menu or order
            makeVoice("메뉴판 혹은 주문으로 다시 한번 말씀해 주세요")
        }
    }

    internal inner class STTThread : Thread() {
        override fun run() {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                micImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_mic_black_24dp))
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
            micImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_mic_none_black_24dp))
        }

        override fun onError(error: Int) {
            micImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_mic_none_black_24dp))

            when (error) {
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> makeVoice(startVoice)
                SpeechRecognizer.ERROR_NO_MATCH -> makeVoice(startVoice)
            }
        }

        override fun onResults(results: Bundle) {
            matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            NextActivity(matches!!.get(0))
        }

        override fun onPartialResults(partialResults: Bundle) {}

        override fun onEvent(eventType: Int, params: Bundle) {}

    }

}
