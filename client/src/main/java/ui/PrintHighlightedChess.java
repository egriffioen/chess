package ui;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.*;

import static ui.EscapeSequences.*;

public class PrintHighlightedChess {

    private String colorPerspective;
    private ChessGame chessGame;
    private Collection<ChessMove> validMoves;
    private final String[][] board = new String[8][8];
    private ChessPosition position;

    public PrintHighlightedChess(String colorPerspective, ChessGame chessGame, Collection<ChessMove> validMoves, ChessPosition position) {
        this.colorPerspective = colorPerspective;
        this.chessGame = chessGame;
        this.validMoves = validMoves;
        this.position = position;
    }

    public void print() {
        if (Objects.equals(colorPerspective, "WHITE")) {
            printWhite();
        }
        if(Objects.equals(colorPerspective, "BLACK")) {
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
