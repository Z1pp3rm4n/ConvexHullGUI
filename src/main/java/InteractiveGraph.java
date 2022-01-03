import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

import java.util.*;
import java.util.stream.Collectors;

public class InteractiveGraph extends Pane {
    /** All vertices and edges in graph */
    private final ListProperty<Node> nodes = new SimpleListProperty<>(getChildren());

    /** History of actions */
    private final ListProperty<Action> history = new SimpleListProperty<>(FXCollections.observableArrayList());

    /** Used to store selected nodes */
    private Clipboard clipboard;

    /** Used to create temporary edge */
    private Vertex startVertex;
    private Line temporary;

    /** Graph with all features (clipboard, double click, draw edge) */
    public InteractiveGraph(){
        setStyle("-fx-border-color: black; -fx-min-height: 500; -fx-min-width: 500");

        enableClipboard();
        enableDoubleClickVertex();
        enableDrawEdge();
    }

    public Button getDeleteButton(){
        Button btDelete = new Button("Delete Selected");
        btDelete.setOnAction(e -> deleteSelected());
        btDelete.disableProperty().bind(clipboard.emptyProperty());

        return btDelete;
    }

    public Button getClearButton(){
        Button btClear = new Button("Clear");
        btClear.setOnAction(e -> clear());
        btClear.disableProperty().bind(nodes.emptyProperty());

        return btClear;
    }

    public Button getUndoButton(){
        Button btUndo = new Button("Undo");
        btUndo.setOnAction(e -> undo());
        btUndo.disableProperty().bind(history.emptyProperty());

        return btUndo;
    }

    public VBox getRandomVerticesBox(){
        TextField tfNumberInput = new TextField();
        Button btAddVertices = new Button("Random Vertices");
        EventHandler<ActionEvent> addVertices = e -> {
            String input = tfNumberInput.getText();
            if (!input.matches("\\d+") || input.matches("0+")) {
                tfNumberInput.setText("Must be valid positive number");
                return;
            }

            addRandomVertices(Integer.parseInt(input));
        };

        btAddVertices.disableProperty().bind(tfNumberInput.textProperty().isEmpty());
        btAddVertices.setOnAction(addVertices);
        tfNumberInput.setOnAction(addVertices);

        VBox buttonBox = new VBox(btAddVertices, tfNumberInput);
        buttonBox.setAlignment(Pos.CENTER);
        return buttonBox;
    }

    public Button getSolveButton(){
        Button btSolve = new Button("Solve");
        btSolve.setOnAction(e -> solveAndDisplay());

        return btSolve;
    }

    /** Delete selected nodes (in clipboard) */
    private void deleteSelected(){
        if (clipboard == null) {
            System.out.println("ERROR: Node selection not enabled");
            return;
        }

        Set<Node> toDelete = new HashSet<>();
        for (SelectableNode se: clipboard.getSelectedNodes()){
            toDelete.add((Node) se);
        }
        nodes.removeAll(toDelete);
        clipboard.clear();

        history.add(new Action(Action.DELETE, toDelete));
    }

    /** Clear all nodes */
    private void clear(){
        Set<Node> nodesCopy = new HashSet<>(nodes);
        nodes.clear();

        history.add(new Action(Action.DELETE, nodesCopy));
    }

    /** Undo last action */
    private void undo(){
        if (history.isEmpty()) {return;}

        Action a = history.remove(history.size()-1);
        switch (a.getType()){
            case Action.ADD:
                nodes.removeAll(a.getSubjects());
                break;
            case Action.DELETE:
                nodes.addAll(a.getSubjects());
                break;
            default:
                break;
        }
    }

    /** Add a number of vertices at random locations */
    private void addRandomVertices(int number){
        if (number <= 0) {
            System.out.println("ERROR: must be valid positive number");
            return;
        }

        final double max_x = getWidth() - Vertex.RADIUS;
        final double max_y = getHeight() - Vertex.RADIUS;

        Set<Vertex> vertices = new HashSet<>();
        for (int i = 0; i < number; i++){
            double x = Math.random() * max_x;
            double y = Math.random() * max_y;

            vertices.add(new Vertex(x,y));
        }

        addNodes(vertices);
    }

    /** Find and draw convex hull edges */
    private void solveAndDisplay(){
        Set<Edge> edges = findConvexHullEdges();
        if (edges.isEmpty()) { return;}
        addNodes(edges);
    }

    /** Find the set of convex hull edges */
    private Set<Edge> findConvexHullEdges(){
        Set<Vertex> vertices = getVertices();
        if (vertices.isEmpty()) {return Collections.emptySet();}

        Vertex[] verticesInHull = GraphTools.grahamScan(vertices);
        return getLinkEdges(verticesInHull);
    }

    /** Get linking edges between adjacent vertices */
    private Set<Edge> getLinkEdges(Vertex[] vertices){
        Set<Edge> edges = new HashSet<>();
        int size = vertices.length;
        for (int i = 0; i < size; i++){
            edges.add(
                    new Edge(vertices[i], vertices[(i + 1) % size])); // connect adjacent vertices.
        }

        return edges;
    }

    private Set<Vertex> getVertices(){
        return nodes.stream()
                .filter(n -> n instanceof Vertex)
                .map(n -> (Vertex) n)
                .collect(Collectors.toSet());
    }

    /** Enables a clipboard (select nodes)*/
    private void enableClipboard(){
        clipboard = new Clipboard(this);
        addEventHandler(MouseEvent.MOUSE_PRESSED, e -> clipboard.handleMousePressedEvent(e));
    }

    /** Enables double clicking graph to create a new vertex. */
    private void enableDoubleClickVertex(){
        addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getButton() == MouseButton.PRIMARY
                    && e.getClickCount() == 2
                    && e.getX() <= getWidth() - Vertex.RADIUS){

                addVertex(e.getX(), e.getY());
            }
        });
    }

    /**
     * Enables drawing an edge between two vertices by dragging between them.
     */
    private void enableDrawEdge(){
        // select starting vertex
        addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getTarget() instanceof Vertex) {
                startVertex = (Vertex) e.getTarget();
            }
        });

        // create temporary line on drag
        addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (startVertex != null){
                if (temporary == null) {    // initialise line
                    addTemporaryLine(startVertex.getCenterX(), startVertex.getCenterY(), e.getX(), e.getY());
                }

                temporary.setEndX(Math.min(e.getX(), getWidth()));
                temporary.setEndY(Math.min(e.getY(), getHeight())); // update temporary line
            }
        });

        // On mouse released: creates a new edge if two valid vertices are selected
        addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            Node picked = e.getPickResult().getIntersectedNode();

            if (picked instanceof Vertex
            && picked != startVertex
            && startVertex != null){
                addEdge(startVertex, (Vertex) picked);
            }

            nodes.remove(temporary);
            temporary = null;
            startVertex = null;
        });
    }
    /** Add given vertex to graph & save action to history */
    private void addVertex(double x, double y){
        Vertex v = new Vertex(x, y);
        nodes.add(v);

        history.add(new Action(Action.ADD, v));
    }

    /** Add given edge to graph & save action to history */
    private void addEdge(Vertex v1, Vertex v2){
        Edge e = new Edge(v1, v2);
        nodes.add(e);

        history.add(new Action(Action.ADD, e));
    }

    /** Add given nodes to graph & save action to history */
    private void addNodes(Collection<? extends Node> c){
        nodes.addAll(c);
        history.add(new Action(Action.ADD, c));
    }

    /** Create a dotted temporary line between two points */
    private void addTemporaryLine(double x1, double y1, double x2, double y2){
        temporary = new Line(x1,y1,x2,y2);
        temporary.setStyle("-fx-stroke-width: 5; -fx-stroke-dash-array: 15 15");
        temporary.setMouseTransparent(true);

        nodes.add(temporary);
    }
}
