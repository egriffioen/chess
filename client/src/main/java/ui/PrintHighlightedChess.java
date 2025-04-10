package ui;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.*;

import static ui.EscapeSequences.*;

public class PrintHighlightedChess extends PrintBoard{

    private String colorPerspective;
    private ChessGame chessGame;
    private Collection<ChessMove> validMoves;
    private ChessPosition position;

    public PrintHighlightedChess(String colorPerspective, ChessGame chessGame, Collection<ChessMove> validMoves, ChessPosition position) {
        super(colorPerspective, chessGame);
        this.validMoves = validMoves;
        this.position = position;
    }


    @Override
    void printBoardRow(int row, String colorPerspective) {
        for (int col = 0; col < 8; col++) {
            String square = board[row][col];

            String squareColor = (row + col) % 2 == 0
                    ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                    : EscapeSequences.SET_BG_COLOR_DARK_GREY;
            String pieceColor = "";
            if (colorPerspective.equals("WHITE")) {
                ChessPosition positionPrinted = new ChessPosition(8-row, col+1);
                if (position.equals(positionPrinted)) {
                    squareColor = SET_BG_COLOR_YELLOW;
                }
                for (ChessMove move: validMoves) {
                    if (move.getEndPosition().equals(positionPrinted) && squareColor.equals(EscapeSequences.SET_BG_COLOR_DARK_GREY)) {
                        squareColor = SET_BG_COLOR_DARK_GREEN;
                        break;
                    }
                    if (move.getEndPosition().equals(positionPrinted) && squareColor.equals(SET_BG_COLOR_LIGHT_GREY)) {
                        squareColor = SET_BG_COLOR_GREEN;
                        break;
                    }
                }
            }

            if (colorPerspective.equals("BLACK")) {
                ChessPosition positionPrinted = new ChessPosition(row+1, 8-col);
                if (position.equals(positionPrinted)) {
                    squareColor = SET_BG_COLOR_YELLOW;
                }
                for (ChessMove move: validMoves) {
                    if (move.getEndPosition().equals(positionPrinted) && squareColor.equals(EscapeSequences.SET_BG_COLOR_DARK_GREY)) {
                        squareColor = SET_BG_COLOR_DARK_GREEN;
                        break;
                    }
                    if (move.getEndPosition().equals(positionPrinted) && squareColor.equals(SET_BG_COLOR_LIGHT_GREY)) {
                        squareColor = SET_BG_COLOR_GREEN;
                        break;
                    }
                }
            }

            System.out.print(squareColor + pieceColor + square + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.RESET_BG_COLOR);
        }
    }


}
