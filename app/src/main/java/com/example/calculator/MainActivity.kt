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
        binding.resultBtn.setOnClickListener { btnResult() }
        binding.btnDel.setOnClickListener { btnDel() }
        // TODO: Добавить удаление при долгом нажатие на кнопку Back
        binding.btnPoint.setOnClickListener {
            when {
                bMathR().last().equals('=') -> bMathW("0.")
                isSignEqual(bMathR().last()) -> addStr("0.")
                bMathR().all { it != '.' } -> addStr(".")
                else -> {
                    for (i in bMathR().length - 1 downTo 0) {
                        if (bMathR()[i] == '.') {
                            break
                        } else if (isSignEqual(bMathR()[i])) {
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
        if (isSignEqual(bMathR().last())) {
            btnDel()
            binding.resultText.text = bMathR()
            addStr("=")
        } else if (bMathR().last().equals('.')) {
            btnDel()
            addStr("=")
        }
        if (bMathR().any { isSignEqual(it) }) {
            if (bMathR().last().equals('=')) btnDel()
            bMathW(delLastZeros(bMathR()))
            resultMinusPlus(bMathR())
            addStr("=")
        }
    } catch (e: Exception) {
        Log.d("Ошибка Result", "В разделе кнопки =, произошла ошибка: ${e.message} ")
    }
}

fun addStr(str: String) {
    binding.mathOperation.append(str)
}

fun bMathR(): String = binding.mathOperation.text.toString()

fun bResuR(): String = binding.resultText.text.toString()

fun bMathW(str: String) {
    binding.mathOperation.text = str
}

fun bResuW(str: String) {
    binding.resultText.text = delLastZeros(str)
}

fun addNum(str: String) {
    if (bMathR().equals("0") || bMathR().last().equals('=')) {
        bMathW(str)
    } else if (bMathR().last().equals('0') && isSignEqual(bMathR()[bMathR().length - 2])) {
        btnDel()
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
        btnDel()
    } else if (!isSignEqual(bMathR().last())) {
        bMathW(delLastZeros(bMathR()) + c)
    } else if (isSignEqual(aEnd)) {
        btnDel()
        addStr(c)
    }
}

fun delLastZeros(str: String): String {
    if (str.any { it == '.' }) {
        if (str.last().equals('.')) {
            return str.dropLast(1)
        } else if (!str.equals("0") && str.last().equals('0')) {
            return delLastZeros(str.dropLast(1))
        } else {
            return str
        }
    } else {
        return str
    }
}

fun btnDel() {
    if (bMathR().length > 1) bMathW(bMathR().substring(0, bMathR().length - 1)) else bMathW("0")
}

fun isSignEqual(sim: Char): Boolean = sim == '/' || sim == '*' || sim == '-' || sim == '+'

fun giveMeNumMinusPlus(str: String): Array<String> {
    var arrElement = str.split("-", "+").filter { it.isNotEmpty() }.toTypedArray()
    if (str[0] == '-') arrElement[0] = "-" + arrElement[0]
    return arrElement
}

fun giveMeNumMulSub(str: String): Array<String> {
    var arrElement = str.split("/", "*").filter { it.isNotEmpty() }.toTypedArray()
    return arrElement
}

fun giveMeNumParenthesis(str: String): Array<String> {
    var arrElement = str.split("(", ")").filter { it.isNotEmpty() }.toTypedArray()
    return arrElement
}

fun giveMeSimMulSub(str: String): CharArray {
    if (str[0] == '-') {
        return str.drop(1).filter { it == '/' || it == '*' }.toCharArray()
    } else {
        return str.filter { it == '/' || it == '*' }.toCharArray()
    }
}

fun giveMeSimMinusPlus(str: String): CharArray {
    if (str[0] == '-') {
        return str.drop(1).filter { it == '-' || it == '+' }.toCharArray()
    } else {
        return str.filter { it == '-' || it == '+' }.toCharArray()
    }
}

fun parenthesisSplit(str: String) {
    val arrNumber = giveMeNumParenthesis(str)
    val arrSimvol = giveMeSimMinusPlus(str)
    var value = arrNumber[0]
    for (i in 1..arrSimvol.size) {
        if (arrNumber[i].any { it == '/' || it == '*' }) {
            arrNumber[i] = resultMulSub(arrNumber[i])
        }
        value = MathEqually(value, arrNumber[i], arrSimvol[i - 1])
    }
    bResuW(value)
}

fun resultMinusPlus(str: String) {
    val arrNumber = giveMeNumMinusPlus(str)
    val arrSimvol = giveMeSimMinusPlus(str)
    var value = arrNumber[0]
    if (arrNumber.size == 1 && value.any { it == '/' || it == '*' }) {
        value = resultMulSub(value)
    } else {
        for (i in 1..arrSimvol.size) {
            if (arrNumber[i].any { it == '/' || it == '*' }) {
                arrNumber[i] = resultMulSub(arrNumber[i])
            }
            value = MathEqually(value, arrNumber[i], arrSimvol[i - 1])
        }
    }
    bResuW(value)
}

fun resultMulSub(str: String): String {
    val arrNumber = giveMeNumMulSub(str)
    val arrSimvol = giveMeSimMulSub(str)
    var value = arrNumber[0]
    for (i in 1..arrSimvol.size) {
        value = MathEqually(value, arrNumber[i], arrSimvol[i - 1])
    }
    return value
}

fun MathEqually(numberOne: String, numberTwo: String, sign: Char): String {
    when (sign) {
        '/' -> return (numberOne.toDouble() / numberTwo.toDouble()).toString()
        '*' -> return (numberOne.toDouble() * numberTwo.toDouble()).toString()
        '-' -> return (numberOne.toDouble() - numberTwo.toDouble()).toString()
        '+' -> return (numberOne.toDouble() + numberTwo.toDouble()).toString()
        else -> return ("Ну хз")
    }
}



