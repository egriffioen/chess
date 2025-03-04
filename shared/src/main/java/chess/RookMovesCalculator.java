package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator extends SlidingPieces implements PieceMovesCalculator {

    private Collection<ChessMove> moves = new ArrayList<>();

    public RookMovesCalculator() {
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        int up = 1;
        int down = -1;
        int left = -1;
        int right = 1;
        addMoves(board, myPosition, right, 0, moves);//move right
        addMoves(board, myPosition, 0, up, moves); //move up
        addMoves(board, myPosition, left, 0, moves); //move left
        addMoves(board, myPosition, 0, down, moves); //move down
        return moves;
    }


}
