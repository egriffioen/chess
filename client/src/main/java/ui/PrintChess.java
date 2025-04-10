package ui;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class PrintChess extends PrintBoard {

    private String colorPerspective;
    private ChessGame chessGame;

    public PrintChess(String colorPerspective, ChessGame chessGame) {
        super(colorPerspective, chessGame);
    }


    @Override
    void printBoardRow(int row, String colorPerspective) {
        for (int col = 0; col < 8; col++) {
            String square = board[row][col];

            String squareColor = (row + col) % 2 == 0
                    ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                    : EscapeSequences.SET_BG_COLOR_DARK_GREY;
            String pieceColor = "";
            System.out.print(squareColor + pieceColor + square + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.RESET_BG_COLOR);
        }
    }

}
