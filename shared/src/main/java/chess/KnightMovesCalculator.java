package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {
    private Collection<ChessMove> moves = new ArrayList<>();

    public KnightMovesCalculator() {
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        addMoves(board, myPosition, 2, 1); //right 2, up 1
        addMoves(board, myPosition, 2, -1); //right 2, down 1
        addMoves(board, myPosition, -2, 1); //left 2 up 1
        addMoves(board, myPosition, -2, -1); //left 2 down 1
        addMoves(board, myPosition, 1, 2);//right 1, up 2
        addMoves(board, myPosition, 1, -2); //right 1, down 2
        addMoves(board, myPosition, -1, 2); //left 1, up 2
        addMoves(board, myPosition, -1, -2); //left 1, down 2
        return moves;
    }

    public void addMoves(ChessBoard board, ChessPosition myPosition, int rowMove, int colMove) {
        ChessPosition end = new ChessPosition(myPosition.getRow() + rowMove, myPosition.getColumn() + colMove);
        if (end.getRow() > 8 || end.getColumn() > 8 || end.getRow() <= 0 || end.getColumn() <= 0) {
            return;
        }
        if (board.getPiece(end) == null) {
            ChessMove move = new ChessMove(myPosition, end, null);
            moves.add(move);
        } else if (board.getPiece(end).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
            ChessMove move = new ChessMove(myPosition, end, null);
            moves.add(move);
        }
    }
}