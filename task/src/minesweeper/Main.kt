package minesweeper

import kotlin.random.Random

class MinesWeeper (val rows: Int = 9, val columns: Int = 9, val mines: Int = 10) {
    private val mineCell = "X"
    private val safeCell = "."
    private val minesLocation: MutableList<List<Int>> = mutableListOf()

    init {
        this.setInitMinesOnBoard()
    }

    private fun setInitMinesOnBoard() {
        do {
            val newMine = listOf(Random.nextInt(rows - 1), Random.nextInt(columns - 1))

            if (this.minesLocation.indexOf(newMine) == -1) {
                this.minesLocation.add(newMine)
            }
        } while (this.minesLocation.size < this.mines)
    }

    private fun getCellStatus(coords: List<Int>): String {
        return if (this.minesLocation.indexOf(coords) == -1) this.safeCell else this.mineCell
    }

    fun drawBoard() {
        for (row in 0 until this.rows) {
            for (col in 0 until this.columns) {
                print(this.getCellStatus(listOf(row, col)))
            }
            println()
        }
    }
}

fun main() {
    println("How many mines do you want on the field?")
    val userInputString = readln()
    val userMinesInput = if (userInputString.toIntOrNull() != null) userInputString.toInt() else 10

    val minesWeeper = MinesWeeper(mines = userMinesInput)

    minesWeeper.drawBoard()
}
