package com.nk.serverces

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.getSystemService
import com.nk.serverces.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.io.PrintWriter
import java.net.Socket
import kotlin.concurrent.thread

val dataToSend = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,0.0f)

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




        showInputDialog(this)

        binding.FuelSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update the TextView with the current progress
                dataToSend[2]=progress.toFloat()
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
               dataToSend[3]=progress.toFloat()
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
                dataToSend[0]=(progress*1.8).toFloat()
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
                dataToSend[1]=(progress*1.6).toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Called when user starts touching the SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Called when user stops touching the SeekBar
            }
        })

        binding.li.setOnClickListener {

            if (binding.li.alpha==1.0f) {

                dataToSend[4]=0.0f
                binding.li.alpha=0.2f
            } else {
                dataToSend[4]=1.0f
                binding.li.alpha=1.0f
            }

        }


        binding.ri.setOnClickListener {
            if (binding.ri.alpha==1.0f) {

                dataToSend[5]=0.0f
                binding.ri.alpha=0.2f
            } else {
                dataToSend[5]=1.0f
                binding.ri.alpha=1.0f
            }
        }

        binding.parkBreak.setOnClickListener {

            if (binding.parkBreak.alpha==1.0f) {

                dataToSend[6]=0.0f
                binding.parkBreak.alpha=0.2f
            } else {
                dataToSend[6]=1.0f
                binding.parkBreak.alpha=1.0f
            }
        }

        binding.seatBelt.setOnClickListener {
            if (binding.seatBelt.alpha==1.0f) {

                dataToSend[7]=0.0f
                binding.seatBelt.alpha=0.2f
            } else {
                dataToSend[7]=1.0f
                binding.seatBelt.alpha=1.0f
            }
        }

        binding.breakFail.setOnClickListener {
            if (binding.breakFail.alpha==1.0f) {

                dataToSend[12]=0.0f
                binding.breakFail.alpha=0.2f
            } else {
                dataToSend[12]=1.0f
                binding.breakFail.alpha=1.0f
            }
        }
        binding.doorOpen.setOnClickListener {
            if (binding.doorOpen.alpha==1.0f) {

                dataToSend[10]=0.0f
                binding.doorOpen.alpha=0.2f
            } else {
                dataToSend[10]=1.0f
                binding.doorOpen.alpha=1.0f
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
                dataToSend[8]=drivePointer.toFloat()
                drivePointer++
                if (drivePointer==5) drivePointer =0
            }

        }

        binding.whichScreen.setOnClickListener {

            if(screenPointer<=4){

                if (screenPointer==4) screenPointer=1
                dataToSend[11]= screenPointer.toFloat()
                binding.whichScreen.setText("Screen :$screenPointer")
                screenPointer++

            }

        }

        binding.restartThread.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        fun updatespeedRpm(speed :Float , rpm : Float){
            binding.Speed.setText(speed.toInt().toString())
            var rpp = rpm/20
            binding.Rpm.setText(rpp.toInt().toString())
        }

        scope.launch {
            while (isActive) {
                // Simulate updating the global variable
                delay(16) // Simulated update interval, in milliseconds
                updatespeedRpm(dataToSend[0], dataToSend[1])
            }
        }

    }
    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onRestart() {
        super.onRestart()
    }

    private fun showInputDialog(context: Context) {
        val inputEditText = EditText(context)
        inputEditText.hint = "Enter IP"
        inputEditText.setText("localhost")

        val dialog = AlertDialog.Builder(context)
            .setTitle("Enter Text")
            .setView(inputEditText)
            .setPositiveButton("OK") { _, _ ->

                val inputText = inputEditText.text.toString()
                sendValues(inputText)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()

        dialog.show()
    }



    fun sendValues(ip : String) {
       thread {  val TAG = "YourTag"
           Log.i(TAG, "server ip : $ip")
           val SERVER_IP = ip
           val SERVER_PORT = 12456
           val DELAY_MILLIS = 10
           while (true) {
               try {
                   val client = Socket(SERVER_IP, SERVER_PORT)
                   val writer = PrintWriter(client.getOutputStream(), true)
                   while(!client.isClosed) {
                       try {
                           val dataString = dataToSend.joinToString(",")
                           writer.println(dataString)
                           Log.i(TAG, "Data sent: $dataString")
                           Thread.sleep(DELAY_MILLIS.toLong())
                       }
                       catch (e:Exception){
                           Log.i(TAG, "sendValues: ${e.message}")

                       }
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
    }




    fun printValuesForever() {
        thread {
            var value = 0.000f
            var increasing = true

            while (auto) {
                    dataToSend[0]= (maxdata[0]* value)

                    dataToSend[1]= (maxdata[0]* value)

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
                Thread.sleep(10)
            }
        }
    }
}