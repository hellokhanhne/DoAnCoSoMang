
package MangMayTinh.Chess.Model.Interface;

import MangMayTinh.Chess.Model.Move;


public interface PlayerInterface {
    void setName(String name, boolean isFirstPlayer);
    void didReceiveMessage(String message, boolean isFirstPlayer);
    void move(Move move, boolean isFirstPlayer);
    void surrender(boolean isFirstPlayer);
    void endGame(boolean isFirstPlayer);
}
