package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator {

    private Collection<ChessMove> moves = new ArrayList<>();

    public RookMovesCalculator() {
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        int up = 1;
        int down = -1;
        int left = -1;
        int right = 1;
        addMoves(board, myPosition, right, 0);
        addMoves(board, myPosition, 0, up);
        addMoves(board, myPosition, left, 0);
        addMoves(board, myPosition, 0, down);
        return moves;
    }

    public void addMoves(ChessBoard board, ChessPosition myPosition, int rowMove, int colMove) {
        for (int i = 0; i < 8; i++) {

            ChessPosition end = new ChessPosition(myPosition.getRow()+rowMove, myPosition.getColumn()+colMove);
            if(end.getRow()==9||end.getColumn()==9||end.getRow()==0||end.getColumn()==0) {
                break;
            }
            else if(board.getPiece(end)==null) {
                ChessMove move = new ChessMove(myPosition, end, null);
                moves.add(move);
            }
            else if (board.getPiece(end).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                ChessMove move = new ChessMove(myPosition, end, null);
                moves.add(move);
                break;
            }
            else if (board.getPiece(end).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                break;
            }

            if (rowMove>0) {
                rowMove++;
            }
            else if (rowMove<0) {
                rowMove--;
            }
            else if (colMove >0) {
                colMove++;
            }
            else if (colMove<0) {
                colMove--;
            }
        }
    }
}
