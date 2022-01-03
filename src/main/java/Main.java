import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {

    @Override
    /**
     * Launch a ConvexHull JavaFx Scene (window)
     */
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Convex Hull");
        HBox pane = new HBox();

        // ensure size of graph
        InteractiveGraph graph = new InteractiveGraph();
        graph.prefWidthProperty().bind(pane.widthProperty().subtract(150));

        // pick buttons
        VBox buttonBox = new VBox(graph.getDeleteButton(),
                graph.getClearButton(),
                graph.getUndoButton(),
                graph.getRandomVerticesBox(),
                graph.getSolveButton());

        // pick button box location
        buttonBox.setPrefWidth(150);
        buttonBox.setSpacing(5);
        buttonBox.setAlignment(Pos.CENTER);

        // add all elements to the pane
        pane.getChildren().addAll(graph, buttonBox);

        primaryStage.setScene(new Scene(pane));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
