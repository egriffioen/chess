package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        resetPawns();
        resetRooks();
        resetKnights();
        resetBishops();
        resetQueens();
        resetKings();
    }

    public void resetPawns() {
        int numWhitePawns = 8;
        int numBlackPawns = 8;
        for (int i = 0; i < numWhitePawns; i++) {
            ChessPiece pawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            ChessPosition position = new ChessPosition(2,i+1);
            addPiece(position, pawn);
        }
        for (int i = 0; i < numBlackPawns; i++) {
            ChessPiece pawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            ChessPosition position = new ChessPosition(7,i+1);
            addPiece(position, pawn);
        }
    }

    public void resetRooks() {
        ChessPiece whiteLeftRook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPosition positionWhiteLeftRook = new ChessPosition(1,1);
        addPiece(positionWhiteLeftRook, whiteLeftRook);

        ChessPiece whiteRightRook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPosition positionWhiteRightRook = new ChessPosition(1,8);
        addPiece(positionWhiteRightRook, whiteRightRook);

        ChessPiece blackLeftRook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPosition positionBlackLeftRook = new ChessPosition(8,1);
        addPiece(positionBlackLeftRook, blackLeftRook);

        ChessPiece blackRightRook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPosition positionBlackRightRook = new ChessPosition(8,8);
        addPiece(positionBlackRightRook, blackRightRook);
    }

    public void resetKnights() {
        ChessPiece whiteLeftKnight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPosition positionWhiteLeftKnight = new ChessPosition(1,2);
        addPiece(positionWhiteLeftKnight, whiteLeftKnight);

        ChessPiece whiteRightKnight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPosition positionWhiteRightKnight = new ChessPosition(1,7);
        addPiece(positionWhiteRightKnight, whiteRightKnight);

        ChessPiece blackLeftKnight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPosition positionBlackLeftKnight = new ChessPosition(8,2);
        addPiece(positionBlackLeftKnight, blackLeftKnight);

        ChessPiece blackRightKnight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPosition positionBlackRightKnight = new ChessPosition(8,7);
        addPiece(positionBlackRightKnight, blackRightKnight);
    }

    public void resetBishops() {
        ChessPiece whiteLeftBishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPosition positionWhiteLeftBishop = new ChessPosition(1,3);
        addPiece(positionWhiteLeftBishop, whiteLeftBishop);

        ChessPiece whiteRightBishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPosition positionWhiteRightBishop = new ChessPosition(1,6);
        addPiece(positionWhiteRightBishop, whiteRightBishop);

        ChessPiece blackLeftBishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPosition positionBlackLeftBishop = new ChessPosition(8,3);
        addPiece(positionBlackLeftBishop, blackLeftBishop);

        ChessPiece blackRightBishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPosition positionBlackRightBishop = new ChessPosition(8,6);
        addPiece(positionBlackRightBishop, blackRightBishop);
    }

    public void resetQueens() {
        ChessPiece whiteQueen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        ChessPosition positionWhiteQueen = new ChessPosition(1,4);
        addPiece(positionWhiteQueen, whiteQueen);

        ChessPiece blackQueen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        ChessPosition positionBlackQueen = new ChessPosition(8,4);
        addPiece(positionBlackQueen, blackQueen);
    }

    public void resetKings() {
        ChessPiece whiteKing = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        ChessPosition positionWhiteKing = new ChessPosition(1,5);
        addPiece(positionWhiteKing, whiteKing);

        ChessPiece blackKing = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        ChessPosition positionBlackKing = new ChessPosition(8,5);
        addPiece(positionBlackKing, blackKing);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }
}
