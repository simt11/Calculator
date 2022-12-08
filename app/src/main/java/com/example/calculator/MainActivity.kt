package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.calculator.databinding.ActivityMainBinding

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
/*    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn0.setOnClickListener { addNum("0") }
        binding.btn1.setOnClickListener { addNum("1") }
        binding.btn2.setOnClickListener { addNum("2") }
        binding.btn3.setOnClickListener { addNum("3") }
        binding.btn4.setOnClickListener { addNum("4") }
        binding.btn5.setOnClickListener { addNum("5") }
        binding.btn6.setOnClickListener { addNum("6") }
        binding.btn7.setOnClickListener { addNum("7") }
        binding.btn8.setOnClickListener { addNum("8") }
        binding.btn9.setOnClickListener { addNum("9") }

        binding.ACBtn.setOnClickListener { binding.mathOperation.text = "0" }
//    binding.lParBtn.setOnClickListener  { calc("lPar") }
//    binding.rParBtn.setOnClickListener  { calc("rPar") }
        binding.subBtn.setOnClickListener { simMath("/") }
        binding.multBtn.setOnClickListener { simMath("*") }
        binding.plusBtn.setOnClickListener { simMath("+") }
        binding.minusBtn.setOnClickListener { simMath("-") }
        binding.resultBtn.setOnClickListener {
            var a = binding.mathOperation.text.toString()
            if (simEqual(a[a.length - 1]) ) { //||
                btnBack()
                a = binding.mathOperation.text.toString()
                binding.resultText.text = binding.mathOperation.text.toString()
                addStr("=")

            } else if (a[a.length - 1] == '.') {
                btnBack()
                a = binding.mathOperation.text.toString()
                addStr("=")
                if (a.all { !simEqual(it) }) result(a)
            }
        }
        binding.btnBack.setOnClickListener { btnBack() }
        binding.btnPoint.setOnClickListener {
            var a = binding.mathOperation.text.toString()
            if (simEqual(a[a.length - 1])) {
                addStr("0.")
            } else if (a.all { it != '.'}){
                addStr(".")
            } else {
                var b = a.reversed()
                for (i in 0..a.length - 1) {
                    if (b[i] == '.') {
                        break
                    }
                    else if (simEqual(b[i])) {
                        addStr(".")
                        break
                    }
                }
            }
        }
    }
}
/*
* Нет точки
* Есть одна точка               ---------
* Есть одна точка и знак и все  ---------
* Есть одна точка и знак
* Есть 2 точки и знак
*
* */

fun addStr(str: String) {
    binding.mathOperation.append(str)
}
fun addNum(str: String) = if (binding.mathOperation.text.toString().equals("0")) binding.mathOperation.text = str else addStr(str)




fun simMath(c: String) {
    var a = binding.mathOperation.text.toString()
    var aEnd = a[a.length - 1]
    if (aEnd == '.') btnBack()
    if (a.all { !simEqual(it) }) addStr(c)
    else if (simEqual(aEnd)) {
        btnBack()
        addStr(c)
    }
}

fun btnBack() {
    var a = binding.mathOperation.text.toString()
    if (a.length>1) binding.mathOperation.text = a.substring(0, a.length - 1) else binding.mathOperation.text = "0"
}
fun simEqual(sim: Char):Boolean = sim == '/' || sim == '*' || sim == '-' || sim == '+'

fun result(str:String){
    var arr = str.split("/".toRegex()).toTypedArray()
    binding.resultText.text = "$arr[0] И $arr[1]"
}

