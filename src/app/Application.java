package app;

import context.Context;
import entity.CellType;
import entity.GenericCell;
import entity.Node;
import entity.Pub;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import utils.TimeToStay;
import utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static utils.Utils.NUMBER_OF_ITERATIONS;
import static utils.Utils.OUTPUT_PATH;

public final class Application {

    private Context context;
    private final StringBuilder stringBuilder = new StringBuilder();

    public void start(Context context) {
        setContext(context);
        placeNodesToTheirHome();
        System.out.println("Relationships before: ");
        printRelationships();

        // simulate the time elapse starting at 6 o'clock
        // 1 month, but each iteration corresponds to 15 minutes
        int hour = 6;
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {

            if (i % 4 == 0 && hour < 24) {
                hour++;
            } else if (hour > 24) {
                hour = 0;
            }

            int finalHour = hour;
            context.getNodes().forEach(node -> {

                //update social network
                updateSocialNetwork();

                if (finalHour % 4 == 0) {
                    // update activity weight according to current hour
                    updateActivityWeight(node, finalHour);
                }

                // nodes are being moved based on their activity weight
                moveNode(node);
            });
        }

        System.out.println("Relationships after: ");
        printRelationships();

        try {
            Files.writeString(Path.of(OUTPUT_PATH), stringBuilder.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void moveNode(Node node) {
        // set the targetCell if it's the case
        if (node.getTargetCell() == null && node.getTimeToStay() == 0) {
            node.setTargetCell(getPlaceToGo(node));
            List<Queue<Pair<Integer, Integer>>> paths = Utils.findShortestPaths(context.getMap(),
                    new Pair<>(node.getCurrentCell().getXCoordinate(), node.getCurrentCell().getYCoordinate()),
                    new Pair<>(node.getTargetCell().getXCoordinate(), node.getTargetCell().getYCoordinate()),
                    2);

            node.setPath(paths.get(0));
        }

        if (node.getCurrentCell().equals(node.getTargetCell())) {
            // node has arrived to destination
            node.setTargetCell(null);
            node.setPath(null);
            node.setTimeToStay(TimeToStay.getMinutes(node.getCurrentCell()));
            System.out.println("Node[" + node.getId() + "] stays " + node.getTimeToStay() + " minutes");
            stringBuilder
                    .append("Node[")
                    .append(node.getId())
                    .append("] stays ")
                    .append(node.getTimeToStay())
                    .append(" minutes")
                    .append('\n');

        } else if (node.getTargetCell() == null && node.getTimeToStay() != 0) {
            // lower the timeToStay with one period time (one iteration = 15 minutes)
            node.setTimeToStay(Math.max(0, node.getTimeToStay() - 15));
            System.out.println("Node[" + node.getId() + "] stays " + node.getTimeToStay() + " minutes");
            stringBuilder
                    .append("Node[")
                    .append(node.getId())
                    .append("] stays ")
                    .append(node.getTimeToStay())
                    .append(" minutes")
                    .append('\n');

        } else if (node.getTargetCell() != null && node.getTimeToStay() == 0) {
            // node can move to a target
            moveNodeToTarget(node);
        }
    }

    private void moveNodeToTarget(Node node) {
        System.out.println("Node[" + node.getId() + "] moved from ["
                + node.getCurrentCell().getXCoordinate() + ", " + node.getCurrentCell().getYCoordinate() + "] to ["
                + node.getPath().peek().getFirst() + ", " + node.getPath().peek().getSecond() + "]; target: ["
                + node.getTargetCell().getXCoordinate() + ", " + node.getTargetCell().getYCoordinate() + "]->"
                + node.getTargetCell().getCellType());

        stringBuilder
                .append("Node[")
                .append(node.getId())
                .append("] moved from [")
                .append(node.getCurrentCell().getXCoordinate())
                .append(", ")
                .append(node.getCurrentCell().getYCoordinate())
                .append("] to [")
                .append(node.getPath().peek().getFirst())
                .append(", ")
                .append(node.getPath().peek().getSecond())
                .append("]; target: [")
                .append(node.getTargetCell().getXCoordinate())
                .append(", ")
                .append(node.getTargetCell().getYCoordinate())
                .append("]->")
                .append(node.getTargetCell().getCellType())
                .append("\n");

        node.setCurrentCell(context.getMap()[node.getPath().peek().getFirst()][node.getPath().poll().getSecond()]);


//        int currentX = node.getCurrentCell().getXCoordinate();
//        int currentY = node.getCurrentCell().getYCoordinate();
//        int targetX = node.getTargetCell().getXCoordinate();
//        int targetY = node.getTargetCell().getYCoordinate();
//
//        int deltaX = Math.abs(targetX - currentX);
//        int deltaY = Math.abs(targetY - currentY);
//
//        if (deltaX == 0 && deltaY == 0) {
//            // node has already reached its destination
//            System.out.println("Node[" + node.getId() + "] arrived to destination");
//
//        } else if (deltaX == 0) {
//            // move on oy axis
//            moveNodeOY(node, targetY, currentY, currentX);
//
//        } else if (deltaY == 0) {
//            // move on ox axis
//            moveNodeOX(node, targetX, currentX, currentY);
//
//        } else {
//            // move on ox or oy axis
//            if ((node.getSpeed() <= deltaX && deltaX <= deltaY) || (node.getSpeed() > deltaX && deltaX >= deltaY)) {
//                // move on ox axis
//                moveNodeOX(node, targetX, currentX, currentY);
//
//            } else if ((node.getSpeed() <= deltaY && deltaY <= deltaX) || (node.getSpeed() > deltaY && deltaY >= deltaX)) {
//                // move on oy axis
//                moveNodeOY(node, targetY, currentY, currentX);
//            }
//        }
    }

    private void moveNodeOX(Node node, int targetX, int currentX, int currentY) {
        if (targetX - currentX < 0) {
            // move to west
            node.setCurrentCell(context.getMap()[Math.max(0, currentX - node.getSpeed())][currentY]);
        } else {
            // move to east
            node.setCurrentCell(context.getMap()[Math.max(currentX + node.getSpeed(), context.getXDimension() - 1)][currentY]);
        }
        System.out.println("Node[" + node.getId() + "] moved from ["
                + currentX + ", " + currentY + "] to ["
                + node.getCurrentCell().getXCoordinate() + ", " + node.getCurrentCell().getYCoordinate() + "]; target: ["
                + node.getTargetCell().getXCoordinate() + ", " + node.getTargetCell().getYCoordinate() + "]->"
                + node.getTargetCell().getCellType());
        stringBuilder
                .append("Node[")
                .append(node.getId())
                .append("] moved from [")
                .append(currentX)
                .append(", ")
                .append(currentY)
                .append("] to [")
                .append(node.getCurrentCell().getXCoordinate())
                .append(", ")
                .append(node.getCurrentCell().getYCoordinate())
                .append("]; target: [")
                .append(node.getTargetCell().getXCoordinate())
                .append(", ")
                .append(node.getTargetCell().getYCoordinate())
                .append("]->")
                .append(node.getTargetCell().getCellType())
                .append("\n");
    }

    private void moveNodeOY(Node node, int targetY, int currentY, int currentX) {
        if (targetY - currentY < 0) {
            // move to south
            node.setCurrentCell(context.getMap()[currentX][Math.max(0, currentY - node.getSpeed())]);
        } else {
            // move to north
            node.setCurrentCell(context.getMap()[currentX][Math.min(currentY + node.getSpeed(), context.getYDimension() - 1)]);
        }

        System.out.println("Node[" + node.getId() + "] moved from ["
                + currentX + ", " + currentY + "] to ["
                + node.getCurrentCell().getXCoordinate() + ", " + node.getCurrentCell().getYCoordinate() + "]; target: ["
                + node.getTargetCell().getXCoordinate() + ", " + node.getTargetCell().getYCoordinate() + "]->"
                + node.getTargetCell().getCellType());
        stringBuilder
                .append("Node[")
                .append(node.getId())
                .append("] moved from [")
                .append(currentX)
                .append(", ")
                .append(currentY)
                .append("] to [")
                .append(node.getCurrentCell().getXCoordinate())
                .append(", ")
                .append(node.getCurrentCell().getYCoordinate())
                .append("]; target: [")
                .append(node.getTargetCell().getXCoordinate())
                .append(", ")
                .append(node.getTargetCell().getYCoordinate())
                .append("]->")
                .append(node.getTargetCell().getCellType())
                .append("\n");
    }

    private GenericCell getPlaceToGo(Node node) {
        List<Pair<CellType, Double>> events = new ArrayList<>();
        events.add(new Pair<>(CellType.HOME, node.getActivityWeight().get(CellType.HOME) / 100D));
        events.add(new Pair<>(CellType.WORK, node.getActivityWeight().get(CellType.WORK) / 100D));
        events.add(new Pair<>(CellType.PUB, node.getActivityWeight().get(CellType.PUB) / 100D));
        events.add(new Pair<>(CellType.OTHER, node.getActivityWeight().get(CellType.OTHER) / 100D));

        EnumeratedDistribution<CellType> distribution = new EnumeratedDistribution<>(events);

        CellType selectedEvent = distribution.sample();
        System.out.println("Selected: " + selectedEvent);

        return getTargetCellBasedOnCellType(node, selectedEvent);
    }

    private void updateActivityWeight(Node node, int hour) {
        if (hour >= 6 && hour <= 10) {
            context.getPubs().forEach(pub -> {
                if (getNrFriendsInPubs(node, context) > 0) {
                    node.getActivityWeight().put(CellType.PUB, 10);
                    node.getActivityWeight().put(CellType.HOME, 10);
                } else {
                    node.getActivityWeight().put(CellType.PUB, 5);
                    node.getActivityWeight().put(CellType.HOME, 15);
                }
            });
            node.getActivityWeight().put(CellType.WORK, 70);
            node.getActivityWeight().put(CellType.OTHER, 10);
        } else if (hour >= 11 && hour <= 18) {
            node.getActivityWeight().put(CellType.HOME, 10);
            node.getActivityWeight().put(CellType.PUB, 5);
            node.getActivityWeight().put(CellType.WORK, 65);
            node.getActivityWeight().put(CellType.OTHER, 20);
        } else if (hour >= 19 && hour <= 24) {
            context.getPubs().forEach(pub -> {
                if (getNrFriendsInPubs(node, context) > 0) {
                    node.getActivityWeight().put(CellType.PUB, 40);
                    node.getActivityWeight().put(CellType.HOME, 40);
                } else {
                    node.getActivityWeight().put(CellType.PUB, 25);
                    node.getActivityWeight().put(CellType.HOME, 55);
                }
            });
            node.getActivityWeight().put(CellType.WORK, 5);
            node.getActivityWeight().put(CellType.OTHER, 15);
        } else {
            node.getActivityWeight().put(CellType.HOME, 90);
            node.getActivityWeight().put(CellType.PUB, 5);
            node.getActivityWeight().put(CellType.WORK, 2);
            node.getActivityWeight().put(CellType.OTHER, 3);
        }
    }

    private void placeNodesToTheirHome() {
        context.getNodes().forEach(node -> node.setCurrentCell(node.getHomeCell()));
    }

    private int getNrFriendsInPubs(Node node, Context context) {
//        AtomicInteger nr = new AtomicInteger(0);

        return (int) context.getPubs().stream()
                .flatMap(pub -> pub.getPresentNodes().stream())
                .filter(node.getFriends()::contains)
                .count();

//        context.getPubs().forEach(pub ->
//                node.getFriends().forEach(friend -> {
//                    if (pub.getPresentNodes().contains(friend)) {
//                        nr.getAndIncrement();
//                    }
//                })
//        );
//
//        return nr.get();
    }

    private GenericCell getTargetCellBasedOnCellType(Node node, CellType cellType) {
        switch (cellType) {
            case HOME:
                return node.getHomeCell();
            case WORK:
                return node.getWorkCell();
            case PUB:
                return chooseTargetPub(node);
            case OTHER:
                return chooseOtherPlaceToGo(node);
            default:
                return null;
        }
    }

    private Pub chooseTargetPub(Node node) {
        // TODO: de reparat
//        AtomicReference<Pub> targetPub = new AtomicReference<>();
//        AtomicInteger numberOfFriendsInPub = new AtomicInteger(0);
        return context.getPubs().stream()
                .max(Comparator.comparingInt(pub -> {
                    Set<Node> commonFriends = new HashSet<>(pub.getPresentNodes());
                    commonFriends.retainAll(node.getFriends());
                    return commonFriends.size();
                }))
                .orElse(null);

        //        context.getPubs()
//                .forEach(pub -> {
//                    Set<Node> commonFriends = new HashSet<>(pub.getPresentNodes());
//                    commonFriends.retainAll(node.getFriends());
//                    if (commonFriends.size() >= numberOfFriendsInPub.get()) {
//                        numberOfFriendsInPub.set(commonFriends.size());
//                        targetPub.set(pub);
//                    }
//                });
//
//        return targetPub.get();
    }

    private GenericCell chooseOtherPlaceToGo(Node node) {
        // there are two ways to choose other place to go
        // 1. nearest place marked as 'other' on map
        // 2. random
        // will make the decision on what way is chosen random

        int randomNumber = new Random().nextInt(2);

        if (randomNumber == 0) {
            return getNearestOtherPlaceToGo(node);
        } else {
            return getRandomOtherPlaceToGo();
        }
    }

    private GenericCell getNearestOtherPlaceToGo(Node node) {
        // select nearest place to go based on Euclidean distance
//        AtomicReference<GenericCell> resultCell = new AtomicReference<>();
        int currentX = node.getCurrentCell().getXCoordinate();
        int currentY = node.getCurrentCell().getYCoordinate();

//        AtomicReference<Double> minDistance = new AtomicReference<>(Double.MAX_VALUE);
//        context.getOthers().forEach(other -> {
//            double euclideanDistance = Math.sqrt(Math.pow(other.getXCoordinate() - currentX, 2)
//                                               + Math.pow(other.getYCoordinate() - currentY, 2));
//
//            if (euclideanDistance < minDistance.get()) {
//                minDistance.set(euclideanDistance);
//                resultCell.set(other);
//            }
//        });

        return context.getOthers().stream()
                .min(Comparator.comparingDouble(other ->
                        Math.sqrt(Math.pow(other.getXCoordinate() - currentX, 2)
                                + Math.pow(other.getYCoordinate() - currentY, 2))))
                .orElse(null);
    }

    private GenericCell getRandomOtherPlaceToGo() {
        Set<GenericCell> others = context.getOthers();

        if (others.isEmpty()) {
            return null;
        }

        int randomNumber = new Random().nextInt(others.size());

        return others.stream().skip(randomNumber).findFirst().orElse(null);

//        GenericCell randomCell = null;
//        int seed = context.getOthers().size();
//
//        if (seed > 0) {
//            int randomNumber = new Random().nextInt(seed);
//            randomCell = new ArrayList<>(context.getOthers()).get(randomNumber);
//        }
//
//        return randomCell;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    private void printRelationships() {
        context.getNodes().forEach(node -> {
            System.out.print(node.getId() + ": ");
            node.getFriends().forEach(friend -> System.out.print(friend.getId() + ", "));
            System.out.println();
        });
        System.out.println();
    }

    private void updateSocialNetwork() {
        context.getNodes().forEach(node -> {
            if (node.getCurrentCell().getCellType() == CellType.PUB) {
                System.out.println("N[" + node.getId() + "] este in pub");
                node.getFriends().forEach(friend -> {
                    if (friend.getCurrentCell().equals(node.getCurrentCell())) {
                        System.out.println("prietenul N[" + friend.getId() + "] este in pub");
                        friend.getFriends().forEach(friendOfFriend -> {
                            if (friendOfFriend.getCurrentCell().equals(node.getCurrentCell())) {
                                // sanse de 50% sa devina prieten cu nodul initial
                                System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa");
                            }
                        });
                    }
                });
            }
        });
    }
}
