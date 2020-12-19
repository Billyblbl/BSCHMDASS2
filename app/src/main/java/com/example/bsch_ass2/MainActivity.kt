package com.example.bsch_ass2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val board = findViewById<MineSweeperBoardView>(R.id.board)
        board.onBoardChange = {
            findViewById<TextView>(R.id.marked_cells).text = board.markedCells.toString()
            findViewById<TextView>(R.id.total_mines).text = board.totalMines.toString()
        }
        board.onBoardChange!!.invoke()

        findViewById<Button>(R.id.reset_button).setOnClickListener {
            board.resetGame()
            Log.d("reset_button", "HERE_DEBUG")
        }

        findViewById<SwitchCompat>(R.id.input_mode).setOnClickListener {
            board.toggleMode()
        }

    }
}