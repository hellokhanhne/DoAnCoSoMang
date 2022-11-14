
package MangMayTinh.Chess.Model.Interface;

import MangMayTinh.Chess.Model.Move;


public interface ChessboardInterface {
    void didMove(Move move);
    void didSendMessage(String message);
    void didClickCloseChessboard();
}
