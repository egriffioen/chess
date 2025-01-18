package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMovesCalculator implements PieceMovesCalculator {

    private Collection<ChessMove> moves = new ArrayList<>();

    public BishopMovesCalculator() {
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        int up = 1;
        int down = -1;
        int left = -1;
        int right = 1;
        addMoves(board, myPosition, right, up);
        addMoves(board, myPosition, right, down);
        addMoves(board, myPosition, left, up);
        addMoves(board, myPosition, left, down);
        return moves;

        //Checks all positions diagonal up and to the right
        //TODO Fix direction logic


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
            else {
                rowMove--;
            }
            if (colMove >0) {
                colMove++;
            }
            else {
                colMove--;
            }
        }
    }
}