import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Vertex extends Circle implements SelectableNode{
    public static final double RADIUS = 15;
    public final double x;
    public final double y;

    public Vertex(double x, double y){
        super(x,y,RADIUS);
        this.x = x;
        this.y = y;
        setStyle("-fx-stroke-width: 5; -fx-fill: white; -fx-stroke: black");
    }

    @Override
    /**
     * Change Vertex color when selected / unselected
     */
    public void notifySelection(boolean selected) {
        if (selected){
            setFill(Color.DARKRED);
        } else {
            setFill(Color.WHITE);
        }
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
