package app;

import context.Context;
import entity.GenericCell;
import entity.Node;
import utils.TimeToStay;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public final class Application {

    public void start(Context context) {
//        System.out.println(context);
        placeNodesToTheirHome(context);

        // simulate the time elapse
        for (int t = 0; t < 24; t++) {

            int finalT = t;
            context.getNodes().forEach(node -> {

                //update social network
                //updateSocialNetwork(context);

                // update activity weight according to hour
                updateActivityWeight(context, node, finalT);

                // nodes are being moved based on their activity weight
                moveNode(node, finalT);
            });
        }

    }

    private void moveNode(Node node, int t) {
        int probability = new Random().nextInt(100);

        if (node.getCurrentCell() == node.getTargetCell()) {
            //node has arrived to destination
            node.setTargetCell(null);
            node.setTimeToStay(TimeToStay.getHours(node.getCurrentCell()));// TODO: to be set according to Cell type

        } else if (node.getTargetCell() == null && node.getTimeToStay() == 0) {
            // node can move to a target
            moveNodeToTarget(node);
        } else if (node.getTargetCell() == null && node.getTimeToStay() != 0) {
            // lower the timeToStay

        } else if (node.getTargetCell() != null) {
            // move the node to the target
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

        if (node.getSpeed() > deltaX) {
            if (node.getSpeed() < deltaY) {
            }
        }
    }

    private void updateActivityWeight(Context context, Node node, int hour) {
        if (hour >= 6 && hour <= 10) {
            context.getPubs().forEach(pub -> {
                if (getNrFriendsInPubs(node, context) > 0) {
                    node.getActivityWeight().put("pub", 10);
                    node.getActivityWeight().put("home", 10);
                } else {
                    node.getActivityWeight().put("pub", 5);
                    node.getActivityWeight().put("home", 15);
                }
            });
            node.getActivityWeight().put("work", 70);
            node.getActivityWeight().put("other", 10);
        } else if (hour >= 11 && hour <= 18) {
            node.getActivityWeight().put("home", 10);
            node.getActivityWeight().put("pub", 5);
            node.getActivityWeight().put("work", 65);
            node.getActivityWeight().put("other", 20);
        } else if (hour >= 19 && hour <= 24) {
            context.getPubs().forEach(pub -> {
                if (getNrFriendsInPubs(node, context) > 0) {
                    node.getActivityWeight().put("pub", 40);
                    node.getActivityWeight().put("home", 40);
                } else {
                    node.getActivityWeight().put("pub", 25);
                    node.getActivityWeight().put("home", 55);
                }
            });
            node.getActivityWeight().put("work", 5);
            node.getActivityWeight().put("other", 15);
        } else {
            node.getActivityWeight().put("home", 90);
            node.getActivityWeight().put("pub", 5);
            node.getActivityWeight().put("work", 2);
            node.getActivityWeight().put("other", 3);
        }
    }

    private void placeNodesToTheirHome(Context context) {
        context.getNodes().forEach(node -> node.setCurrentCell(node.getHome()));
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
}
