package com.example.sudokusolver;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    GridLayout grid;
    EditText[][] cells = new EditText[9][9];
    TextView easy;

    Button solve;
    Button clear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grid = findViewById(R.id.sudokuGrid);
        solve=findViewById(R.id.solveButton);
        clear=findViewById(R.id.clearButton);
        easy=findViewById(R.id.easy);
        int [][]gridValue=new int [9][9];
        for (int i = 0; i < 9; i++) {
            Arrays.fill(gridValue[i], 0);
        }
        int row,col;

        int cellSizeDp = 36; // smaller size for mobile screens
        float scale = getResources().getDisplayMetrics().density;
        int cellPx = (int) (cellSizeDp * scale + 0.5f);

        for ( row = 0; row < 9; row++) {
            for (col = 0; col < 9; col++) {
                EditText cell = new EditText(this);

                // Appearance
                cell.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                cell.setGravity(Gravity.CENTER);
                cell.setTextColor(Color.BLACK);
                cell.setBackgroundColor(Color.WHITE);

                // Input config
                cell.setInputType(InputType.TYPE_CLASS_NUMBER);

                cell.setFocusable(true);
                cell.setFocusableInTouchMode(true);
                cell.setEnabled(true);

                int finalRow = row;
                int finalCol = col;
                cell.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        String input = s.toString().trim();

                        if (input.isEmpty()) {
                            // Optional: clear grid value or do nothing
                            gridValue[finalRow][finalCol] = 0;
                            return;
                        }

                        // Only allow digits 1–9
                        if (!input.matches("[1-9]")) {
                            cell.setText(""); // just clear the invalid input
                            return;
                        }

                        int value = Integer.parseInt(input);

                        // Check if it's safe to place the value in the Sudoku grid
                        if (!isSafe(gridValue, finalRow, finalCol, value)) {
                            cell.setText(""); // not safe — clear cell
                        } else {
                            gridValue[finalRow][finalCol] = value;
                        }
                        // ✅ Add your own logic here: check duplicates, auto-solve, etc.
                    }
                });

                // Layout config
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellPx;
                params.height = cellPx;

                // Bold 3x3 borders
                int thin = 1;
                int thick = 4;

                params.setMargins(
                        (col % 3 == 0) ? thick : thin,
                        (row % 3 == 0) ? thick : thin,
                        (col == 8) ? thick : thin,
                        (row == 8) ? thick : thin
                );

                cell.setLayoutParams(params);
                cells[row][col] = cell;
                grid.addView(cell);
            }
        }


        solve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int [][] arr=sudoku_solver(gridValue);


                for (int row = 0; row < 9; row++) {
                    for (int col = 0; col < 9; col++) {
                        if (!cells[row][col].getText().toString().isEmpty())continue;
                        cells[row][col].setText(String.valueOf(arr[row][col]));

                        cells[row][col].setFocusable(false);
                        cells[row][col].setEnabled(false);
                        cells[row][col].setInputType(InputType.TYPE_NULL);
                        gridValue[row][col] = 0;
                    }
                }

                solve.setEnabled(false);
                solve.setBackgroundColor(Color.WHITE);
                solve.setTextColor(Color.BLACK);
                easy.setText("Easy!");

            }
        });


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int row = 0; row < 9; row++) {
                    for (int col = 0; col < 9; col++) {
                        cells[row][col].setText("");
                        cells[row][col].setEnabled(true);
                        cells[row][col].setFocusable(true);
                        cells[row][col].setFocusableInTouchMode(true);
                        cells[row][col].setInputType(InputType.TYPE_CLASS_NUMBER); // restore input type
                    }
                }
                solve.setEnabled(true);
                solve.setBackgroundColor(Color.parseColor("#FFC107"));
                easy.setText("");

            }
        });



    }


    public static boolean isSafe ( int [][] grid, int row, int col, int tv ) // tv test value
    {
        int tr; //test row
        int tc; // test
        // row
        for( tr = row, tc = 0; tc < 9; tc++)
            if ( grid [ tr ] [ tc ] == tv)
                return false;
        // col check

        for( tc = col, tr = 0; tr < 9; tr++)
            if ( grid [ tr ] [ tc ] == tv)
                return false;
        int submat_row;
        int submat_col;
        submat_row = ( row - (row % 3));
        submat_col = ( col - (col % 3));
        for( tr = 0; tr < 3; tr++)
        {
            for( tc = 0; tc < 3;tc++)
            {
                if ( grid [ submat_row + tr ] [ submat_col + tc ] == tv )
                    return false;
            }
        }
        return true;
    }


    static boolean solved = false;

    public static void sudoku_solver_helper(int[][] orig, int[][] copy, int row, int col) {
        if (col == 9) {
            col = 0;
            row++;
        }
        if (row == 9) {
            solved = true;
            return;
        }

        if (orig[row][col] != 0) {
            copy[row][col] = orig[row][col];
            sudoku_solver_helper(orig, copy, row, col + 1);
        } else {
            for (int val = 1; val <= 9; val++) {
                if (isSafe(copy, row, col, val)) {
                    copy[row][col] = val;
                    sudoku_solver_helper(orig, copy, row, col + 1);
                    if (solved) return;
                    copy[row][col] = 0;
                }
            }
        }
    }



    public static int [][]  sudoku_solver ( int [][] grid )
    {
        // arm length / validation
        int [][] copy = new int [grid.length][grid.length];
        // set an environment

        int r;
        int c;
        for ( r = 0; r < 9; r++)
            for( c= 0; c< 9; c++)
                copy [ r ] [ c ] = grid [ r] [ c ];
        // call helper recursive , pass, initiate
        solved = false;
        sudoku_solver_helper( grid, copy, 0, 0);
        return copy;

    }





}
