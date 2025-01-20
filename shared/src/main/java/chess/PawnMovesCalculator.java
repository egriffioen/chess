package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    private Collection<ChessMove> moves = new ArrayList<>();
    public PawnMovesCalculator() {
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        if (board.getPiece(myPosition).getTeamColor()== ChessGame.TeamColor.BLACK) {
            addMoves(board, myPosition, -1, 0);
        }
        else {
            addMoves(board, myPosition, 1,0);
        }
        return moves;
    }
    //Can only move one space ahead
    //If first move can move 2 spaces ahead
    //attacks forward diagonally
    //If black gets to row 1, becomes a promotion piece...Need to check if promotion piece??
    //If white gets to row 8 becomes a promotion piece

    public void addMoves(ChessBoard board, ChessPosition myPosition, int rowMove, int colMove) {
        ChessPosition end = new ChessPosition(myPosition.getRow()+rowMove, myPosition.getColumn()+colMove);
        if(end.getRow()==9||end.getColumn()==9||end.getRow()==0||end.getColumn()==0) {
            return;
        }
        if(board.getPiece(end)==null) {
            if (checkPromotionMoves(myPosition, end, board)) {
                addPromotionPieceMoves(myPosition, end);
            }
            else {
                ChessMove move = new ChessMove(myPosition, end, null);
                moves.add(move);
                if (myPosition.getRow()==2 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
                    ChessPosition pawnFirstMovePosition = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn());
                    if (board.getPiece(pawnFirstMovePosition) == null) {
                        ChessMove pawnFirstMove = new ChessMove(myPosition, pawnFirstMovePosition, null);
                        moves.add(pawnFirstMove);
                    }
                }
                if (myPosition.getRow()==7 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
                    ChessPosition pawnFirstMovePosition = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn());
                    if (board.getPiece(pawnFirstMovePosition) == null) {
                        ChessMove pawnFirstMove = new ChessMove(myPosition, pawnFirstMovePosition, null);
                        moves.add(pawnFirstMove);
                    }
                }
            }
        }
        ChessPosition captureLeft = new ChessPosition(myPosition.getRow()+rowMove,myPosition.getColumn()-1);
        ChessPosition captureRight = new ChessPosition(myPosition.getRow()+rowMove,myPosition.getColumn()+1);

        addCaptureMoves(myPosition, captureLeft, board);
        addCaptureMoves(myPosition, captureRight, board);

    }

    public boolean checkPromotionMoves(ChessPosition myPosition, ChessPosition end, ChessBoard board) {
        if (end.getRow()==8 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE){
            return true;
        }
        else if (end.getRow()==1 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK){
            return true;
        }
        return false;
    }

    public void addPromotionPieceMoves(ChessPosition startPosition, ChessPosition endPosition) {
        ChessMove bishopPromotion = new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP);
        moves.add(bishopPromotion);
        ChessMove rookPromotion = new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK);
        moves.add(rookPromotion);
        ChessMove knightPromotion = new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT);
        moves.add(knightPromotion);
        ChessMove queenPromotion = new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN);
        moves.add(queenPromotion);
    }

    public void addCaptureMoves(ChessPosition myPosition, ChessPosition capturePosition, ChessBoard board) {
        if(capturePosition.getColumn()==0||capturePosition.getColumn()==9||board.getPiece(capturePosition)==null) {
            return;
        }
        if (checkPromotionMoves(myPosition, capturePosition, board)) {
            addPromotionPieceMoves(myPosition, capturePosition);
        }
        else if (board.getPiece(capturePosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
            ChessMove move = new ChessMove(myPosition, capturePosition, null);
            moves.add(move);
        }
    }


}
