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

import static utils.Utils.*;

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

            // count hours from 0 to 24
            if (i % 4 == 0 && hour < 24) {
                hour++;
            } else if (hour >= 24) {
                hour = 0;
            }

            int finalHour = hour;
            context.getNodes().forEach(node -> {

                //update social network
                updateSocialNetwork(node);

                // update activity weight
                if (finalHour == 6 || finalHour == 10 || finalHour == 18 || finalHour == 24 || finalHour == 0) {
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

            if (!node.getTargetCell().equals(node.getCurrentCell()) && node.getCurrentCell().getCellType() == CellType.PUB) {
                ((Pub) node.getCurrentCell()).removeNode(node);
            }
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

            if (node.getCurrentCell().getCellType() == CellType.PUB) {
                ((Pub) node.getCurrentCell()).addNode(node);
            }

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

        System.out.println("SPEEED: " + node.getSpeed());

        int i = 0;
        Pair<Integer, Integer> nextNode = null;
        while (i++ < node.getSpeed() && !node.getPath().isEmpty()) {
            nextNode = node.getPath().poll();
        }

        node.setCurrentCell(context.getMap()[nextNode.getFirst()][nextNode.getSecond()]);
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
        if (hour >= 6 && hour <= 9) {
            if (getNrFriendsInPubs(node) > 0) {
                System.out.println("AAA: " + getNrFriendsInPubs(node));
                node.getActivityWeight().put(CellType.PUB, 10);
                node.getActivityWeight().put(CellType.HOME, 10);
            } else {
                node.getActivityWeight().put(CellType.PUB, 5);
                node.getActivityWeight().put(CellType.HOME, 15);
            }
            node.getActivityWeight().put(CellType.WORK, 70);
            node.getActivityWeight().put(CellType.OTHER, 10);
        } else if (hour >= 10 && hour <= 17) {
            node.getActivityWeight().put(CellType.HOME, 10);
            node.getActivityWeight().put(CellType.PUB, 5);
            node.getActivityWeight().put(CellType.WORK, 65);
            node.getActivityWeight().put(CellType.OTHER, 20);
        } else if (hour >= 18 && hour <= 24) {
            if (getNrFriendsInPubs(node) > 0) {
                System.out.println("BBB: " + getNrFriendsInPubs(node));

                node.getActivityWeight().put(CellType.PUB, 50);
                node.getActivityWeight().put(CellType.HOME, 30);
            } else {
                node.getActivityWeight().put(CellType.PUB, 25);
                node.getActivityWeight().put(CellType.HOME, 55);
            }
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

    private int getNrFriendsInPubs(Node node) {
        return (int) context.getPubs().stream()
                .flatMap(pub -> pub.getPresentNodes().stream())
                .filter(node.getFriends()::containsKey)
                .count();
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
        return context.getPubs().stream()
                .max(Comparator.comparingInt(pub -> {
                    Set<Node> commonFriends = new HashSet<>(pub.getPresentNodes());
                    commonFriends.retainAll(node.getFriends().keySet());
                    return commonFriends.size();
                }))
                .orElse(null);
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
        int currentX = node.getCurrentCell().getXCoordinate();
        int currentY = node.getCurrentCell().getYCoordinate();

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

    }

    private void setContext(Context context) {
        this.context = context;
    }

    private void printRelationships() {
        context.getNodes().forEach(node -> {
            StringBuilder string = new StringBuilder(node.getId() + ": ");
            node.getFriends().keySet().forEach(friend -> string.append(friend.getId()).append(", "));
            System.out.println(string.substring(0, string.length() - 2));
        });
        System.out.println();
    }

    private void updateSocialNetwork(Node node) {
        Random random = new Random();

        List<Node> newFriends = new ArrayList<>();

        if (node.getCurrentCell().getCellType() == CellType.PUB) {
            System.out.println("N[" + node.getId() + "] este in pub");
            node.getFriends().keySet().forEach(friend -> {
                if (friend.getCurrentCell().equals(node.getCurrentCell())) {
                    System.out.println("prietenul N[" + friend.getId() + "] este in pub");
                    friend.getFriends().keySet().forEach(friendOfFriend -> {
                        if (friendOfFriend.getId() != node.getId()
                                && friendOfFriend.getCurrentCell().equals(node.getCurrentCell())
                                && !node.getFriends().containsKey(friendOfFriend)) {
                            System.out.println("prietenul prietenul N[" + friendOfFriend.getId() + "] este in pub");

                            // sanse de 50% sa devina prieten cu nodul initial
                            if (random.nextInt(2) == 0) {
                                newFriends.add(friendOfFriend);
                                System.out.println("Nodul N[" + node.getId() + "] a devenit prieten cu N[" + friendOfFriend.getId() + "].");
                            }
                        }
                    });
                }
            });
        }

        newFriends.forEach(node::addFriend);

        removeOldFriends(node);
    }

    private void removeOldFriends(Node node) {
        node.getFriends().keySet().forEach(friend -> {
            if (node.getCurrentCell().equals(friend.getCurrentCell())) {
                node.getFriends().put(friend, 0);
            } else {
//                System.out.println("ASDSADASSADASDASDASDAD: " + node.getFriends().get(friend));
                node.getFriends().put(friend, node.getFriends().get(friend) + 1);
            }
        });

        Set<Node> friendsToBeRemoved = new HashSet<>();

        node.getFriends().forEach((friend, lastTimeSeen) -> {
            if (lastTimeSeen > MAXIMUM_ALLOWED_LAST_TIME_SEEN) {
                friendsToBeRemoved.add(friend);
            }
        });

        friendsToBeRemoved.forEach(node::removeFriend);
    }
}
