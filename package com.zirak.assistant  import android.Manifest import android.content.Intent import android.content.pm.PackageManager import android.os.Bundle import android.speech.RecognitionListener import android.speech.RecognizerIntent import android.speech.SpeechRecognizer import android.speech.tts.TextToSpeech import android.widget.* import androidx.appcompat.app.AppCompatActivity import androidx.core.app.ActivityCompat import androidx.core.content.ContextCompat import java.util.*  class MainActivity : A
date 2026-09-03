package com.zirak.assistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var chatContainer: LinearLayout
    private lateinit var chatScroll: ScrollView
    private lateinit var inputText: EditText
    private lateinit var statusText: TextView

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var tts: TextToSpeech
    private val claudeClient = ClaudeApiClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chatContainer = findViewById(R.id.chatContainer)
        chatScroll = findViewById(R.id.chatScroll)
        inputText = findViewById(R.id.inputText)
        statusText = findViewById(R.id.statusText)

        val micButton = findViewById<Button>(R.id.micButton)
        val sendButton = findViewById<Button>(R.id.sendButton)

        tts = TextToSpeech(this, this)

        requestMicPermission()
        setupSpeechRecognizer()

        micButton.setOnClickListener { startListening() }
        sendButton.setOnClickListener {
            val text = inputText.text.toString().trim()
            if (text.isNotEmpty()) {
                handleUserMessage(text)
                inputText.setText("")
            }
        }

        addBotMessage("سڵاو! من زیرەکم. بنووسە یان دەستبنێ بۆ دوگمەی مایکرۆفۆن بۆ قسەکردن.")
        checkAccessibilityPermission()
    }

    private fun requestMicPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 100)
        }
    }

    private fun checkAccessibilityPermission() {
        if (ZirakAccessibilityService.instance == null) {
            addBotMessage("تێبینی: بۆ کۆنترۆڵی سیستەم، پێویستە خزمەتگوزاری Accessibility چالاک بکەیت لە سێتینگی مۆبایلەکەت.")
        }
    }

    private fun addUserMessage(text: String) {
        val tv = TextView(this).apply {
            this.text = text
            setPadding(24, 16, 24, 16)
            setBackgroundColor(0xFF2C3E91.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textAlignment = TextView.TEXT_ALIGNMENT_TEXT_END
        }
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = android.view.Gravity.END; setMargins(60, 8, 8, 8) }
        chatContainer.addView(tv, params)
        scrollToBottom()
    }

    private fun addBotMessage(text: String) {
        val tv = TextView(this).apply {
            this.text = text
            setPadding(24, 16, 24, 16)
            setBackgroundColor(0xFFE0E0E0.toInt())
            setTextColor(0xFF000000.toInt())
        }
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = android.view.Gravity.START; setMargins(8, 8, 60, 8) }
        chatContainer.addView(tv, params)
        scrollToBottom()
    }

    private fun scrollToBottom() {
        chatScroll.post { chatScroll.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun handleUserMessage(text: String) {
        addUserMessage(text)

        when {
            text.contains("سێتینگ") -> {
                ZirakAccessibilityService.instance?.openSettings()
                addBotMessage("سێتینگەکانم کردەوە.")
                return
            }
            text.contains("وایفای") -> {
                ZirakAccessibilityService.instance?.openWifiSettings()
                addBotMessage("وایفایم کردەوە.")
                return
            }
            text.contains("گەڕانەوە") || text.contains("ماڵەوە") -> {
                ZirakAccessibilityService.instance?.goHome()
                return
            }
        }

        statusText.text = "زیرەک بیر لێدەکاتەوە..."
        claudeClient.sendMessage(text) { reply, error ->
            runOnUiThread {
                statusText.text = "بڵێ: هەستا"
                if (reply != null) {
                    addBotMessage(reply)
                    speak(reply)
                } else {
                    addBotMessage("هەڵەیەک ڕوویدا: $error")
                }
            }
        }
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotEmpty()) {
                    handleUserMessage(text)
                }
            }
            override fun onReadyForSpeech(params: Bundle?) { statusText.text = "گوێ دەگرم..." }
            override fun onError(error: Int) { statusText.text = "بڵێ: هەستا" }
            override fun onEndOfSpeech() {}
            override fun onBeginningOfSpeech() {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onRmsChanged(rmsdB: Float) {}
        })
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "قسە بکە...")
        }
        speechRecognizer.startListening(intent)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("ar")
        }
    }

    private fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
        tts.shutdown()
    }
}
