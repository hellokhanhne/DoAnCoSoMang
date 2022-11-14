
package MangMayTinh.Chess.Model;

import java.awt.Point;
import java.io.Serializable;


public class Move implements Serializable {
    Point source;
    Point destination;

    public Move(Point source, Point destination) {
        this.source = source;
        this.destination = destination;
    }
    
    public Move clone() {
        Point source = (Point) this.source.clone();
        Point destination = (Point) this.destination.clone();
        Move newMove = new Move(source, destination);
        return newMove;
    }

    public Point getSource() {
        return source;
    }

    public void setSource(Point source) {
        this.source = source;
    }

    public Point getDestination() {
        return destination;
    }

    public void setDestination(Point destination) {
        this.destination = destination;
    }
}
