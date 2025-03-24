package ui;

import facade.ServerFacade;
import java.util.Arrays;

import java.util.*;
import java.util.List;

public class InGameClient {
    private final ServerFacade server;
    private final String serverUrl;
    //private State state = State.INGAME;
    public static final String HALF_SPACE = "\u2009";
    private final String[][] board = new String[8][8];

    public InGameClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "quit" -> "quit --> returning to Lobby";
            default -> help();
        };
    }

    public String help() {
        return """
                - quit -> quit playing chess
                - help -> help with possible commands
                """;
    }

    public void printChessBoard(String colorPerspective) {
        if (colorPerspective.equals("WHITE")) {
            printChessBoardWhite();
        }
        else if (colorPerspective.equals("BLACK")) {
            printChessBoardBlack();
        }
    }

    private void printChessBoardBlack() {
        String colorPerspective = "BLACK";

        board[0][0] = EscapeSequences.WHITE_ROOK;
        board[0][1] = EscapeSequences.WHITE_KNIGHT;
        board[0][2] = EscapeSequences.WHITE_BISHOP;
        board[0][3] = EscapeSequences.WHITE_KING;
        board[0][4] = EscapeSequences.WHITE_QUEEN;
        board[0][5] = EscapeSequences.WHITE_BISHOP;
        board[0][6] = EscapeSequences.WHITE_KNIGHT;
        board[0][7] = EscapeSequences.WHITE_ROOK;

        for (int i = 0; i < 8; i++) {
            board[1][i] = EscapeSequences.WHITE_PAWN;
        }

        board[7][0] = EscapeSequences.BLACK_ROOK;
        board[7][1] = EscapeSequences.BLACK_KNIGHT;
        board[7][2] = EscapeSequences.BLACK_BISHOP;
        board[7][3] = EscapeSequences.BLACK_KING;
        board[7][4] = EscapeSequences.BLACK_QUEEN;
        board[7][5] = EscapeSequences.BLACK_BISHOP;
        board[7][6] = EscapeSequences.BLACK_KNIGHT;
        board[7][7] = EscapeSequences.BLACK_ROOK;

        for (int i = 0; i < 8; i++) {
            board[6][i] = EscapeSequences.BLACK_PAWN;
        }

        setEmptySquares();

        String[] letters = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
        List<String> letterList = Arrays.asList(letters);
        Collections.reverse(letterList);
        System.out.print("   " + EscapeSequences.RESET_TEXT_COLOR);
        for (String letter : letterList) {
            System.out.print(HALF_SPACE+letter+HALF_SPACE);
        }
        System.out.println();
        for (int row = 0; row < 8; row++) {
            System.out.print(" " + (row + 1) + " ");
            printBoardRow(row, colorPerspective);
            System.out.print(" " + (row + 1) + " ");
            System.out.println();
        }
        System.out.print("   ");
        for (String letter : letterList) {
            System.out.print(HALF_SPACE+letter+HALF_SPACE);
        }
        System.out.println();
    }


    private void printChessBoardWhite() {
        String colorPerspective = "WHITE";

        board[7][0] = EscapeSequences.WHITE_ROOK;
        board[7][1] = EscapeSequences.WHITE_KNIGHT;
        board[7][2] = EscapeSequences.WHITE_BISHOP;
        board[7][3] = EscapeSequences.WHITE_QUEEN;
        board[7][4] = EscapeSequences.WHITE_KING;
        board[7][5] = EscapeSequences.WHITE_BISHOP;
        board[7][6] = EscapeSequences.WHITE_KNIGHT;
        board[7][7] = EscapeSequences.WHITE_ROOK;

        for (int i = 0; i < 8; i++) {
            board[6][i] = EscapeSequences.WHITE_PAWN;
        }

        board[0][0] = EscapeSequences.BLACK_ROOK;
        board[0][1] = EscapeSequences.BLACK_KNIGHT;
        board[0][2] = EscapeSequences.BLACK_BISHOP;
        board[0][3] = EscapeSequences.BLACK_QUEEN;
        board[0][4] = EscapeSequences.BLACK_KING;
        board[0][5] = EscapeSequences.BLACK_BISHOP;
        board[0][6] = EscapeSequences.BLACK_KNIGHT;
        board[0][7] = EscapeSequences.BLACK_ROOK;

        for (int i = 0; i < 8; i++) {
            board[1][i] = EscapeSequences.BLACK_PAWN;
        }
        setEmptySquares();
        // Print the board with alternating colors
        String[] letters = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
        List<String> letterList = Arrays.asList(letters);
        System.out.print("   " + EscapeSequences.RESET_TEXT_COLOR);
        for (String letter : letterList) {
            System.out.print(HALF_SPACE+letter+HALF_SPACE);
        }
        System.out.println();
        for (int row = 0; row < 8; row++) {
            System.out.print(" " + (8-row) + " ");
            printBoardRow(row, colorPerspective);
            System.out.print(" " + (8-row) + " ");
            System.out.println();
        }
        System.out.print("   ");
        for (String letter : letterList) {
            System.out.print(HALF_SPACE+letter+HALF_SPACE);
        }
        System.out.println();
    }

    private void setEmptySquares() {
        for (int row = 2; row < 6; row++) {
            for (int col = 0; col < 8; col++) {
                if (col % 2 == 0) {
                    if (row % 2 == 0) {
                        String invisiblePawn = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + EscapeSequences.WHITE_PAWN +
                                EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR;
                        board[row][col] = invisiblePawn;
                    } else {
                        String invisiblePawn = EscapeSequences.SET_TEXT_COLOR_DARK_GREY + EscapeSequences.WHITE_PAWN +
                                EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR;
                        board[row][col] = invisiblePawn;
                    }
                } else {
                    if (row % 2 == 0) {
                        String invisiblePawn = EscapeSequences.SET_TEXT_COLOR_DARK_GREY + EscapeSequences.WHITE_PAWN +
                                EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR;
                        board[row][col] = invisiblePawn;
                    } else {
                        String invisiblePawn = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + EscapeSequences.WHITE_PAWN +
                                EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR;
                        board[row][col] = invisiblePawn;
                    }
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
            if (colorPerspective.equals("BLACK")) {
                if (row <= 1) {
                    pieceColor = EscapeSequences.SET_TEXT_COLOR_WHITE;
                } else if (row >= 6) {
                    pieceColor = EscapeSequences.SET_TEXT_COLOR_BLACK;
                } else {
                    pieceColor = "";
                }
            }
            
            else if(colorPerspective.equals("WHITE")) {
                if (row <= 1) {
                    pieceColor = EscapeSequences.SET_TEXT_COLOR_BLACK;
                } else if (row >= 6) {
                    pieceColor = EscapeSequences.SET_TEXT_COLOR_WHITE;
                } else {
                    pieceColor = "";
                }
            }
            System.out.print(squareColor + pieceColor + square + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.RESET_BG_COLOR);
        }
    }
}