import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class Clipboard {
    private final ListProperty<SelectableNode> selectedNodes =
            new SimpleListProperty(FXCollections.observableArrayList());
    private final Parent root;

    public Clipboard(Parent root){
        this.root = root;
    }

    /**
     * Handles click-to-select in root node:
     * - Clears clipboard when clicking on non-selectable nodes.
     * - Reverse selection of selectable nodes.
    */
    public void handleMousePressedEvent(MouseEvent e){
        Node target = (Node) e.getTarget();

        if (target == root
        || !(target instanceof SelectableNode)) { clear();}
        else {
            SelectableNode se = (SelectableNode) target;
            select(se, !isSelected(se));    // reverse selection of target node
        }
    }
    public List<SelectableNode> getSelectedNodes(){ return selectedNodes;}

    /** Returns the clipboard's EmptyProperty (if there's any selected node) */
    public ReadOnlyBooleanProperty emptyProperty(){ return selectedNodes.emptyProperty();}

    /** Unselect all nodes */
    public void clear(){
        List<SelectableNode> toDelete = new ArrayList<>(selectedNodes);

        for (SelectableNode se: toDelete){
            select(se, false);
        }
    }

    private void select(SelectableNode se, boolean selected){
        if (selected){
            selectedNodes.add(se);
        } else {
            selectedNodes.remove(se);
        }

        se.notifySelection(selected);
    }

    private boolean isSelected(SelectableNode se){
        return selectedNodes.contains(se);
    }
}
