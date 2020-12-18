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

    init {
        //Act as constructor here
    }

    fun toggleMode() { mode = if (mode == Mode.UNCOVER) Mode.MARK else Mode.UNCOVER }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

}