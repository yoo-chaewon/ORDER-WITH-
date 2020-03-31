package com.example.order_with.Ui

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.order_with.R
import com.example.order_with.Service.HeadsetReceiver
import kotlinx.android.synthetic.main.activity_main.*
import java.util.Locale.KOREAN

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    val startVoice = "음성이 필요하시면 기계 하단에 이어폰을 꽂아주세요. 이어폰 꽂이는 기계 하단 왼쪽에 있습니다. 이어폰이 없는 경우 화면 아무곳을 터치해주세요."
    private var tts: TextToSpeech? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(KOREAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "The Language specified is not supported!")
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts!!.speak(startVoice, TextToSpeech.QUEUE_FLUSH, null, "")

                    tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String) {}

                        override fun onDone(utteranceId: String) {
                            tts!!.playSilentUtterance(5000, TextToSpeech.QUEUE_ADD, null)
                            onInit(status)
                        }

                        override fun onError(utteranceId: String) {}
                    })
                } else {
                    @Suppress("DEPRECATION")
                    tts!!.speak(startVoice, TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        }else{
            Log.e("TTS", "Initialization Failed!")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startVoiceVer()

        tts = TextToSpeech(this, this)


        view_start.setOnClickListener {
            val nextIntent = Intent(this, VoiceStartActivity::class.java)
            startActivity(nextIntent)
        }
    }

    fun startVoiceVer(){
        val headsetReceiver = HeadsetReceiver()
        val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        registerReceiver(headsetReceiver, filter)
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
}
