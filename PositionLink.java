import java.util.Arrays;

public class PositionLink {
    PositionNode node;
    int move1;
    int move2;
    public PositionLink(PositionNode node, int move1, int move2) {
        this.node = node;
        this.move1 = move1;
        this.move2 = move2;
    }
    public PositionLink(PositionNode node) {
        this.node = node;
    }
    public PositionLink(int[] position) {
        this.node = new PositionNode(position);
    }
    public void setMoves(int move1, int move2) {
        this.move1 = move1;
        this.move2 = move2;
    }
    public PositionNode getNode() {
        return node;
    }
    @Override
    public int hashCode() {
        return node.hashCode();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionLink that = (PositionLink) o;
        return node.equals(that);
    }
}
