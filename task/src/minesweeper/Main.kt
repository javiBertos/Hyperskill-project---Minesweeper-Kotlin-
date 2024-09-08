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

    private fun calculateSurroundingMines(coords: List<Int>): String {
        val rowIni = if (coords[0] == 0) 0 else coords[0] - 1
        val rowEnd = if (coords[0] == this.rows - 1) this.rows - 1 else coords[0] + 1

        val colIni = if (coords[1] == 0) 0 else coords[1] - 1
        val colEnd = if (coords[1] == this.columns - 1) this.columns - 1 else coords[1] + 1

        var minesCounter = 0

        for (r in rowIni..rowEnd) {
            for (c in colIni..colEnd) {
                if (this.minesLocation.indexOf(listOf(r, c)) != -1) {
                    minesCounter++
                }
            }
        }

        return if (minesCounter == 0) this.safeCell else minesCounter.toString()
    }

    private fun getCellStatus(coords: List<Int>): String {
        val thereIsMineHere = this.minesLocation.indexOf(coords) != -1

        if (thereIsMineHere) {
            return this.mineCell
        }

        return this.calculateSurroundingMines(coords)
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
