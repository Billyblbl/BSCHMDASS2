package com.example.bsch_ass2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
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
            updateRenderingData()
        }

    var boardHeight = 0
        set(value) {
            field = value
            updateRenderingData()
        }

    var cellMargins = 0.0f
        set(value) {
            field = value
            updateRenderingData()
        }

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

    private var fullCellRect = Rect()
    private var visCellRect = Rect()
    fun updateRenderingData() {
        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        fullCellRect = Rect(
            0, 0,
            contentWidth / boardWidth,
            contentHeight / boardHeight
        )
        visCellRect = Rect(
            cellMargins.toInt(),
            cellMargins.toInt(),
            (fullCellRect.width() - cellMargins).toInt(),
            (fullCellRect.height() - cellMargins).toInt()
        )
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
        cellPaint.color = Color.BLACK
        for (i in 0 until boardHeight) {
            canvas.save()
            for (j in 0 until boardWidth) {
                canvas.save()
                canvas.translate((fullCellRect.width() * j).toFloat(), (fullCellRect.height() * i).toFloat())

                //Draw cell
                canvas.drawRect(visCellRect, cellPaint)

                canvas.restore()
            }
            canvas.restore()
        }
        canvas.restore()
    }

}