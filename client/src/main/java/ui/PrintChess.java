package ui;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class PrintChess {

    private String colorPerspective;
    private ChessGame chessGame;
    private final String[][] board = new String[8][8];

    public PrintChess(String colorPerspective, ChessGame chessGame) {
        this.colorPerspective = colorPerspective;
        this.chessGame = chessGame;
    }

    public void print() {
        if (colorPerspective=="WHITE") {
            printWhite();
        }
        if(colorPerspective=="BLACK") {
            printBlack();
        }
    }

    private void printWhite() {
        setBoardWhite();
        System.out.print(RESET_TEXT_COLOR);
        printLetterGrid();
        for (int row = 0; row < 8; row++) {
            System.out.print(" " + (8-row) + " ");
            printBoardRow(row, colorPerspective);
            System.out.print(" " + (8-row) + " ");
            System.out.println();
        }
        printLetterGrid();

    }

    private void printBlack() {
        setBoardBlack();
        System.out.print(RESET_TEXT_COLOR);
        printLetterGrid();
        for (int row = 0; row < 8; row++) {
            System.out.print(" " + (row + 1) + " ");
            printBoardRow(row, colorPerspective);
            System.out.print(" " + (row + 1) + " ");
            System.out.println();
        }
        printLetterGrid();
    }

    private void setBoardBlack() {
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++) {
                ChessPosition position = new ChessPosition(i+1,j+1);
                if (chessGame.getBoard().getPiece(position) == null) {
                    board[i][7-j] = "   ";
                    continue;
                }
                ChessPiece piece = chessGame.getBoard().getPiece(position);
                switch(piece.getPieceType()) {
                    case BISHOP:
                        board[i][7-j] = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? " B " : " b ";
                        break;
                    case KING:
                        board[i][7-j] = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? " K " : " k ";
                        break;
                    case QUEEN:
                        board[i][7-j] = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? " Q " : " q ";
                        break;
                    case KNIGHT:
                        board[i][7-j] = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? " N " : " n ";
                        break;
                    case ROOK:
                        board[i][7-j] = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? " R " : " r ";
                        break;
                    case PAWN:
                        board[i][7-j] = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? " P " : " p ";
                        break;
                    case null:
                        board[i][7-j] = "   ";
                        break;
                }
            }
        }
    }

    private void setBoardWhite() {
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++) {
                ChessPosition position = new ChessPosition(i+1,j+1);
                if (chessGame.getBoard().getPiece(position) == null) {
                    board[7-i][j] = "   ";
                    continue;
                }
                ChessPiece piece = chessGame.getBoard().getPiece(position);
                switch(piece.getPieceType()) {
                    case BISHOP:
                        board[7-i][j] = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? " B " : " b ";
                        break;
                    case KING:
                        board[7-i][j] = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? " K " : " k ";
                        break;
                    case QUEEN:
                        board[7-i][j] = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? " Q " : " q ";
                        break;
                    case KNIGHT:
                        board[7-i][j] = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? " N " : " n ";
                        break;
                    case ROOK:
                        board[7-i][j] = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? " R " : " r ";
                        break;
                    case PAWN:
                        board[7-i][j] = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? " P " : " p ";
                        break;
                    case null:
                        board[7-i][j] = "   ";
                        break;
                }
            }
        }
    }

    private void printBoardRow(int row, String colorPerspective) {
        for (int col = 0; col < 8; col++) {
            String square = board[row][col];

            String squareColor = (row + col) % 2 == 0
                    ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                    : EscapeSequences.SET_BG_COLOR_DARK_GREY;
            String pieceColor = "";
//            if (colorPerspective.equals("BLACK")) {
//                if (row <= 1) {
//                    pieceColor = EscapeSequences.SET_TEXT_COLOR_WHITE;
//                } else if (row >= 6) {
//                    pieceColor = EscapeSequences.SET_TEXT_COLOR_BLACK;
//                } else {
//                    pieceColor = "";
//                }
//            }
//
//            else if(colorPerspective.equals("WHITE")) {
//                if (row <= 1) {
//                    pieceColor = EscapeSequences.SET_TEXT_COLOR_BLACK;
//                } else if (row >= 6) {
//                    pieceColor = EscapeSequences.SET_TEXT_COLOR_WHITE;
//                } else {
//                    pieceColor = "";
//                }
//            }
            System.out.print(squareColor + pieceColor + square + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.RESET_BG_COLOR);
        }
    }

    private void printLetterGrid() {
        String[] letters = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
        List<String> letterList = Arrays.asList(letters);
        if (Objects.equals(colorPerspective, "WHITE")) {
            System.out.print("   " + EscapeSequences.RESET_TEXT_COLOR);
            for (String letter : letterList) {
                System.out.print(letter);
            }
            System.out.println();
        }
        else {
            Collections.reverse(letterList);
            System.out.print("   " + EscapeSequences.RESET_TEXT_COLOR);
            for (String letter : letterList) {
                System.out.print(letter);
            }
            System.out.println();
        }
    }
}
