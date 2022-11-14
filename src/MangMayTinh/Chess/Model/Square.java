
package MangMayTinh.Chess.Model;

import java.awt.Color;
import java.awt.Point;
import javax.swing.JPanel;


public class Square extends JPanel {
    Color originalColor = Color.BLUE;
    Color secondaryColor = Color.GREEN;
    Point position;
    
    public static String getKey(Point point) {
        return "" + point.x + "" + point.y;
    }
    
    public String getKey() {
        return "" + position.x + "" + position.y;
    }
    
    public void turnToOriginalColor() {
        this.setBackground(originalColor);
    }
    
    public void turnToSecondaryColor() {
        this.setBackground(secondaryColor);
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Color getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(Color secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public Color getOriginalColor() {
        return originalColor;
    }
    
    public void setOriginalColor(Color originalColor) {
        this.originalColor = originalColor;
    }
}
