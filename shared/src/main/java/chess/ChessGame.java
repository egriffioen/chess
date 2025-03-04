package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamColorTurn;


    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamColorTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamColorTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamColorTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> pieceMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validPieceMoves = new ArrayList<>();
        for(ChessMove pieceMove:pieceMoves) {
            ChessBoard boardCopy = board.clone();
            ChessPosition newPosition = pieceMove.getEndPosition();
            board.addPiece(newPosition, piece);
            board.removePiece(pieceMove.getStartPosition());
            if (!isInCheck(piece.getTeamColor())){
                board = boardCopy.clone();
                validPieceMoves.add(pieceMove);
            }
            board = boardCopy.clone();
        }
        return validPieceMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (board.getPiece(move.getStartPosition())== null) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (validMoves.contains(move)&&piece.getTeamColor()==teamColorTurn) {
            if(piece.getPieceType()== ChessPiece.PieceType.PAWN && teamColorTurn ==TeamColor.WHITE&&move.getEndPosition().getRow()==8) {
                ChessPiece.PieceType promotion = move.getPromotionPiece();
                ChessPiece promotedPawn = new ChessPiece(teamColorTurn, promotion);
                board.addPiece(move.getEndPosition(), promotedPawn);
                board.removePiece(move.getStartPosition());
            }
            else if(piece.getPieceType()== ChessPiece.PieceType.PAWN && teamColorTurn ==TeamColor.BLACK&&move.getEndPosition().getRow()==1) {
                ChessPiece.PieceType promotion = move.getPromotionPiece();
                ChessPiece promotedPawn = new ChessPiece(teamColorTurn, promotion);
                board.addPiece(move.getEndPosition(), promotedPawn);
                board.removePiece(move.getStartPosition());
            }
            else {
                board.addPiece(move.getEndPosition(),piece);
                board.removePiece(move.getStartPosition());
                teamColorTurn = findOpposingTeamColor(piece.getTeamColor());
            }

        }
        else {
            throw new InvalidMoveException();
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor opposingTeam = findOpposingTeamColor(teamColor);
        Collection<Collection<ChessMove>> opposingTeamMoves = compileTeamPossibleMoves(opposingTeam);
        for(Collection<ChessMove> movesList:opposingTeamMoves) {
            for(ChessMove move: movesList) {
                ChessPiece pieceToCheck = board.getPiece(move.getEndPosition());
                if (pieceToCheck != null && pieceToCheck.getPieceType() == ChessPiece.PieceType.KING) {
                    if(pieceToCheck.getTeamColor()!=opposingTeam) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            Collection<Collection<ChessMove>> teamPossibleMoves = compileTeamPossibleMoves(teamColor);
            for (Collection<ChessMove> moveList: teamPossibleMoves) {
                for(ChessMove pieceMove: moveList) {
                    ChessBoard boardCopy = board.clone();
                    ChessPiece piece = board.getPiece(pieceMove.getStartPosition());
                    ChessPosition newPosition = pieceMove.getEndPosition();
                    board.addPiece(newPosition, piece);
                    board.removePiece(pieceMove.getStartPosition());
                    if (!isInCheck(teamColor)){
                        board = boardCopy.clone();
                        return false;
                    }
                    board = boardCopy.clone();
                }
            }
            return true;
        }
        return false;
    }


/**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            Collection<Collection<ChessMove>> teamPossibleMoves = compileTeamPossibleMoves(teamColor);
            for (Collection<ChessMove> moveList: teamPossibleMoves) {
                for(ChessMove pieceMove: moveList) {
                    ChessBoard boardCopy = board.clone();
                    ChessPiece piece = board.getPiece(pieceMove.getStartPosition());
                    ChessPosition newPosition = pieceMove.getEndPosition();
                    board.addPiece(newPosition, piece);
                    board.removePiece(pieceMove.getStartPosition());
                    if (!isInCheck(teamColor)){
                        board = boardCopy.clone();
                        return false;
                    }
                    board = boardCopy.clone();
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private Collection<Collection<ChessMove>> compileTeamPossibleMoves(TeamColor opposingTeam) {
        Collection<Collection<ChessMove>> compiledMoves = new ArrayList<>();
        for(int i=0;i<8;i++) {
            for(int j = 0; j<8; j++) {
                ChessPosition boardspace = new ChessPosition(i+1,j+1);
                if (board.getPiece(boardspace)!=null && board.getPiece(boardspace).getTeamColor()==opposingTeam) {
                    compiledMoves.add(board.getPiece(boardspace).pieceMoves(board, boardspace));
                }
            }
        }
        return compiledMoves;
    }

    private TeamColor findOpposingTeamColor(TeamColor playerColor) {
        TeamColor opposingTeam;
        if (playerColor == TeamColor.WHITE) {
            opposingTeam = TeamColor.BLACK;
        }
        else {
            opposingTeam = TeamColor.WHITE;
        }
        return opposingTeam;
    }

}
