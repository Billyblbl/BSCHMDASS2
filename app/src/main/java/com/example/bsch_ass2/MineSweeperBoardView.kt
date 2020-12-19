package com.example.bsch_ass2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

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
        UNCOVERED(Color.GRAY)
    }

    var cells = Array(boardWidth * boardHeight) { CellState.UNKNOWN }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateRenderingData()
    }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.MineSweeperBoardView, style, 0).apply { try {
            boardWidth = getInteger(R.styleable.MineSweeperBoardView_board_width, 10)
            boardHeight = getInteger(R.styleable.MineSweeperBoardView_board_height, 10)
            totalMines = getInteger(R.styleable.MineSweeperBoardView_mine_count, 20)
            cellMargins = getDimension(R.styleable.MineSweeperBoardView_cell_margins, 20.0f)
        } finally { recycle() }}
    }

    fun toggleMode() { mode = if (mode == Mode.UNCOVER) Mode.MARK else Mode.UNCOVER }

    fun resetBoard() {
        cells = Array(boardWidth * boardHeight) { CellState.UNKNOWN }
    }

    private fun cellIndexFromCoords(x : Int, y : Int) = y * boardWidth + x

    fun at(x : Int, y : Int) = cells[cellIndexFromCoords(x, y)]

    private var contentWidth = width - paddingLeft - paddingRight
    private var contentHeight = height - paddingTop - paddingBottom
    private var fullCellRect = Rect()
    private var visCellRect = Rect()
    fun updateRenderingData() {
        contentWidth = width - paddingLeft - paddingRight
        contentHeight = height - paddingTop - paddingBottom

        fullCellRect =  if (boardWidth == 0 || boardHeight == 0 ) Rect(0, 0, 0, 0)
                        else Rect(0, 0,contentWidth / boardWidth,contentHeight / boardHeight)

        visCellRect = Rect(
            cellMargins.toInt(),
            cellMargins.toInt(),
            (fullCellRect.width() - cellMargins).toInt(),
            (fullCellRect.height() - cellMargins).toInt()
        )
        invalidate()
    }

    private val cellPaint = Paint()
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        canvas.drawColor(Color.WHITE)

        //Put this call at the right place later, this allocates Rects every time
        updateRenderingData()

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
        cells[cellIndexFromCoords(x, y)] = CellState.UNCOVERED
        //propagate uncover depending on adjacency
        invalidate()
    }

    private fun mark(x : Int, y : Int) {

    }

    private fun unmark(x : Int, y : Int) {

    }

}