package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator {
    private Collection<ChessMove> moves = new ArrayList<>();

    public QueenMovesCalculator() {
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        RookMovesCalculator horizontalMoves = new RookMovesCalculator();
        moves.addAll(horizontalMoves.calculateMoves(board, myPosition));
        BishopMovesCalculator diagonalMoves = new BishopMovesCalculator();
        moves.addAll(diagonalMoves.calculateMoves(board, myPosition));
        return moves;
    }
}