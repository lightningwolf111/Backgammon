import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class BearingOffSolver {
    static ConcurrentHashMap<Integer, PositionNode> nodes = new ConcurrentHashMap<>();
    static int generated = 0;
    final static PositionNode root = new PositionNode(new int[]{15, 0, 0, 0, 0, 0, 0});
    final static double EPSILON = 0.00000000001;

    public static void main(String[] args) {
        //PositionNode root = buildTree();

        //generateNextNodes(nodes.get(new PositionNode(new int[]{0, 0, 0, 0, 0, 0, 15}).hashCode()));
        nodes.put(root.hashCode(), root);
        generateAllPositions(root);
        System.out.println(nodes.size() + " nodes generated.");
        for (Integer i : nodes.keySet()) {
            for (int move1 = 6; move1 > 0; move1--) {
                for (int move2 = move1; move2 > 0; move2--) {
                    getNextPositions(nodes.get(i), move1, move2);
                }
            }
        }
        System.out.println("Tree links populated.");
        for (Integer i : nodes.keySet()) {
            removeDuplicates(nodes.get(i).previousPositions);
            removeDuplicates(nodes.get(i).nextPositions);
        }
        System.out.println("Starting on probability computations.");
        nodes.get(root.hashCode()).setProbability(0, 1);
        nodes.get(root.hashCode()).probabilitiesInitialized = true;
        updateProbabilityDistributions(root);
        System.out.println("Distributions Updated: " + nodes.get(new PositionNode(new int[]{14, 1, 0, 0, 0, 0, 0}).hashCode()).printProbabilityDistribution() + " " + nodes.get(new PositionNode(new int[]{14, 1, 0, 0, 0, 0, 0}).hashCode()).probabilitiesInitialized);

//        generateNextNodes(new PositionNode(new int[]{0, 0, 0, 0, 0, 0, 15}));
//        Random random = new Random();
//
//        int move1 = random.nextInt(6) + 1;
//        int move2 = random.nextInt(6) + 1;

//        PositionNode current = root;
//        while (current.previousPositions.size() > 0) {
//            System.out.println("Moving to: " + current);
//            current = current.previousPositions.get(0);
//        }

    }

    public static PositionNode buildTree() {
        PositionNode root = new PositionNode(new int[]{15, 0, 0, 0, 0, 0, 0});
        nodes.put(root.hashCode(), root);
        //root.previousPositions = generatePreviousNodes(root);
        return root;
    }
    public static ArrayList<PositionNode> generatePreviousNodes(final PositionNode node) {
        int largestReverseMovePossible = getLargestReverseMovePossible(node);
        ArrayList<PositionNode> previousPositions = new ArrayList<>();
        for (int move1 = 1; move1 <= largestReverseMovePossible; move1++) {
            for (int move2 = 1; move2 <= largestReverseMovePossible; move2++) {
                previousPositions.addAll(getPreviousPositions(node, move1, move2));
            }
        }
        //previousPositions = removeDuplicates(previousPositions);
        generated++;
        if (generated%1000 == 0) {
            System.out.println(generated);
        }
//        for (PositionNode prevNode : previousPositions) {
//            prevNode.previousPositions = generatePreviousNodes(prevNode);
//        }
        return previousPositions;
    }
    public static ArrayList<PositionNode> getPreviousPositions(final PositionNode position, int move1, int move2) {
        ArrayList<PositionNode> previousPositions = new ArrayList<>();
        if (move1 == move2) { // Double
            for (PositionNode intermediatePos1 : getPreviousPositions(position, move1)) {
                //System.out.println("InterPos1: " + intermediatePos1.toString());
                for (PositionNode intermediatePos2 : getPreviousPositions(intermediatePos1, move1)) {
                    //System.out.println("InterPos2: " + intermediatePos2.toString());
                    for (PositionNode intermediatePos3 : getPreviousPositions(intermediatePos2, move1)) {
                        //System.out.println("InterPos3: " + intermediatePos3.toString());
                        previousPositions.addAll(getPreviousPositions(intermediatePos3, move1));
                    }
                }
            }
        } else {
            for (PositionNode intermediatePos : getPreviousPositions(position, move1)) {
                previousPositions.addAll(getPreviousPositions(intermediatePos, move2));
            }
        }

        //previousPositions = removeDuplicates(previousPositions);

        ArrayList<PositionNode> toAdd = new ArrayList<>();
        ArrayList<PositionNode> toRemove = new ArrayList<>();
        for (PositionNode node : previousPositions) {
            if (nodes.containsKey(node.hashCode())) {
                toAdd.add(nodes.get(node.hashCode()));
                toRemove.add(node);
            } else {
                nodes.put(node.hashCode(), node);
            }
        }
        previousPositions.addAll(toAdd);
        previousPositions.removeAll(toRemove);

        //position.previousPositions = previousPositions;
        return previousPositions;
    }

    public static ArrayList<PositionNode> getPreviousPositions(final PositionNode position, int move) {
        //final int[] pos = position.getPosition();
        ArrayList<PositionNode> prevPositions = new ArrayList<>();
        int[] nextPos;
        for (int i = 6 - move; i >= 0; i--) {
            if (position.getPosition()[i] > 0) {
                nextPos = position.getPosition().clone();
                nextPos[i]--;
                nextPos[i + move]++;
                //System.out.println("Move " + move);
                //System.out.println("I: " + i + " " + position.toString() + " Position: " + nextPos[0] + " " + nextPos[1] + " " + nextPos[2] + " " + nextPos[3] + " " + nextPos[4] + " " + nextPos[5] + " " + nextPos[6]);
                prevPositions.add(new PositionNode(nextPos));
            }
        }
        return prevPositions;
    }

    public static ArrayList removeDuplicates(ArrayList<PositionLink> list) {
        ArrayList toReturn = new ArrayList<>();
        for (Object p : list) {
            if (!toReturn.contains(p)) {
                toReturn.add(p);
            }
        }
        return toReturn;
    }


    public static int getLargestReverseMovePossible(PositionNode positionNode) {
        int largestReverseMovePossible = 6;
        while (positionNode.getPosition()[6 - largestReverseMovePossible] == 0) {
            largestReverseMovePossible--;
        }
        return largestReverseMovePossible;
    }

//    public static ArrayList<PositionNode> generateNextNodes(final PositionNode node) {
//        if (node.equals(new PositionNode(new int[]{15, 0, 0, 0, 0, 0, 0}))) {
//            return null;
//        }
//        ArrayList<PositionNode> nextPositions = new ArrayList<>();
//        for (int move1 = 1; move1 <= 6; move1++) {
//            for (int move2 = 1; move2 <= 6; move2++) {
//                nextPositions.addAll(getNextPositions(node, move1, move2));
//            }
//        }
//        nextPositions = removeDuplicates(nextPositions);
//        generated++;
//        //if (generated%1000 == 0) {
//            System.out.println(generated);
//            System.out.println(node + " and next is: " + nextPositions);
//        //}
////        for (PositionNode nextNode : nextPositions) {
////            nextNode.nextPositions = generateNextNodes(nextNode);
////        }
//        return nextPositions;
//    }

    public static ArrayList<PositionLink> getNextPositions(final PositionNode position, int move1, int move2) {
        ArrayList<PositionLink> nextPositions = new ArrayList<>();
        if (move1 == move2) { // Double
            for (PositionLink intermediatePos1 : getNextPositions(position, move1)) {
                //System.out.println("InterPos1: " + intermediatePos1.toString());
                for (PositionLink intermediatePos2 : getNextPositions(intermediatePos1, move1)) {
                    //System.out.println("InterPos2: " + intermediatePos2.toString());
                    for (PositionLink intermediatePos3 : getNextPositions(intermediatePos2, move1)) {
                        //System.out.println("InterPos3: " + intermediatePos3.toString());
                        nextPositions.addAll(getNextPositions(intermediatePos3, move1));
                    }
                }
            }
        } else {
            for (PositionLink intermediatePos : getNextPositions(position, move1)) {
                nextPositions.addAll(getNextPositions(intermediatePos, move2));
            }
        }

        nextPositions = removeDuplicates(nextPositions);
        //System.out.println(position + " NextPOS: " + nextPositions);
        ArrayList<PositionNode> toAdd = new ArrayList<>();
        ArrayList<PositionLink> toRemove = new ArrayList<>();
        for (PositionLink node : nextPositions) {
            if (nodes.containsKey(node.hashCode())) {
                toAdd.add(nodes.get(node.hashCode()));
                toRemove.add(node);
            } else {
                nodes.put(node.hashCode(), node.getNode());
            }
        }
        for (PositionNode node : toAdd) {
            nextPositions.add(new PositionLink(node));
        }
        nextPositions.removeAll(toRemove);

//        for (PositionLink l : nextPositions) {
//            if (l.hashCode() == root.hashCode()) {
//                System.out.println("Root is in next Positions.");
//            }
//        }

        for (PositionLink link : nextPositions) {
            link.setMoves(move1, move2);
            PositionNode l = nodes.get(link.hashCode());
            l.previousPositions.add(new PositionLink(position, move1, move2));
            nodes.put(l.hashCode(), l);
        }
        //System.out.println(position + " NextPOS1: " + nextPositions);
        position.nextPositions = nextPositions;
        nodes.put(position.hashCode(), position);
        //System.out.println(nextPositions.size());
        return nextPositions;
    }

    public static ArrayList<PositionLink> getNextPositions(final PositionNode position, int move) { //TODO: currently this allows for non-legal combinations, where a number can be used as less. Shouldn't impact optimal play.
        ArrayList<PositionLink> nextPositions = new ArrayList<>();
        int[] nextPos;
        for (int i = move; i <= 6 ; i++) {
            if (position.getPosition()[i] > 0) {
                nextPos = position.getPosition().clone();
                nextPos[i]--;
                nextPos[i - move]++;
                //System.out.println("Move " + move);
                //System.out.println("I: " + i + " " + position.toString() + " Position: " + nextPos[0] + " " + nextPos[1] + " " + nextPos[2] + " " + nextPos[3] + " " + nextPos[4] + " " + nextPos[5] + " " + nextPos[6]);
                nextPositions.add(new PositionLink(nextPos));
            }
        }
        if (nextPositions.isEmpty()) {
            for (int i = move - 1; i > 0; i--) {
                if (position.getPosition()[i] > 0) {
                    nextPos = position.getPosition().clone();
                    nextPos[i]--;
                    nextPos[0]++;
                    //System.out.println("Move " + move);
                    //System.out.println("I: " + i + " " + position.toString() + " Position: " + nextPos[0] + " " + nextPos[1] + " " + nextPos[2] + " " + nextPos[3] + " " + nextPos[4] + " " + nextPos[5] + " " + nextPos[6]);
                    nextPositions.add(new PositionLink(nextPos));
                }
            }
        }
        return nextPositions;
    }

    public static ArrayList<PositionLink> getNextPositions(final PositionLink position, int move) { //TODO: currently this allows for non-legal combinations, where a number can be used as less. Shouldn't impact optimal play.
        ArrayList<PositionLink> nextPositions = new ArrayList<>();
        int[] nextPos;
        for (int i = move; i <= 6 ; i++) {
            if (position.getNode().getPosition()[i] > 0) {
                nextPos = position.getNode().getPosition().clone();
                nextPos[i]--;
                nextPos[i - move]++;
                //System.out.println("Move " + move);
                //System.out.println("I: " + i + " " + position.toString() + " Position: " + nextPos[0] + " " + nextPos[1] + " " + nextPos[2] + " " + nextPos[3] + " " + nextPos[4] + " " + nextPos[5] + " " + nextPos[6]);
                nextPositions.add(new PositionLink(nextPos));
            }
        }
        if (nextPositions.isEmpty()) {
            for (int i = move - 1; i > 0; i--) {
                if (position.getNode().getPosition()[i] > 0) {
                    nextPos = position.getNode().getPosition().clone();
                    nextPos[i]--;
                    nextPos[0]++;
                    //System.out.println("Move " + move);
                    //System.out.println("I: " + i + " " + position.toString() + " Position: " + nextPos[0] + " " + nextPos[1] + " " + nextPos[2] + " " + nextPos[3] + " " + nextPos[4] + " " + nextPos[5] + " " + nextPos[6]);
                    nextPositions.add(new PositionLink(nextPos));
                }
            }
        }
        return nextPositions;
    }

    public static void generateAllPositions(PositionNode root) {
        generated++;
//        if (generated%1000 == 0) {
//            System.out.println(generated);
//        }
        ArrayList<PositionNode> next = new ArrayList<>();
        for (int move1 = 1; move1 <= 6; move1++) {
            next.addAll(getPreviousPositions(root, move1));
        }
        for (PositionNode n : next) {
            if (!nodes.containsKey(n.hashCode())) {
                nodes.put(n.hashCode(), n);
                generateAllPositions(n);
            }
        }
    }

    public static void updateProbabilityDistributions(PositionNode node) {
        System.out.println("Updating Dists for: " + node);
        if (!node.nextPositions.isEmpty()) {
            int totalPossibilities = 0;
            for (PositionLink link : node.nextPositions) {
                if (link.move1 == link.move2) {
                    totalPossibilities += 2;
                } else {
                    totalPossibilities++;
                }
            }
            for (PositionLink link : node.nextPositions) {
                if (!link.node.probabilitiesInitialized) {
                    updateProbabilityDistributions(link.getNode());
                }
            }
            ArrayList<PositionLink> linksForThrow = new ArrayList<>();
            for (int move1 = 6; move1 > 0; move1--) {
                for (int move2 = move1; move2 > 0; move2--) {
                    for (PositionLink link : node.nextPositions) {
                        if (link.move1 == move1 && link.move2 == move2) {
                            linksForThrow.add(link);
                        }
                    }
                    PositionLink best = findBestLink(linksForThrow);
                    int chancesToRoll = 1;
                    if (move1 == move2) {
                        chancesToRoll = 2;
                    }
                    updateDistribution(node, best, chancesToRoll, totalPossibilities);
                }
            }
        }
        node.probabilitiesInitialized = true;
        for (PositionLink link : node.previousPositions) {
            updateProbabilityDistributions(link.getNode());
        }
    }

    public static PositionLink findBestLink(ArrayList<PositionLink> links) {
        PositionLink bestLink = links.get(0);
        for (PositionLink link : links) {
            boolean bestWins = true;
            boolean unclear = false;
            double cumulativeProbBest = 0;
            double cumulativeProbLink = 0;
            for (int i = 0; i <= 30; i++) {
                cumulativeProbBest += bestLink.getNode().probabilityDistribution[i];
                cumulativeProbLink += link.getNode().probabilityDistribution[i];
                if (bestWins && cumulativeProbLink > cumulativeProbBest + EPSILON) {
                    bestWins = false;
                }
                if (!bestWins && cumulativeProbBest > cumulativeProbLink + EPSILON) {
                    unclear = true;
                    System.out.println("UNCLEAR which continuation is best bestween " + bestLink.getNode() + " and " + link.getNode() + ".");
                }
            }
            if (!bestWins) {
                bestLink = link;
            }
        }
        return bestLink;
    }
    public static void updateDistribution(PositionNode node, PositionLink nextNode, int chancesToRoll, int totalRollsPossible) {
        for (int i = 1; i <= 30; i++) {
            node.probabilityDistribution[i] += nextNode.getNode().probabilityDistribution[i - 1] * chancesToRoll / totalRollsPossible;
        }
    }
}
