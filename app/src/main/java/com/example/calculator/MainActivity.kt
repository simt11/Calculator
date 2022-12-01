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
            setMathText("0")
            setResultText("0")
        }
//    binding.lParBtn.setOnClickListener  { calc("lPar") }
//    binding.rParBtn.setOnClickListener  { calc("rPar") }
        binding.subBtn.setOnClickListener { createSign("/") }
        binding.multBtn.setOnClickListener { createSign("*") }
        binding.plusBtn.setOnClickListener { createSign("+") }
        binding.minusBtn.setOnClickListener { createSign("-") }
        // TODO: Реализовать проверку деления на 0
        binding.resultBtn.setOnClickListener { btnResult() }
        binding.btnDel.setOnClickListener { btnDel() }
        // TODO: Добавить удаление при долгом нажатие на кнопку Back
        binding.btnPoint.setOnClickListener {
            when {
                getMathText().last().equals('=') -> setMathText("0.")
                isSignEqual(getMathText().last()) -> addStr("0.")
                getMathText().all { !it.equals('.') } -> addStr(".")
                else -> {
                    for (i in getMathText().length - 1 downTo 0) {
                        if (getMathText()[i].equals('.')) {
                            break
                        } else if (isSignEqual(getMathText()[i])) {
                            addStr(".")
                            break
                        }
                    }
                }
            }
        }
    }
}

fun btnResult() {
    try {
        if (isSignEqual(getMathText().last())) {
            btnDel()
            binding.resultText.text = getMathText()
            addStr("=")
        } else if (getMathText().last().equals('.')) {
            btnDel()
            addStr("=")
        }
        if (getMathText().any { isSignEqual(it) }) {
            if (getMathText().last().equals('=')) btnDel()
            setMathText(delLastZeros(getMathText()))
            resultMinusPlus(getMathText())
            addStr("=")
        }
    } catch (e: Exception) {
        Log.d("Ошибка Result", "В разделе кнопки =, произошла ошибка: ${e.message} ")
    }
}

fun addStr(str: String) {
    binding.mathOperation.append(str)
}

fun getMathText(): String = binding.mathOperation.text.toString()

fun getResultText(): String = binding.resultText.text.toString()

fun setMathText(str: String) {
    binding.mathOperation.text = str
}

fun setResultText(str: String) {
    binding.resultText.text = delLastZeros(str)
}

fun addNum(str: String) {
    when {
        getMathText().equals("0") || getMathText().last().equals('=') -> setMathText(str)
        getMathText().last().equals('0') && isSignEqual(getMathText()[getMathText().length - 2]) -> {
            btnDel()
            addStr(str)
        }
        else -> addStr(str)
    }
}



fun createSign(c: String) {
    val aEnd = getMathText().last()
    when {
        c.equals("-") && getMathText().equals("0") -> setMathText("-0")
        aEnd.equals('=') -> {
            setMathText(getResultText())
            addStr(c)
        }
        aEnd.equals('.') -> btnDel()
        !isSignEqual(getMathText().last()) -> setMathText(delLastZeros(getMathText()) + c)
        isSignEqual(aEnd) -> {
            btnDel()
            addStr(c)
        }
    }
}

fun delLastZeros(str: String): String {
    return when {
        str.any { it.equals('.') } -> {
            when {
                str.last().equals('.') -> str.dropLast(1)
                !str.equals("0") && str.last().equals('0') -> delLastZeros(str.dropLast(1))
                else -> str
            }
        }
        else -> str
    }
}

fun btnDel() {
    if (getMathText().length > 1) setMathText(
        getMathText().substring(
            0,
            getMathText().length - 1
        )
    ) else setMathText("0")
}

fun isSignEqual(sim: Char): Boolean =
    sim.equals('/') || sim.equals('*') || sim.equals('-') || sim.equals('+')

fun getNumMinusPlus(str: String): Array<String> {
    var arrElement = str.split("-", "+").filter { it.isNotEmpty() }.toTypedArray()
    if (str.first().equals('-')) arrElement[0] = "-" + arrElement[0]
    return arrElement
}

fun getNumMulSub(str: String): Array<String> =
    str.split("/", "*").filter { it.isNotEmpty() }.toTypedArray()

fun getNumParenthesis(str: String): Array<String> =
    str.split("(", ")").filter { it.isNotEmpty() }.toTypedArray()

fun getSimMulSub(str: String): CharArray {
    return when {
        str.first().equals('-') -> str.drop(1).filter { it == '/' || it == '*' }.toCharArray()
        else -> str.filter { it == '/' || it == '*' }.toCharArray()
    }
}

fun getSimMinusPlus(str: String): CharArray {
    return when {
        str.first().equals('-') -> str.drop(1).filter { it.equals('-') || it == '+' }.toCharArray()
        else -> str.filter { it.equals('-') || it == '+' }.toCharArray()
    }
}

fun parenthesisSplit(str: String) {
    val arrNumber = getNumParenthesis(str)
    val arrSimvol = getSimMinusPlus(str)
    var value = arrNumber.first()
    for (i in 1..arrSimvol.size) {
        if (arrNumber[i].any { it == '/' || it == '*' }) {
            arrNumber[i] = resultMulSub(arrNumber[i])
        }
        value = mathEqually(value, arrNumber[i], arrSimvol[i - 1])
    }
    setResultText(value)
}

fun resultMinusPlus(str: String) {
    val arrNumber = getNumMinusPlus(str)
    val arrSimvol = getSimMinusPlus(str)
    var value = arrNumber.first()
    if (arrNumber.size == 1 && value.any { it == '/' || it == '*' }) {
        value = resultMulSub(value)
    } else {
        for (i in 1..arrSimvol.size) {
            if (arrNumber[i].any { it == '/' || it == '*' }) {
                arrNumber[i] = resultMulSub(arrNumber[i])
            }
            value = mathEqually(value, arrNumber[i], arrSimvol[i - 1])
        }
    }
    setResultText(value)
}

fun resultMulSub(str: String): String {
    val arrNumber = getNumMulSub(str)
    val arrSimvol = getSimMulSub(str)
    var value = arrNumber.first()
    for (i in 1..arrSimvol.size) {
        value = mathEqually(value, arrNumber[i], arrSimvol[i - 1])
    }
    return value
}

fun mathEqually(numberOne: String, numberTwo: String, sign: Char): String {
    return when (sign) {
        '/' -> (numberOne.toDouble() / numberTwo.toDouble()).toString()
        '*' -> (numberOne.toDouble() * numberTwo.toDouble()).toString()
        '-' -> (numberOne.toDouble() - numberTwo.toDouble()).toString()
        '+' -> (numberOne.toDouble() + numberTwo.toDouble()).toString()
        else -> ("Ну хз")
    }
}



