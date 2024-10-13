package app;

import context.Context;
import entity.CellType;
import entity.GenericCell;
import entity.Node;
import entity.Pub;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import utils.TimeToStay;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class Application {

    private Context context;

    public void start(Context context) {
        setContext(context);
        placeNodesToTheirHome(context);

        // simulate the time elapse starting at 6 o'clock
        // 24h but each iteration corresponds to 15 minutes
        for (int t = 6; t < 220; t++) {

            int finalT = t;
            context.getNodes().forEach(node -> {

                //update social network
//                updateSocialNetwork(context);

                if (finalT % 4 == 0) {
                    // update activity weight according to current hour
                    updateActivityWeight(node, finalT);
                }

                // nodes are being moved based on their activity weight
                // TODO: movement on diag + obstacles avoiding
                moveNode(node, finalT);
            });
        }

    }

    private void moveNode(Node node, int t) {
        // set the targetCell if it is the case
        if (node.getTargetCell() == null && node.getTimeToStay() == 0) {
            node.setTargetCell(getPlaceToGo(node));
        }

        if (node.getCurrentCell() == node.getTargetCell()) {
            // node has arrived to destination
            node.setTargetCell(null);
            node.setTimeToStay(TimeToStay.getMinutes(node.getCurrentCell()));
            System.out.println("Node[" + node.getId() + "] stays " + node.getTimeToStay() + " minutes");

        } else if (node.getTargetCell() == null && node.getTimeToStay() != 0) {
            // lower the timeToStay with one period time (one iteration = 15 minutes)
            node.setTimeToStay(node.getTimeToStay() - 15);
            System.out.println("Node[" + node.getId() + "] stays " + node.getTimeToStay() + " minutes");

        } else if (node.getTargetCell() != null && node.getTimeToStay() == 0) {
            // node can move to a target
            moveNodeToTarget(node);
        }

    }

    private void moveNodeToTarget(Node node) {
        int currentX = node.getCurrentCell().getXCoordinate();
        int currentY = node.getCurrentCell().getYCoordinate();
        int targetX = node.getTargetCell().getXCoordinate();
        int targetY = node.getTargetCell().getYCoordinate();

        int deltaX = Math.abs(targetX - currentX);
        int deltaY = Math.abs(targetY - currentY);

        if (deltaX == 0 && deltaY == 0) {
            // node has already reached its destination
            System.out.println("Node[" + node.getId() + "] arrived to destination");

        } else if (deltaX == 0) {
            // move on oy axis
            moveNodeOY(node, targetY, currentY, currentX);

        } else if (deltaY == 0) {
            // move on ox axis
            moveNodeOX(node, targetX, currentX, currentY);

        } else {
            // move on ox or oy axis
            if ((node.getSpeed() <= deltaX && deltaX <= deltaY) || (node.getSpeed() > deltaX && deltaX >= deltaY)) {
                // move on ox axis
                moveNodeOX(node, targetX, currentX, currentY);

            } else if ((node.getSpeed() <= deltaY && deltaY <= deltaX) || (node.getSpeed() > deltaY && deltaY >= deltaX)) {
                // move on oy axis
                moveNodeOY(node, targetY, currentY, currentX);
            }
        }

        System.out.println("Node[" + node.getId() + "] moved from ["
                + currentX + ", " + currentY + "] to ["
                + node.getCurrentCell().getXCoordinate() + ", " + node.getCurrentCell().getYCoordinate() + "]; target: ["
                + node.getTargetCell().getXCoordinate() + ", " + node.getTargetCell().getYCoordinate() + "]->"
                + node.getTargetCell().getCellType());

    }

    private void moveNodeOX(Node node, int targetX, int currentX, int currentY) {
        if (targetX - currentX < 0) {
            // move to west
            node.setCurrentCell(context.getMap()[Math.max(0, currentX - node.getSpeed())][currentY]);
        } else {
            // move to east
            node.setCurrentCell(context.getMap()[Math.max(currentX + node.getSpeed(), context.getXDimension() - 1)][currentY]);
        }
    }

    private void moveNodeOY(Node node, int targetY, int currentY, int currentX) {
        if (targetY - currentY < 0) {
            // move to south
            node.setCurrentCell(context.getMap()[currentX][Math.max(0, currentY - node.getSpeed())]);
        } else {
            // move to north
            node.setCurrentCell(context.getMap()[currentX][Math.min(currentY + node.getSpeed(), context.getYDimension() - 1)]);
        }
    }

    private GenericCell getPlaceToGo(Node node) {
        int randomProbability = new Random().nextInt(100) + 1;

        List<Pair<CellType, Double>> events = new ArrayList<>();
        events.add(new Pair<>(CellType.HOME, node.getActivityWeight().get(CellType.HOME) / 100D));
        events.add(new Pair<>(CellType.WORK, node.getActivityWeight().get(CellType.WORK) / 100D));
        events.add(new Pair<>(CellType.PUB, node.getActivityWeight().get(CellType.PUB) / 100D));
        events.add(new Pair<>(CellType.OTHER, node.getActivityWeight().get(CellType.OTHER) / 100D));

        EnumeratedDistribution<CellType> distribution = new EnumeratedDistribution<>(events);

        CellType selectedEvent = distribution.sample();
        System.out.println("Selected: " + selectedEvent);

//        int cumulativeProbabilities = 0;
//        for (Map.Entry<CellType, Integer> entry : node.getActivityWeight().entrySet()) {
//            cumulativeProbabilities += entry.getValue();
//            if (randomProbability <= cumulativeProbabilities) {
//                return getTargetCellBasedOnCellType(node, entry.getKey());
//            }
//        }

        return  getTargetCellBasedOnCellType(node, selectedEvent);
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

    private void placeNodesToTheirHome(Context context) {
        context.getNodes()
                .forEach(node -> node.setCurrentCell(node.getHomeCell()));
    }

    private int getNrFriendsInPubs(Node node, Context context) {
        AtomicInteger nr = new AtomicInteger(0);

        context.getPubs().forEach(pub ->
            node.getFriends().forEach(friend -> {
                if (pub.getPresentNodes().contains(friend)) {
                    nr.getAndIncrement();
                }
            })
        );

        return nr.get();
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
            default: return null;
        }
    }

    private Pub chooseTargetPub(Node node) {
        AtomicReference<Pub> targetPub = new AtomicReference<>();
        AtomicInteger numberOfFriendsInPub= new AtomicInteger(0);

        context.getPubs()
                .forEach(pub -> {
                    Set<Node> commonFriends = new HashSet<>(pub.getPresentNodes());
                    commonFriends.retainAll(node.getFriends());
                    if (commonFriends.size() >= numberOfFriendsInPub.get()) {
                        numberOfFriendsInPub.set(commonFriends.size());
                        targetPub.set(pub);
                    }
                });

        return targetPub.get();
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
        AtomicReference<GenericCell> resultCell = new AtomicReference<>();
        int currentX = node.getCurrentCell().getXCoordinate();
        int currentY = node.getCurrentCell().getYCoordinate();

        AtomicReference<Double> minDistance = new AtomicReference<>(Double.MAX_VALUE);
        context.getOthers().forEach(other -> {
            if (Math.sqrt(Math.pow(other.getXCoordinate() - currentX, 2) + Math.pow(other.getYCoordinate() - currentY, 2)) < minDistance.get()) {
                minDistance.set(Math.sqrt(Math.pow(other.getXCoordinate() - currentX, 2) + Math.pow(other.getYCoordinate() - currentY, 2)));
                resultCell.set(other);
            }
        });

        return resultCell.get();
    }

    private GenericCell getRandomOtherPlaceToGo() {
        int randomNumber = new Random().nextInt(context.getOthers().size());
        return new ArrayList<>(context.getOthers()).get(randomNumber);
    }

    private void setContext(Context context) {
        this.context = context;
    }
}
