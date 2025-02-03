package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalculator implements PieceMovesCalculator {
    private Collection<ChessMove> moves = new ArrayList<>();
    public PawnMovesCalculator() {
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        if (board.getPiece(myPosition).getTeamColor()== ChessGame.TeamColor.WHITE) {
            addMoves(board, myPosition, 1, 0);
        }
        else {
            addMoves(board, myPosition, -1, 0);
        }
        return moves;
    }
    //moves forward 1
    //firstMove can move 2 spots if available
    //capture diagonal
    //promotion piece if opposite side of board
    //COLOR MATTERS
    public void addMoves(ChessBoard board, ChessPosition myPosition, int rowMove, int colMove) {
        ChessPosition end = new ChessPosition(myPosition.getRow()+rowMove, myPosition.getColumn()+colMove);
        if (end.getRow()<1||end.getColumn()<1||end.getRow()>8||end.getColumn()>8) {
            return;
        }
        else if(board.getPiece(end)==null) {
            if (checkPromotion(end, myPosition, board)) {
                addPromotion(myPosition,end);
            }
            else {
                ChessMove move = new ChessMove(myPosition, end, null);
                moves.add(move);
                if (board.getPiece(myPosition).getTeamColor()== ChessGame.TeamColor.WHITE && myPosition.getRow()==2) {
                    ChessPosition fwPos = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn());
                    if (board.getPiece(fwPos)==null) {
                        ChessMove fwMove = new ChessMove(myPosition, fwPos, null);
                        moves.add(fwMove);
                    }
                }
                else if (board.getPiece(myPosition).getTeamColor()== ChessGame.TeamColor.BLACK && myPosition.getRow()==7) {
                    ChessPosition fbPos = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn());
                    if (board.getPiece(fbPos)==null) {
                        ChessMove fbMove = new ChessMove(myPosition, fbPos, null);
                        moves.add(fbMove);
                    }
                }
            }

        }
        ChessPosition captureLeft = new ChessPosition(myPosition.getRow()+rowMove,myPosition.getColumn()-1);
        ChessPosition captureRight = new ChessPosition(myPosition.getRow()+rowMove,myPosition.getColumn()+1);
        addCapture(board, myPosition, captureLeft);
        addCapture(board, myPosition, captureRight);

//        else if (board.getPiece(end).getTeamColor()!= board.getPiece(myPosition).getTeamColor()){
//            return;
//        }
//        else if (board.getPiece(end).getTeamColor()== board.getPiece(myPosition).getTeamColor()) {
//            return;
//        }
    }

    public boolean checkPromotion(ChessPosition endPos, ChessPosition myPosition, ChessBoard board) {
        if (board.getPiece(myPosition).getTeamColor()== ChessGame.TeamColor.WHITE && endPos.getRow()==8) {
            return true;
        }
        else if (board.getPiece(myPosition).getTeamColor()== ChessGame.TeamColor.BLACK && endPos.getRow()==1) {
            return true;
        }
        else{
            return false;
        }
    }

    public void addPromotion(ChessPosition myPosition, ChessPosition end) {
        ChessMove moveBishop = new ChessMove(myPosition, end, ChessPiece.PieceType.BISHOP);
        ChessMove moveRook = new ChessMove(myPosition, end, ChessPiece.PieceType.ROOK);
        ChessMove moveKnight = new ChessMove(myPosition, end, ChessPiece.PieceType.KNIGHT);
        ChessMove moveQueen = new ChessMove(myPosition, end, ChessPiece.PieceType.QUEEN);
        moves.add(moveBishop);
        moves.add(moveRook);
        moves.add(moveKnight);
        moves.add(moveQueen);
    }

    public void addCapture(ChessBoard board, ChessPosition start, ChessPosition end) {
        if (end.getRow()<1||end.getColumn()<1||end.getRow()>8||end.getColumn()>8) {
            return;
        }
        else if(board.getPiece(end)==null) {
            return;
        }
        else if (board.getPiece(end).getTeamColor()!=board.getPiece(start).getTeamColor()){
            if (checkPromotion(end, start, board)) {
                addPromotion(start,end);
            }
            else {
                ChessMove capture = new ChessMove(start, end, null);
                moves.add(capture);
            }

        }
    }

}