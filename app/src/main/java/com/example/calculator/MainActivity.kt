package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.calculator.databinding.ActivityMainBinding
import kotlin.system.measureTimeMillis

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
//        binding.lParBtn.setOnClickListener  { createSign("(") }
//        binding.rParBtn.setOnClickListener  { createSign(")") }
        binding.subBtn.setOnClickListener { createSign("/") }
        binding.multBtn.setOnClickListener { createSign("*") }
        binding.plusBtn.setOnClickListener { createSign("+") }
        binding.minusBtn.setOnClickListener { createSign("-") }
        binding.resultBtn.setOnClickListener { btnResult() }
        binding.btnDel.setOnClickListener { btnDel() }
        // TODO: Добавить удаление при долгом нажатие на кнопку Back

        binding.btnPoint.setOnClickListener { buttonPoint() }
    }
}

private fun buttonPoint() {
    val text = getMathText()
    when {
        text.last().equals('=') -> setMathText("0.")
        isSignEqual(text.last()) -> addStr("0.")
        text.all { !it.equals('.') } -> addStr(".")
        else -> {
            for (i in text.length - 1 downTo 0) {
                if (text[i].equals('.')) {
                    break
                } else if (isSignEqual(text[i])) {
                    addStr(".")
                    break
                }
            }
        }
    }
}

private fun isDivisionZero(str: String): Boolean {
    return when {
        str.length < 2 -> false
        str.subSequence(str.length - 2, str.length) == "/0" -> true
        else -> false
    }
}

private fun btnResult() {
    var text = delLastZeros(getMathText())
    when {
        text.last() == '=' -> false
        else -> try {
            if (isSignEqual(text.last())) {
                text = text.dropLast(1)
                btnDel()
                addStr("=")
            } else if (text.last().equals('.')) {
                text = text.dropLast(1)
                btnDel()
                addStr("=")
            }
            when {
                isDivisionZero(text) -> setResultText("Попытка деления на 0!")
                text.any { isSignEqual(it) } -> {
                    setMathText(text + "=")
                    setResultText(resultMath(text, '-', '+'))
                }
            }
        } catch (e: Exception) {
            Log.d("Ошибка Result", "В разделе кнопки =, произошла ошибка: ${e.message} ")
        }
    }
}

private fun addStr(str: String) {
    binding.mathOperation.append(str)
}

private fun getMathText(): String = binding.mathOperation.text.toString()

private fun getResultText(): String = binding.resultText.text.toString()

private fun setMathText(str: String) {
    binding.mathOperation.text = str
}

private fun setResultText(str: String) {
    binding.resultText.text = delLastZeros(str)
}

private fun addNum(str: String) {
    val text = getMathText()
    when {
        text.equals("0") || text.last().equals('=') -> setMathText(str)
        text.last().equals('0') && isSignEqual(text[text.length - 2]) -> {
            btnDel()
            addStr(str)
        }
        else -> addStr(str)
    }
}

private fun createSign(c: String) {
    val text = getMathText()
    val aEnd = text.last()
    when {
        isDivisionZero(text) -> setResultText("Попытка деления на 0!")
        aEnd.equals('=') -> {
            setMathText(getResultText())
            addStr(c)
        }
        aEnd.equals('.') -> btnDel()
        !isSignEqual(aEnd) -> setMathText(delLastZeros(text) + c)
        isSignEqual(aEnd) -> {
            btnDel()
            addStr(c)
        }
    }
}

private fun delLastZeros(str: String): String {
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

private fun btnDel() {
    val text = getMathText()
    if (text.length > 1) setMathText(
        text.substring(
            0, text.length - 1
        )
    ) else setMathText("0")
}

val signMap = setOf('/', '*', '-', '+')

val signMulSubMap = setOf('/', '*')

private fun isSignEqual(sign: Char): Boolean = sign in signMap

private fun getNumberArray(str: String, signOne: Char, signTwo: Char): Array<String> =
    str.split(signOne, signTwo).filter { it.isNotEmpty() }.toTypedArray()

private fun getSign(str: String, signOne: Char, signTwo: Char): String {
    return when {
        str.first().equals('-') -> str.drop(1).filter { it.equals(signOne) || it == signTwo }
        else -> str.filter { it.equals(signOne) || it == signTwo }
    }
}

private fun resultMath(str: String, signOne: Char, signTwo: Char): String {
    val arrNumber = getNumberArray(str, signOne, signTwo)
    if (str.first().equals('-')) arrNumber[0] = "-" + arrNumber[0]
    val arrSimvol = getSign(str, signOne, signTwo)
    var value = arrNumber.first()
    if (value.any { it in signMulSubMap }) value = resultMath(value, '/', '*')
    for (i in 1..arrSimvol.length) {
        if (arrNumber[i].any { it in signMulSubMap }) {
            arrNumber[i] = resultMath(arrNumber[i], '/', '*')
        }
        value = mathEqually(value, arrNumber[i], arrSimvol[i - 1])
    }
    return value
}

private fun mathEqually(numberOne: String, numberTwo: String, sign: Char): String {
    return when (sign) {
        '/' -> (numberOne.toDouble() / numberTwo.toDouble()).toString()
        '*' -> (numberOne.toDouble() * numberTwo.toDouble()).toString()
        '-' -> (numberOne.toDouble() - numberTwo.toDouble()).toString()
        '+' -> (numberOne.toDouble() + numberTwo.toDouble()).toString()
        else -> ("Ну хз")
    }
}