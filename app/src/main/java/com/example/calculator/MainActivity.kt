package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.calculator.databinding.ActivityMainBinding

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
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

        binding.ACBtn.setOnClickListener {
            bMathW("0")
            bResuW("0")
        }
//    binding.lParBtn.setOnClickListener  { calc("lPar") }
//    binding.rParBtn.setOnClickListener  { calc("rPar") }
        binding.subBtn.setOnClickListener { simMath("/") }
        binding.multBtn.setOnClickListener { simMath("*") }
        binding.plusBtn.setOnClickListener { simMath("+") }
        binding.minusBtn.setOnClickListener { simMath("-") }
        // TODO: Добавить проверку отрицательного числа перед вычеслениями
        // TODO: Проверить реакцию условий на дополнительный код, для знаков, точки и равно
        // TODO: Реализовать проверку деления на 0
        binding.resultBtn.setOnClickListener {
            try {
                if (simEqual(bMathR().last())) {
                    btnBack()
                    binding.resultText.text = bMathR()
                    addStr("=")
                } else if (bMathR().last() == '.') {
                    btnBack()
                    addStr("=")
                }
                if (bMathR().any { simEqual(it) }) {
                    if (bMathR().last() == '=') btnBack()
                    bMathW(whySoManyZeros(bMathR()))
                    result(bMathR())
                    addStr("=")
                }
            } catch (e: Exception) {
                Log.d("Ошибка Result", "В разделе кнопки =, произошла ошибка: ${e.message} ")
            }

        }
        binding.btnBack.setOnClickListener { btnBack() }
        // TODO: Добавить удаение при долгом нажатие на кнопку Back
        binding.btnPoint.setOnClickListener {
            if (bMathR().last() == '=') {
                bMathW("0.")
            } else if (simEqual(bMathR().last())) {
                addStr("0.")
            } else if (bMathR().all { it != '.' }) {
                addStr(".")
            } else {
                for (i in 0..bMathR().length - 1) {
                    if (bMathR().reversed()[i] == '.') {
                        break
                    } else if (simEqual(bMathR().reversed()[i])) {
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

fun bMathR(): String = binding.mathOperation.text.toString()

fun bResuR(): String = binding.resultText.text.toString()

fun bMathW(str: String) {
    //if (str.last() == '0' && str[str.length - 2] == '.')
    binding.mathOperation.text = str
}

fun bResuW(str: String) {
    binding.resultText.text = whySoManyZeros(str)
}

fun addNum(str: String) {
    if (bMathR().equals("0") || bMathR().last() == '=') {
        bMathW(str)
    } else if (bMathR().last() == '0' && simEqual(bMathR()[bMathR().length - 2])) {
        btnBack()
        addStr(str)
    } else {
        addStr(str)
    }
}

fun simMath(c: String) {
    val aEnd = bMathR().last()
    if (c == "-" && bMathR() == "0") {
        bMathW("-0")
    } else if (aEnd == '=') {
        bMathW(bResuR())
        addStr(c)
    } else if (aEnd == '.') {
        btnBack()
    } else if (!simEqual(bMathR().last())) {
        bMathW(whySoManyZeros(bMathR()) + c)
    } else if (simEqual(aEnd)) {
        btnBack()
        addStr(c)
    }
}

fun whySoManyZeros(str: String): String {
    if (str.any { it == '.' }) {
        if (str.last() == '.') {
            return str.dropLast(1)
        } else if (!str.equals("0") && str.last() == '0') {
            return whySoManyZeros(str.dropLast(1))
        } else {
            return str
        }
    } else {
        return str
    }
}

fun btnBack() {
    if (bMathR().length > 1) bMathW(bMathR().substring(0, bMathR().length - 1)) else bMathW("0")
}

fun simEqual(sim: Char): Boolean = sim == '/' || sim == '*' || sim == '-' || sim == '+'

fun giveMeNum(str: String): Array<String> {
    var arrElement = str.split("/", "*", "-", "+").filter { it.isNotEmpty() }.toTypedArray()
    if (str[0] == '-') arrElement[0] = "-" + arrElement[0]
    return arrElement
}

fun giveMeSimvol(str: String): CharArray {
    if (str[0] == '-') {
        return str.drop(1).filter { simEqual(it) }.toCharArray()
    } else {
        return str.filter { simEqual(it) }.toCharArray()
    }
}

fun result(str: String) {
    val arrNumber = giveMeNum(str)
    val arrSimvol = giveMeSimvol(str)
    for(i in 0..arrSimvol.size-1){
        bResuW(MathEqually(arrNumber[i]))
    }
        /*when (str.filter { simEqual(it) }) {
            "/" -> bResuW(MathEqually())
            "*" -> bResuW(MathEqually())
            "-" -> bResuW(MathEqually())
            "+" -> bResuW(MathEqually())
            else -> bResuW("Ну хз")*/


}

fun MathEqually(numberOne: String, numberTwo: String, sign: String): String {
    when (sign) {
        "/" -> return (numberOne.toFloat() / numberTwo.toFloat()).toString()
        "*" -> return (numberOne.toFloat() * numberTwo.toFloat()).toString()
        "-" -> return (numberOne.toFloat() - numberTwo.toFloat()).toString()
        "+" -> return (numberOne.toFloat() + numberTwo.toFloat()).toString()
        else -> return ("Ну хз")
    }
}

