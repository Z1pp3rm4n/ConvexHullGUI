import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Edge extends Line implements SelectableNode {
    public Edge(Vertex v1, Vertex v2){
        super(v1.getCenterX(), v1.getCenterY(), v2.getCenterX(), v2.getCenterY());
        setStyle("-fx-stroke-width: 5;");
    }

    @Override
    public void notifySelection(boolean selected) {
        if (selected){
            setStroke(Color.DARKRED);
        } else{
            setStroke(Color.BLACK);
        }
    }

}
