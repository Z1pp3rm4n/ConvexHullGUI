import javafx.scene.Node;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Represents an action over a collect of nodes .
 */
public class Action {
    public static final int DELETE = 0;
    public static final int ADD = 1;

    private final int type;
    private final Collection<? extends Node> subjects;

    public Action(int type, Collection<? extends Node> subjects) {
        this.type = type;
        this.subjects = subjects;
    }

    public Action(int type, Node subject){
        this.type = type;
        subjects = Collections.singleton(subject);
    }

    public int getType() {
        return type;
    }

    public Collection<? extends Node> getSubjects() {
        return subjects;
    }
}
