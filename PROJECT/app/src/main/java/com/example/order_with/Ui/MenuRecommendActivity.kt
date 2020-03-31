package com.example.order_with.Ui

import android.Manifest
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
import android.widget.ImageView
import android.widget.TextView
import com.example.order_with.Adapter.MenuAdapter
import com.example.order_with.Core.IndexAPI
import com.example.order_with.Core.MenuAPI
import com.example.order_with.Core.NetworkCore
import com.example.order_with.Data.Index
import com.example.order_with.Data.Menu
import com.example.order_with.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_menu_recommend.*
import kotlinx.android.synthetic.main.activity_order_menu.*
import java.util.*

class MenuRecommendActivity : AppCompatActivity(), TextToSpeech.OnInitListener  {
    private var tts: TextToSpeech? = null
//    internal var requestQueue: RequestQueue? = null
    internal val PERMISSION = 1
    internal var indexItems: ArrayList<Index>? = null
    internal var menus: ArrayList<Menu>? = null
    internal var recommend: ArrayList<Menu>? = null
    internal var recommend2: ArrayList<Menu>? = null
    internal var input_menu: String? = null
    internal var mRecognizer: SpeechRecognizer? = null
    internal var matches: ArrayList<String>? = null
    internal var intent: Intent? = null
    internal var voice1 = "라는 메뉴는 존재하지 않습니다."
    internal var voice2 = "와 유사한 추천 메뉴를 받고 싶으면, 예 그렇지 않으면 아니오.로 답하세요."
    internal var flag = 0
    internal var result = ""
    internal var result1 = ""
    internal var voice3 = "추천 메뉴가 없습니다."

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.KOREAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "The Language specified is not supported!")
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    makeVoice(startVoice)
                } else {
//                    @Suppress("DEPRECATION")
//                    tts!!.speak(startVoice, TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        }else{
            Log.e("TTS", "Initialization Failed!")
        }
    }

    override fun onResume() {
        super.onResume()
        makeVoice(voice1)

    }

    fun makeVoice(voice: String) {
        tts = TextToSpeech(this, object : TextToSpeech.OnInitListener {
            override fun onInit(status: Int) {
                if (status == TextToSpeech.SUCCESS) {
                    tts!!.setLanguage(Locale.KOREAN)
                    tts!!.setSpeechRate(1.toFloat())
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
                            if (voice === voice3) {
                                finish()
                            } else {
                                val sttThread = STTThread()
                                sttThread.start()
                            }
                        }

                        override fun onError(utteranceId: String) {}
                    })
                }
            }
        })
    }

    private fun VoiceStarting2(startVoice: String) {
        tts = TextToSpeech(this, object : TextToSpeech.OnInitListener {
            override fun onInit(status: Int) {
                if (status == TextToSpeech.SUCCESS) {
                    tts!!.setLanguage(Locale.KOREAN)
                    tts!!.setSpeechRate(1.toFloat())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts!!.speak(startVoice, TextToSpeech.QUEUE_FLUSH, null, this.hashCode().toString() + "")
                    } else {
                        val map = HashMap<String, String>()
                        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "MessageId"
                        tts!!.speak(startVoice, TextToSpeech.QUEUE_FLUSH, map)
                    }

                    tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String) {}

                        override fun onDone(utteranceId: String) {
                            val sttThread2 = STTThread2()
                            sttThread2.start()
                        }

                        override fun onError(utteranceId: String) {}
                    })
                }
            }
        })

    }

    internal inner class STTThread : Thread() {
        override fun run() {
            Log.d("Test", "STTThread")
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                iv_recommend.setImageDrawable(resources.getDrawable(R.drawable.ic_mic_black_24dp))
                StartSTT()
            }, 1000)
        }
    }

    internal inner class STTThread2 : Thread() {
        override fun run() {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                iv_recommend.setImageDrawable(resources.getDrawable(R.drawable.ic_mic_black_24dp))
                StartSTT2()
            }, 1000)
        }
    }

    private fun StartSTT() {
        intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
        mRecognizer!!.setRecognitionListener(listener)
        mRecognizer!!.startListening(intent)
    }



    private fun StartSTT2() {
        intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
        mRecognizer!!.setRecognitionListener(listener2)
        mRecognizer!!.startListening(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_recommend)

        getData()

        recommend = ArrayList()
        recommend2 = ArrayList()
        menus = ArrayList()
        val getintent = getIntent()
        menus = getintent.getParcelableArrayListExtra("menu_fromSTT")
        input_menu = getintent.getStringExtra("menu_name")
        voice1 = input_menu + voice1 + input_menu + voice2
    }

    fun getData() {
        NetworkCore.getNetworkCore<IndexAPI>()
            .getMenuData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                indexItems = it.Indexs
            }, {
                it.printStackTrace()
            })
    }

    var listener: RecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle) {}

        override fun onBeginningOfSpeech() {}

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray) {}

        override fun onEndOfSpeech() {
            iv_recommend.setImageDrawable(resources.getDrawable(R.drawable.ic_mic_none_black_24dp))
        }

        override fun onError(error: Int) {
            iv_recommend.setImageDrawable(resources.getDrawable(R.drawable.ic_mic_none_black_24dp))
            when (error) {
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> if (flag == 0) {
                    makeVoice(voice1)
                } else {
                    makeVoice(result)
                }
                SpeechRecognizer.ERROR_NO_MATCH -> if (flag == 0) {
                    makeVoice(voice1)
                } else {
                    makeVoice(result)
                }
            }
        }

        override fun onResults(results: Bundle) {
            var count = 0
            matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (matches!!.get(0) == "예" || matches!!.get(0) == "네") {
                Log.d("yooTest", "yes")
                flag = 1
                val count_arr = IntArray(menus!!.size + 1)

                for (i in 0 until input_menu!!.length) {
                    val temp = input_menu!![i]

                    for (j in indexItems!!.indices) {
                        Log.d("yooTest",""+indexItems!!.get(j).word+"/"+temp )


                        if (indexItems!!.get(j).word.equals(temp)) {
                            count = 1
                            val split = indexItems!!.get(j).list.split("/")
                            for (k in split.indices) {

                                count_arr[Integer.parseInt(split[k])]++
                            }
                        } else {
                            makeVoice(voice3)

                        }
                    }
                }


                if (count == 1) {
                    var max = -1
                    for (i in 0 until menus!!.size + 1) {
                        max = Math.max(max, count_arr[i])
                    }
                    result = ""

                    for (i in 0 until menus!!.size + 1) {
                        if (count_arr[i] == max) {
                            recommend!!.add(menus!!.get(i))
                        }
                    }

                    max = -1
                    val count_result = ArrayList<Int>()
                    for (k in recommend!!.indices) {
                        val map = Array(input_menu!!.length + 1) { IntArray(recommend!![k].name.length + 1) }
                        for (i in 1..input_menu!!.length) {
                            for (j in 1..recommend!![k].name.length) {
                                if (input_menu!!.get(i - 1) == recommend!![k].name[j-1]) {
                                    map[i][j] = map[i - 1][j - 1] + 1
                                } else {
                                    map[i][j] = Math.max(map[i - 1][j], map[i][j - 1])
                                }
                            }
                        }
                        max = Math.max(max, map[input_menu!!.length][recommend!![k].name.length])
                        count_result.add(map[input_menu!!.length][recommend!![k].name.length])
                    }

                    while (true) {
                        for (i in count_result.indices) {
                            if (count_result[i] == max) {
                                recommend2!!.add(recommend!![i])
                                result1 += recommend!![i].name + "\n"
                            }
                        }
                        if (recommend2!!.size > 4) break
                        if (recommend2!!.size == recommend!!.size)
                            break
                        max--
                    }

                    tv_recommend.text = result1
                    result1 = "추천 메뉴로는" + result1 + "가 있습니다. 이 중 주문하실 메뉴를 한개만 말씀해 주세요."
                    VoiceStarting2(result1)
                }

            } else if (matches!!.get(0) == "아니요" || matches!!.get(0) == "아니오") {
                finish()
            } else {
                val result = "예 혹은 아니오로 다시 한번 말씀해주세요."
                makeVoice(result)
            }
        }

        override fun onPartialResults(partialResults: Bundle) {}

        override fun onEvent(eventType: Int, params: Bundle) {}
    }

    var listener2: RecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle) {}

        override fun onBeginningOfSpeech() {}

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray) {}

        override fun onEndOfSpeech() {
            iv_recommend.setImageDrawable(resources.getDrawable(R.drawable.ic_mic_none_black_24dp))
        }

        override fun onError(error: Int) {
            iv_recommend.setImageDrawable(resources.getDrawable(R.drawable.ic_mic_none_black_24dp))
            when (error) {
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> VoiceStarting2(result1)
                SpeechRecognizer.ERROR_NO_MATCH -> VoiceStarting2(result1)
            }

        }

        override fun onResults(results: Bundle) {
            var i: Int
            matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            i = 0
            while (i < recommend2!!.size) {
                for (j in matches!!.indices) {
                    if (matches!!.get(j) == recommend2!![i].name) {
                        val resultIntent = Intent()
                        resultIntent.putExtra("recommend", recommend2!![i].name)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                        break
                    }
                }
                i++
            }
            if (i == recommend2!!.size) {
                val str = "추천 메뉴로 다시 한 번 말씀해주세요."
                VoiceStarting2(str + result1)
            }
        }

        override fun onPartialResults(partialResults: Bundle) {}

        override fun onEvent(eventType: Int, params: Bundle) {}
    }

    override fun onPause() {
        super.onPause()
        tts!!.stop()
        tts!!.shutdown()
    }
}
