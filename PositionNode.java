import java.util.ArrayList;
import java.util.Arrays;

public class PositionNode {
    final int[] position;
    ArrayList<PositionLink> nextPositions;
    ArrayList<PositionLink> previousPositions;
    double[] probabilityDistribution = new double[31];
    boolean probabilitiesInitialized = false;

    PositionNode(int[] position) {
        this.position = position;
        nextPositions = new ArrayList<>();
        previousPositions = new ArrayList<>();
    }

//    public void setPosition(int[] position) {
//        this.position = position;
//    }

    public int[] getPosition() {
        return position;
    }

    public int getTotalDistanceToWin() {
        int distance = 0;
        for (int i = 0; i < 7; i++) {
            distance += i * position[i];
        }
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionNode that = (PositionNode) o;
        return Arrays.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(position);
    }

    @Override
    public String toString() {
        return "PositionNode: " + position[0] + " " + position[1] + " " + position[2] + " " + position[3] + " " + position[4] + " " + position[5] + " " + position[6];
    }
    public void setProbability(int index, double value) {
        probabilityDistribution[index] = value;
    }

    public String printProbabilityDistribution() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= 30; i++) {
            sb.append(probabilityDistribution[i]);
            sb.append("   ");
        }
        return sb.toString();
    }
}
