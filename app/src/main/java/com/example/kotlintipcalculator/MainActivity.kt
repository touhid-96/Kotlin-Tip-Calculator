package com.example.kotlintipcalculator

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15

class MainActivity : AppCompatActivity() {
    private lateinit var billAmountET: EditText
    private lateinit var seekBar: SeekBar
    private lateinit var tipPercentageTV: TextView
    private lateinit var tipTV: TextView
    private lateinit var totalTV: TextView
    private lateinit var tipDescriptionTV: TextView
    private lateinit var splitByET: EditText
    private lateinit var billPerEachTV: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        billAmountET = findViewById(R.id.bill_amount_edittext)
        tipPercentageTV = findViewById(R.id.tip_percentage_textview)
        seekBar = findViewById(R.id.percentage_seekbar)
        tipTV = findViewById(R.id.tip_textview)
        totalTV = findViewById(R.id.total_textview)
        tipDescriptionTV = findViewById(R.id.tip_description_textview)
        splitByET = findViewById(R.id.split_by_edittext)
        splitByET.inputType = InputType.TYPE_CLASS_NUMBER
        billPerEachTV = findViewById(R.id.bill_per_each_textview)

        /*-------------------------default value-----------------------------------*/
        seekBar.progress = INITIAL_TIP_PERCENT  //setting a default value on seekbar
        tipPercentageTV.text = "$INITIAL_TIP_PERCENT%"  //setting the default value in percentage textview
        updateTipDescription(INITIAL_TIP_PERCENT) //setting the default description sync with seekbar & percentage
        /*-------------------------default value-----------------------------------*/

        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                //Log.i(TAG, "onProgressChanged $progress")   //logcat debug
                tipPercentageTV.text = "$progress%"
                computeTipAndTotal()
                updateTipDescription(progress)
                computeBillSplit()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        billAmountET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                //Log.i(TAG, "afterTextChanged $p0")   //logcat debug
                computeTipAndTotal()
                computeBillSplit()
            }
        })

        splitByET.addTextChangedListener(object  : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                computeBillSplit()
            }

        })
    }

    private fun computeTipAndTotal() {
        var bill = 0.0
        if(billAmountET.text.isEmpty()) {
            bill = 0.0
        } else {
            bill = billAmountET.text.toString().toDouble()
        }
        val tipPercent = seekBar.progress

        val tipAmount = bill * tipPercent / 100
        tipTV.text = "%.2f".format(tipAmount)

        val totalAmount = bill + tipAmount
        totalTV.text = "%.2f".format(totalAmount)
    }

    private fun updateTipDescription(tipPercent : Int) {
        val tipDescription = when (tipPercent) {
            in 0..10 -> "Poor"
            in 11..20 -> "Good"
            in 21..25 -> "Great"
            else -> "Amazing"
        }

        tipDescriptionTV.text = tipDescription

        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBar.max,
            ContextCompat.getColor(this, R.color.worst_tip),
            ContextCompat.getColor(this, R.color.best_tip)
        ) as Int

        tipDescriptionTV.setTextColor(color)
    }

    private fun computeBillSplit() {
        val totalAmount = totalTV.text.toString().toDouble()
        var devideBy = 1
        if(splitByET.text.isEmpty()) {
            devideBy = 1
        } else {
            if (splitByET.text.toString().toInt() == 0) {
                devideBy = 1
            } else {
                devideBy = splitByET.text.toString().toInt()
            }
        }

        val billPerEachPerson = totalAmount / devideBy
        billPerEachTV.text = billPerEachPerson.roundToInt().toString()
    }
}