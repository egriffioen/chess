package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {
    private Collection<ChessMove> moves = new ArrayList<>();

    public KingMovesCalculator() {
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        int up = 1;
        int down = -1;
        int left = -1;
        int right = 1;
        addMoves(board, myPosition, right, up); //diagonal right up
        addMoves(board, myPosition, right, down); //diagonal right down
        addMoves(board, myPosition, left, up); //diagonal left up
        addMoves(board, myPosition, left, down); //diagonal right down
        addMoves(board, myPosition, right, 0);//move right
        addMoves(board, myPosition, 0, up); //move up
        addMoves(board, myPosition, left, 0); //move left
        addMoves(board, myPosition, 0, down); //move down
        return moves;
    }

    public void addMoves(ChessBoard board, ChessPosition myPosition, int rowMove, int colMove) {
        ChessPosition end = new ChessPosition(myPosition.getRow()+rowMove, myPosition.getColumn()+colMove);
        if(end.getRow()==9||end.getColumn()==9||end.getRow()==0||end.getColumn()==0) {
            return;
        }
        if(board.getPiece(end)==null) {
            ChessMove move = new ChessMove(myPosition, end, null);
            moves.add(move);
        }
        else if (board.getPiece(end).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
            ChessMove move = new ChessMove(myPosition, end, null);
            moves.add(move);
        }
    }
}