package minesweeper

import java.util.Scanner
import kotlin.random.Random

class MinesWeeper (val rows: Int = 9, val columns: Int = 9, val mines: Int = 10) {
    private val mineCell = "X"
    private val safeCell = "."
    private val markedCell = "*"
    private val minesLocation: MutableList<List<Int>> = mutableListOf()
    private val cellsMarked: MutableList<List<Int>> = mutableListOf()

    init {
        this.setInitMinesOnBoard()
    }

    private fun setInitMinesOnBoard() {
        do {
            val newMine = listOf(Random.nextInt(1, rows), Random.nextInt(1, columns))

            if (this.minesLocation.indexOf(newMine) == -1) {
                this.minesLocation.add(newMine)
            }
        } while (this.minesLocation.size < this.mines)
    }

    private fun calculateSurroundingMines(cords: List<Int>): String {
        val rowIni = if (cords[1] == 1) 1 else cords[1] - 1
        val rowEnd = if (cords[1] == this.rows) this.rows else cords[1] + 1

        val colIni = if (cords[0] == 1) 1 else cords[0] - 1
        val colEnd = if (cords[0] == this.columns) this.columns else cords[0] + 1

        var minesCounter = 0

        for (r in rowIni..rowEnd) {
            for (c in colIni..colEnd) {
                if (this.minesLocation.indexOf(listOf(c, r)) != -1) {
                    minesCounter++
                }
            }
        }

        return if (minesCounter == 0) this.safeCell else minesCounter.toString()
    }

    private fun getCellStatus(cords: List<Int>): String {
        if (this.minesLocation.indexOf(cords) != -1 || this.cellsMarked.indexOf(cords) != -1) {
            return if (this.cellsMarked.indexOf(cords) != -1) this.markedCell else this.safeCell
        }

        return this.calculateSurroundingMines(cords)
    }

    fun drawBoard() {
        for (row in 0..this.rows) {
            for (col in 0..this.columns) {
                if (col == 0) {
                    print("${if (row == 0) " " else row}|")
                    continue
                }

                if (row == 0) {
                    print(col)
                } else {
                    print(this.getCellStatus(listOf(col, row)))
                }

                if (col == this.columns) {
                    print("|")
                }
            }
            println()

            if (row == 0 || row == this.rows) {
                println("-|${"-".repeat(this.columns)}|")
            }
        }
    }

    private fun allAndOnlyMinesAreMarked(): Boolean {
        var totalMatch = false

        if (this.minesLocation.size == this.cellsMarked.size) {
            return this.minesLocation.containsAll(this.cellsMarked)
        }

        return totalMatch
    }

    fun markCellAsMined(cords: List<Int>): Boolean {
        if (this.minesLocation.indexOf(cords) == -1 && this.calculateSurroundingMines(cords) != this.safeCell) {
            throw Exception("There is a number here!")
        }

        if (this.cellsMarked.indexOf(cords) != -1) {
            this.cellsMarked.remove(cords)
        } else {
            this.cellsMarked.add(cords)
        }

        return this.allAndOnlyMinesAreMarked()
    }
}

fun main() {
    // Init inputs reader
    val reader = Scanner(System.`in`)

    println("How many mines do you want on the field?")
    val userMinesInput = reader.nextInt()
    var gameFinished = false

    val minesWeeper = MinesWeeper(mines = userMinesInput)

    minesWeeper.drawBoard()

    do {
        println("Set/delete mines marks (x and y coordinates):")
        val mineCol = reader.nextInt()
        val mineRow = reader.nextInt()

        try {
            gameFinished = minesWeeper.markCellAsMined(listOf(mineCol, mineRow))

            minesWeeper.drawBoard()
        } catch (e: Exception) {
            println(e.message)
        }

    } while (!gameFinished)

    println("Congratulations! You found all the mines!")
}
