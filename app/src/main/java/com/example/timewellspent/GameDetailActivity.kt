package com.example.timewellspent

import android.R
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.example.timewellspent.databinding.ActivityGameDetailBinding
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.NonCancellable.start
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class GameDetailActivity : AppCompatActivity() {

    companion object {
        val TAG = "GameDetailActivity"
        val EXTRA_GAME_ENTRY = "game entry"
    }

    private lateinit var binding: ActivityGameDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGameDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val gameEntry = intent.getParcelableExtra<GameEntry>(EXTRA_GAME_ENTRY) ?: GameEntry()

        binding.editTextGameDetailName.setText(gameEntry.name)
        binding.editTextGameDetailMoneySpent.setText("${gameEntry.moneySpent/100}.${gameEntry.moneySpent%100}")
        binding.sliderGameDetailTimeSpent.value = gameEntry.elapsedTime.toFloat()
        val format: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        binding.textViewGameDetailDate.text = format.format(gameEntry.datePlayed)

        var spinnerItems = GameEntry.EMOTION.entries.map { Pair(it.emoji, it.name) }
        binding.spinnerGameDetailEmotion.adapter =
            ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, spinnerItems.map { it.first })
        Log.d(TAG, "onCreate: $gameEntry  ${gameEntry.emotion} ${ spinnerItems.map { it.second }.indexOf(gameEntry.emotion)}")
        var position = spinnerItems.map { it.second }.indexOf(gameEntry.emotion)
        if(position < 0) {
            position = 0
        }
        binding.spinnerGameDetailEmotion.setSelection(position)

        binding.textViewGameDetailDate.setOnClickListener {
            val selection = binding.textViewGameDetailDate.text.toString()
            val date: Date = format.parse(selection)
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setSelection(date.time) // requires milliseconds
                .setTitleText("Select a Date")
                .build()

            datePicker.addOnPositiveButtonClickListener { millis ->
                val newDate = Date(millis+24*60*60*1000)
                binding.textViewGameDetailDate.setText(format.format(newDate))
            }

            datePicker.show(supportFragmentManager,"date picker")

        }

        binding.buttonGameDetailSave.setOnClickListener {
            gameEntry.ownerId = Backendless.UserService.CurrentUser().userId
            gameEntry.name = binding.editTextGameDetailName.text.toString()
            // for money spent, remove the leading $
            val moneyString = binding.editTextGameDetailMoneySpent.text.toString()
            var cents = 0
            if(!moneyString.contains(".")) {
                cents = moneyString.toInt() * 100
            } else {
                var dollarsAndCents = moneyString.split(".")
                var dollarString = dollarsAndCents[0]
                var centsString = dollarsAndCents[1]
                if(dollarString.isNotEmpty()) {
                    cents += dollarString.toInt() * 100
                }
                if(centsString.isNotEmpty()) {
                    if(centsString.length > 2) {
                        centsString = centsString.substring(0,2)
                    } else if(centsString.length == 1) {
                        centsString += "0"
                    }
                    cents += centsString.toInt()
                }
            }
            gameEntry.moneySpent = cents
            gameEntry.elapsedTime = binding.sliderGameDetailTimeSpent.value.toInt()
            gameEntry.datePlayed =  format.parse(binding.textViewGameDetailDate.text.toString()) ?: Date()
            gameEntry.emotion =  GameEntry.EMOTION.entries.find { it.emoji == binding.spinnerGameDetailEmotion.selectedItem.toString()}!!.name
            saveToBackendless(gameEntry)
            finish()
        }
    }



    private fun saveToBackendless(gameEntry: GameEntry) {
        // code here to save to backendless
        Backendless.Data.of(GameEntry::class.java).save(gameEntry, object : AsyncCallback<GameEntry?> {
            override fun handleResponse(response: GameEntry?) {
                // new Contact instance has been saved
                Log.d(TAG, "handleResponse: Saved to Backendless")
            }

            override fun handleFault(fault: BackendlessFault) {
                // an error has occurred, the error code can be retrieved with fault.getCode()
                Log.d(TAG, "handleFault: ${fault.message}")
            }
        })
    }




}