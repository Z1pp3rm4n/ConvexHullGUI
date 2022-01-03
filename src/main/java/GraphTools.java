import java.util.*;
import java.util.stream.Collectors;

public class GraphTools {
    /** Find ordered list of vertices on the convex hull. Input must be unempty */
    public static Vertex[] grahamScan(Collection<Vertex> vertices){
        Vertex lowest = lowestLeftmostVertex(vertices);

        Vertex[] sorted = sortedByAngle(vertices, lowest);
        Deque<Vertex> stack = new LinkedList<>();

        for (Vertex v: sorted){
            while (stack.size() > 1){       // While the 2 top vertices and new vertex form clockwise rotation,
                Vertex top = stack.pop();   // remove the top vertex
                Vertex nextToTop = stack.peek();

                if (ccw(nextToTop, top, v)){   // If the 3 vertices form ccw, push back the top vertex to stack
                    stack.push(top);
                    break;
                }
            }

            stack.push(v);
        }

        return stack.toArray(Vertex[]::new);
    }

    /**
     * Returns the lowest vertex. If there are multiple, return the leftmost one
     * In other words, find vertex with minx, min y
     */
    private static Vertex lowestLeftmostVertex(Collection<Vertex> c){
        return c.stream().min((v1, v2) -> (int) (v1.y - v2.y != 0
                ? v1.y - v2.y
                : v1.x - v2.x)).get();
    }

    /**
     * Returns a sorted array of vertices according to their polar angle with a base-vertex.
     * If more vertices share the same angle, take only the furthest.
     *
     * @param vertices set of vertices
     * @param base base vertex
     * @return array of vertices sorted on polar angle
     */
    private static Vertex[] sortedByAngle(Collection<Vertex> vertices, Vertex base){
        Map<Double, Vertex> polarAngletoVertex = new HashMap<>();

        for (Vertex v: vertices){
            double angle = polarAngle(base, v);
            Vertex inMap = polarAngletoVertex.get(angle);
            if (inMap == null
                    || dist(base, v) > dist(base, inMap)) {
                polarAngletoVertex.put(angle, v);   // get furthest vertex by each polar angle
            }
        }

        return polarAngletoVertex.entrySet()
                .stream()
                .sorted(Comparator.comparingDouble(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .toArray(Vertex[]::new);
    }

    /** Returns the polar angle made by vertex v2, v1 and the x-axis. */
    private static double polarAngle(Vertex v1, Vertex v2){ //
        return Math.acos((v2.x - v1.x)    // cos-1( (x2 - x1)
                / Math.sqrt(Math.pow(v2.x - v1.x, 2) + Math.pow(v2.y - v1.y, 2))); // div sqrt((x2-x1)^2 + (y2-y1)^2
    }

    /** Returns the distance between vertex v1, v2 */
    private static double dist(Vertex v1, Vertex v2){
        return Math.sqrt(Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2)); //sqrt (x1 - x2)^2 + (y1 - y2)^2
    }

    /**  Returns whether 3 vertices form counter-clockwise rotation. */
    private static boolean ccw(Vertex v1, Vertex v2, Vertex v3){
        return (v2.x - v1.x)*(v3.y - v1.y)
                > (v2.y - v1.y)*(v3.x - v1.x); // (x2-x1)(y3-y1) - (y2-y1)(x3-x1)
    }

}
