package com.example.bsch_ass2

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.sql.Time
import java.sql.Timestamp
import java.time.Clock
import kotlin.random.Random

class MineSweeperBoardView @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    style: Int = 0
) : View(ctx, attrs, style) {

    enum class Mode {
        UNCOVER,
        MARK
    }

    var totalMines = 0
        set(value) {
            field = value
            resetBoard()
            updateRenderingData()
        }

    var markedCells = 0
    var mode = Mode.UNCOVER

    var boardWidth = 0
        set(value) {
            field = value
            resetBoard()
            updateRenderingData()
        }

    var boardHeight = 0
        set(value) {
            field = value
            resetBoard()
            updateRenderingData()
        }

    var cellMargins = 0.0f
        set(value) {
            field = value
            updateRenderingData()
        }

    enum class CellState(val drawColor : Int) {
        UNKNOWN(Color.BLACK),
        UNCOVERED(Color.GRAY),
        EXPLODED(Color.RED)
    }

    var cells = Array(boardWidth * boardHeight) { CellState.UNKNOWN }

    data class Coordinates(val x : Int, val y : Int)

    //Indexes of cells containing a mine
    val mines = ArrayList<Int>()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateRenderingData()
    }

    private val textPaint = Paint()

    private var visCellRect = RectF()
    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.MineSweeperBoardView, style, 0).apply { try {
            boardWidth = getInteger(R.styleable.MineSweeperBoardView_board_width, 10)
            boardHeight = getInteger(R.styleable.MineSweeperBoardView_board_height, 10)
            totalMines = getInteger(R.styleable.MineSweeperBoardView_mine_count, 20)
            cellMargins = getDimension(R.styleable.MineSweeperBoardView_cell_margins, 20.0f)
        } finally { recycle() }}
        textPaint.color = Color.BLACK
    }

    fun toggleMode() { mode = if (mode == Mode.UNCOVER) Mode.MARK else Mode.UNCOVER }

    fun resetBoard() {
        cells = Array(boardWidth * boardHeight) { CellState.UNKNOWN }
        mines.clear()
        disseminateMines()
    }

    private fun disseminateMines() {
        val rdm = Random(System.currentTimeMillis())
        val possibleIdx = cells.indices.toMutableList()

        for (i in 0 until totalMines) {
            val idxIdx = rdm.nextInt(possibleIdx.size)
            mines.add(possibleIdx[idxIdx])
            possibleIdx.removeAt(idxIdx)
        }
    }

    private fun cellIndexFromCoords(x : Int, y : Int) = y * boardWidth + x
    private fun cellCoordsFromIndex(i : Int) = Coordinates(
        x = i % boardWidth,
        y = i / boardWidth
    )

    private fun adjacentCellsCoord(x : Int, y : Int) = arrayOf(
        Coordinates(x - 1, y - 1),
        Coordinates(x, y - 1),
        Coordinates(x + 1, y - 1),
        Coordinates(x + 1, y),
        Coordinates(x + 1, y + 1),
        Coordinates(x, y + 1),
        Coordinates(x - 1, y + 1),
        Coordinates(x - 1, y),
    )

    private fun inBoard(x : Int, y : Int) = x in 0 until boardWidth && y in 0 until boardHeight

    private fun adjacentMines(x : Int, y : Int) : Int {
        var count = 0
        val adjacentCellsIdx = adjacentCellsCoord(x, y).map { if (inBoard(it.x, it.y)) cellIndexFromCoords(it.x, it.y) else null }
        for (idx in adjacentCellsIdx) if (idx != null && mines.find { it == idx } != null) count++
        return count
    }

    fun at(x : Int, y : Int) = cells[cellIndexFromCoords(x, y)]

    private var contentWidth = width - paddingLeft - paddingRight
    private var contentHeight = height - paddingTop - paddingBottom
    private var fullCellRect = Rect()
    fun updateRenderingData() {
        contentWidth = width - paddingLeft - paddingRight
        contentHeight = height - paddingTop - paddingBottom

        fullCellRect =  if (boardWidth == 0 || boardHeight == 0 ) Rect(0, 0, 0, 0)
                        else Rect(0, 0,contentWidth / boardWidth,contentHeight / boardHeight)

        visCellRect = RectF(
            cellMargins,
            cellMargins,
            (fullCellRect.width() - cellMargins),
            (fullCellRect.height() - cellMargins)
        )
        textPaint.textSize = visCellRect.width()
        invalidate()
    }

    private val cellPaint = Paint()
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        canvas.drawColor(Color.WHITE)

        //Put this call at the right place later, this allocates Rects every time
//        updateRenderingData()

        canvas.save()
        canvas.translate(cellMargins, cellMargins)
        for (i in 0 until boardHeight) {
            canvas.save()
            for (j in 0 until boardWidth) {
                canvas.save()
                canvas.translate((fullCellRect.width() * j).toFloat(), (fullCellRect.height() * i).toFloat())

                //Draw cell
                cellPaint.color = at(j, i).drawColor
                canvas.drawRect(visCellRect, cellPaint)
                when (at(j, i)) {
                    CellState.EXPLODED -> canvas.drawText("M", visCellRect.left * 2, visCellRect.height() - visCellRect.top, textPaint)
                    CellState.UNCOVERED -> if (adjacentMines(j, i) > 0) canvas.drawText(adjacentMines(j, i).toString(), visCellRect.left * 2, visCellRect.height() - visCellRect.top, textPaint)
                    else -> {}
                }

                canvas.restore()
            }
            canvas.restore()
        }
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return super.onTouchEvent(event)
        val x = (event.x * boardWidth / contentWidth).toInt()
        val y = (event.y * boardHeight / contentHeight).toInt()
        onTouchCellEvent(x, y)
        return super.onTouchEvent(event)
    }

    private fun onTouchCellEvent(x : Int, y : Int) {
        val state = cells[cellIndexFromCoords(x, y)]
        when {
            state == CellState.UNKNOWN && mode == Mode.UNCOVER -> uncover(x, y)
            else -> {}
        }
    }

    private fun uncover(x : Int, y : Int) {
        //if mine in (x, y) -> boom, else
        if (mines.find { it == cellIndexFromCoords(x, y) } != null) for (mine in mines) {
            cells[mine] = CellState.EXPLODED
            exploded = true
        } else {
            cells[cellIndexFromCoords(x, y)] = CellState.UNCOVERED
            if (adjacentMines(x, y) == 0) for (cell in adjacentCellsCoord(x, y)) {
                if (inBoard(cell.x, cell.y) && at(cell.x, cell.y) == CellState.UNKNOWN) uncover(cell.x, cell.y)
            }
        }
        invalidate()
    }

    private fun mark(x : Int, y : Int) {

    }

    private fun unmark(x : Int, y : Int) {

    }

}