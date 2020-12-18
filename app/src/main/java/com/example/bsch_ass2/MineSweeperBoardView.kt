package com.example.bsch_ass2

import android.content.Context
import android.graphics.Canvas
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
    var boardHeight = 0

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.MineSweeperBoardView, style, 0).apply {
            try {
                boardWidth = getInteger(R.styleable.MineSweeperBoardView_board_width, 10)
                boardHeight = getInteger(R.styleable.MineSweeperBoardView_board_height, 10)
                totalMines = getInteger(R.styleable.MineSweeperBoardView_mine_count, 20)
            } finally {
                recycle()
            }
        }
    }

    fun toggleMode() { mode = if (mode == Mode.UNCOVER) Mode.MARK else Mode.UNCOVER }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

}