package chess;

import java.util.Collection;

public abstract class SlidingPieces {
    public void addMoves(ChessBoard board, ChessPosition myPosition, int rowMove, int colMove, Collection<ChessMove> moves) {
        for (int i=0;i<8;i++) {
            ChessPosition end = new ChessPosition(myPosition.getRow()+rowMove, myPosition.getColumn()+colMove);
            if(end.getRow()<1||end.getColumn()<1||end.getRow()>8||end.getColumn()>8){
                break;
            }
            else if(board.getPiece(end)==null) {
                ChessMove move = new ChessMove(myPosition, end, null);
                moves.add(move);
            }
            else if (board.getPiece(end).getTeamColor()!=board.getPiece(myPosition).getTeamColor()){
                ChessMove move = new ChessMove(myPosition, end, null);
                moves.add(move);
                break;
            }
            else if (board.getPiece(end).getTeamColor()==board.getPiece(myPosition).getTeamColor()){
                break;
            }

            if (rowMove>0){
                rowMove++;
            }
            else if(rowMove<0) {
                rowMove--;
            }
            if(colMove>0) {
                colMove++;
            }
            else if(colMove<0){
                colMove--;
            }
        }
    }
}
