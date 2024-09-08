package minesweeper

import java.util.Scanner
import kotlin.random.Random

enum class BoardMarks(val symbol: String) {
    MINE("X"),
    HIDDEN("."),
    MARKED("*"),
    FREE("/")
}

class MinesWeeper (val mines: Int = 10) {
    private val rows: Int = 9
    private val columns: Int = 9
    private val board: MutableList<MutableList<String>> = mutableListOf()
    private val minesLocation: MutableList<List<Int>> = mutableListOf()
    private val cellsMarked: MutableList<List<Int>> = mutableListOf()

    private var steppedAMine: Boolean = false

    init {
        this.setInitMinesOnBoard()
        this.initBoard()

        println(this.minesLocation)
    }

    private fun setInitMinesOnBoard() {
        var counter = 0
        val availableRows = MutableList(this.rows * this.columns){
            if (counter == this.rows) counter = 0
            ++counter
        }
        counter = 0
        val availableColums = MutableList(this.rows * this.columns){
            if (counter == this.columns) counter = 0
            ++counter
        }

        do {
            val randomRow = Random.nextInt(availableRows.size)
            val randomCol = Random.nextInt(availableColums.size)
            val newMine = listOf(availableRows[randomRow], availableColums[randomCol])

            if (this.minesLocation.indexOf(newMine) == -1) {
                this.minesLocation.add(newMine)

                availableRows.removeAt(randomRow)
                availableColums.removeAt(randomCol)
            }
        } while (this.minesLocation.size < this.mines)
    }

    private fun initBoard() {
        for (row in 0..this.rows) {
            this.board.add(MutableList(this.columns + 1){ BoardMarks.HIDDEN.symbol })
        }
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
                    print(
                        if (this.steppedAMine && this.minesLocation.indexOf(listOf(col, row)) != -1) {
                            BoardMarks.MINE.symbol
                        } else {
                            this.board[row][col]
                        }
                    )
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
        if (this.minesLocation.size == this.cellsMarked.size) {
            return this.minesLocation.containsAll(this.cellsMarked)
        }

        return false
    }

    private fun markCellAsMined(cords: List<Int>): List<Boolean> {
        if (this.cellsMarked.indexOf(cords) != -1) {
            this.cellsMarked.remove(cords)
            this.board[cords.last()][cords.first()] = BoardMarks.HIDDEN.symbol
        } else {
            this.cellsMarked.add(cords)
            this.board[cords.last()][cords.first()] = BoardMarks.MARKED.symbol
        }

        val result = this.allAndOnlyMinesAreMarked()

        return listOf(result, result)
    }

    private fun getSurroundingCells(cords: List<Int>): List<List<Int>> {
        val rowIni = if (cords[1] == 1) 1 else cords[1] - 1
        val rowEnd = if (cords[1] == this.rows) this.rows else cords[1] + 1

        val colIni = if (cords[0] == 1) 1 else cords[0] - 1
        val colEnd = if (cords[0] == this.columns) this.columns else cords[0] + 1

        val cellsAround = mutableListOf<List<Int>>()

        for (r in rowIni..rowEnd) {
            for (c in colIni..colEnd) {
                val thisCell = listOf(c, r)

                if (thisCell != cords) {
                    cellsAround.add(thisCell)
                }
            }
        }

        return cellsAround
    }

    private fun checkAndExpandFreeCells(cords: List<Int>) {
        if (
            this.minesLocation.indexOf(cords) != -1 ||
            (
                this.board[cords.last()][cords.first()] != BoardMarks.HIDDEN.symbol &&
                this.board[cords.last()][cords.first()] != BoardMarks.MARKED.symbol
            )
        ) {
            return
        }

        var minesCounter = 0

        for (cell in this.getSurroundingCells(cords)) {
            if (this.minesLocation.indexOf(cell) != -1) {
                minesCounter++
            }
        }

        if (minesCounter != 0) {
            this.board[cords.last()][cords.first()] = minesCounter.toString()
        } else {
            this.board[cords.last()][cords.first()] = BoardMarks.FREE.symbol
            for (cell in this.getSurroundingCells(cords)) {
                this.checkAndExpandFreeCells(cell)
            }
        }
    }

    private fun allFreeCellsAreCleared(): Boolean {
        var nonFreeCells = 0

        for (r in 1..9) {
            for (c in 1..9) {
                nonFreeCells += if (
                    board[r][c] == BoardMarks.HIDDEN.symbol ||
                    board[r][c] == BoardMarks.MARKED.symbol
                ) 1 else 0
            }
        }

        return nonFreeCells == this.minesLocation.size
    }

    private fun markCellAsFree(cords: List<Int>): List<Boolean> {
        if (this.minesLocation.indexOf(cords) != -1) {
            this.steppedAMine = true
            return listOf(true, false)
        }

        this.checkAndExpandFreeCells(cords)

        return listOf(this.allFreeCellsAreCleared(), this.allFreeCellsAreCleared())
    }

    fun performActionOnCell(cords: List<Int>, action: String): List<Boolean> {
        val result = when (action) {
            "mine" -> this.markCellAsMined(cords)
            "free" -> this.markCellAsFree(cords)
            else -> {
                throw Exception("Not valid action '$action' for your movement")
            }
        }
        return result
    }
}

fun main() {
    // Init inputs reader
    val reader = Scanner(System.`in`)

    println("How many mines do you want on the field?")
    val userMinesInput = reader.nextInt()
    var gameWon = false

    val minesWeeper = MinesWeeper(userMinesInput)

    minesWeeper.drawBoard()

    do {
        println("Set/unset mines marks or claim a cell as free:")
        val mineCol = reader.nextInt()
        val mineRow = reader.nextInt()
        val markOrClaim = reader.next()
        var gameResult: List<Boolean> = mutableListOf()

        try {
            gameResult = minesWeeper.performActionOnCell(listOf(mineCol, mineRow), markOrClaim)
            gameWon = gameResult.last()

            minesWeeper.drawBoard()
        } catch (e: Exception) {
            println(e.message)
        }

    } while (gameResult.isEmpty() || !gameResult.first())

    println(
        if (!gameWon) {
            "You stepped on a mine and failed!"
        } else {
            "Congratulations! You found all the mines!"
        }
    )
}
