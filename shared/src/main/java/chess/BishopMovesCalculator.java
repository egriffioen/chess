package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMovesCalculator extends SlidingPieces implements PieceMovesCalculator {

    private Collection<ChessMove> moves = new ArrayList<>();

    public BishopMovesCalculator() {
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        addMoves(board, myPosition, 1,1, moves);
        addMoves(board, myPosition, 1,-1, moves);
        addMoves(board, myPosition, -1,1, moves);
        addMoves(board, myPosition, -1,-1, moves);
        return moves;
    }
}