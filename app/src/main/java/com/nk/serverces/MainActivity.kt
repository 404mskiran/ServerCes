package com.nk.serverces

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.nk.serverces.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.io.PrintWriter
import java.net.Socket
import kotlin.concurrent.thread

val dataToSend = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 12)

val maxdata = intArrayOf(190, 170, 110, 110, 5, 5, 5, 5, 6, 5000, 11, 12)

var auto = true

class MainActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main)

    private lateinit var binding: ActivityMainBinding

    val TAG = "hihi"

    val driveMode = charArrayOf('P','R','N','D','L')

    var drivePointer = 0
    var screenPointer =1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        //printValuesForever()
        thread { sendValues() }

        binding.FuelSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update the TextView with the current progress
                dataToSend[2]=progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Called when user starts touching the SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Called when user stops touching the SeekBar
            }
        })

        binding.tempSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update the TextView with the current progress
               dataToSend[3]=progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Called when user starts touching the SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Called when user stops touching the SeekBar
            }
        })

        binding.SpeedSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update the TextView with the current progress
                dataToSend[0]=(progress*180)/100
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Called when user starts touching the SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Called when user stops touching the SeekBar
            }
        })

        binding.RpmSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update the TextView with the current progress
                dataToSend[1]=(progress*160)/100
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Called when user starts touching the SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Called when user stops touching the SeekBar
            }
        })

        binding.Li.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
               dataToSend[4]=1
            } else {
                dataToSend[4]=0
            }
        }

        binding.Ri.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                dataToSend[5]=1
            } else {
                dataToSend[5]=0
            }
        }

        binding.park.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                dataToSend[6]=1
            } else {
                dataToSend[6]=0
            }
        }

        binding.seatBelt.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                dataToSend[7]=1
            } else {
                dataToSend[7]=0
            }
        }

        binding.Data.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                auto = true;
                printValuesForever();
            }
            else auto = false
        }

        binding.DriveSwitch.setOnClickListener {

            if (drivePointer<5){
                binding.DriveSwitch.setText(driveMode[drivePointer].toString())
                dataToSend[8]=drivePointer
                drivePointer++
                if (drivePointer==5) drivePointer =0
            }

        }

        binding.whichScreen.setOnClickListener {

            if(screenPointer<=4){

                if (screenPointer==4) screenPointer=1
                dataToSend[11]= screenPointer
                binding.whichScreen.setText("Screen :$screenPointer")
                screenPointer++
            }

        }

        fun updatespeedRpm(speed :Int , rpm : Int){
            binding.Speed.setText(speed.toString())
            var rpp = rpm/20
            binding.Rpm.setText(rpp.toString())
        }

        scope.launch {
            while (isActive) {
                // Simulate updating the global variable
                delay(16) // Simulated update interval, in milliseconds
                updatespeedRpm(dataToSend[0], dataToSend[1])
            }
        }

    }


    fun sendValues() {
        val TAG = "YourTag"
        val SERVER_IP = "127.0.0.1"
        val SERVER_PORT = 12456
        val DELAY_MILLIS = 16
        while (true) {
            try {
                val client = Socket(SERVER_IP, SERVER_PORT)
                val writer = PrintWriter(client.getOutputStream(), true)
                while(true) {
                    val dataString = dataToSend.joinToString(",")
                    writer.println(dataString)
                    Log.i(TAG, "Data sent: $dataString")
                    Thread.sleep(DELAY_MILLIS.toLong())
                }
                // Close the client socket when done
                client.close()


            } catch (e: Exception) {
                Log.e(TAG, "sendValues: ${e.message}")
                Thread.sleep(500)
                // Handle the exception, e.g., logging, error reporting, or retry logic
            }
        }
    }


    fun printValuesForever() {
        thread {
            var value = 0.00f
            var increasing = true

            while (auto) {
                    dataToSend[0]= (maxdata[0]* value).toInt()

                    dataToSend[1]= (maxdata[0]* value).toInt()

                if (increasing) {
                    value += 0.001f
                    if (value > 1.0f) {
                        value = 1.0f
                        increasing = false
                    }
                } else {
                    value -= 0.001f
                    if (value < 0.0f) {
                        value = 0.00f
                        increasing = true
                    }
                }
                Thread.sleep(5)
            }
        }
    }
}