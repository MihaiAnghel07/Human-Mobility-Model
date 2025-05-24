import app.Application;
import context.Context;
import entity.CellType;
import entity.GenericCell;
import entity.Node;
import entity.Pub;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static utils.Utils.EMPTY_SPACE;
import static utils.Utils.INPUT_PATH;

public final class EntryPoint {
    private static final Application application = new Application();

    public static void main(String[] args) {

        Context context = getContext();
        application.start(context);
    }

    private static Context getContext() {
        int contextX = 1;
        int contextY = 1;
        int minSpeed = 0;
        int maxSpeed = 0;
        int nrNodes = 0;
        int nrPubs = 0;
        int nrOthers = 0;
        int maximumAllowedLastTimeSeen = 0;
        int chancesToBecomeFriends = 0;
        Set<Node> nodes = new HashSet<>();
        Set<Pub> pubs = new HashSet<>();
        Set<GenericCell> others = new HashSet<>();
        Set<GenericCell> obstacles = new HashSet<>();

        try {
            int i = 0;
            File file = new File(INPUT_PATH);
            Scanner scanner = new Scanner(file);
            String data;

            // read contextX and contextY
            scanner.nextLine();
            data = scanner.nextLine();
            contextX = Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());
            contextY = Integer.parseInt(data.split(EMPTY_SPACE)[1].strip());

            // read minSpeed and maxSpeed
            scanner.nextLine();
            data = scanner.nextLine();
            minSpeed = Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());
            maxSpeed = Integer.parseInt(data.split(EMPTY_SPACE)[1].strip());

            // read nr of nodes, nr of pubs and nr of other places
            scanner.nextLine();
            data = scanner.nextLine();
            nrNodes = Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());
            nrPubs = Integer.parseInt(data.split(EMPTY_SPACE)[1].strip());
            nrOthers = Integer.parseInt(data.split(EMPTY_SPACE)[2].strip());

            while (i < nrNodes) {
                data = scanner.nextLine();
                if (data.startsWith("#"))
                    continue;
                nodes.add(readNode(data, minSpeed, maxSpeed));
                i++;
            }

            // read Pubs
            i = 0;
            while (i < nrPubs) {
                data = scanner.nextLine();
                if (data.startsWith("#"))
                    continue;
                pubs.add(readPub(data));
                i++;
            }

            // read Others
            i = 0;
            while (i < nrOthers) {
                data = scanner.nextLine();
                if (data.startsWith("#"))
                    continue;
                others.add(readOther(data));
                i++;
            }

            // read friendship
            while (scanner.hasNextLine() && !data.startsWith("#numarul")) {
                data = scanner.nextLine();
                if (data.startsWith("#"))
                    continue;
                readFriendship(data, nodes);
            }

            // read maximum allowed days nodes don't see each other, but remain friends
            data = scanner.nextLine();
            maximumAllowedLastTimeSeen = readMaximumAllowedLastTimeSeen(data);
            scanner.nextLine();

            // read the chances that the node will make friends with the friends of its friends
            data = scanner.nextLine();
            chancesToBecomeFriends = readChancesToBecomeFriends(data);

            // read obstacles
            while (scanner.hasNextLine()) {
                data = scanner.nextLine();
                if (data.startsWith("#"))
                    continue;
                obstacles.add(readObstacle(data));
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return new Context(contextX, contextY, minSpeed, maxSpeed, nodes, pubs, others, obstacles,
                maximumAllowedLastTimeSeen, chancesToBecomeFriends);
    }

    private static Node readNode(String data, int minSpeed, int maxSpeed) {
        int id = Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());
        int homeXCoordinate = Integer.parseInt(data.split(EMPTY_SPACE)[1].strip());
        int homeYCoordinate = Integer.parseInt(data.split(EMPTY_SPACE)[2].strip());
        int workXCoordinate = Integer.parseInt(data.split(EMPTY_SPACE)[3].strip());
        int workYCoordinate = Integer.parseInt(data.split(EMPTY_SPACE)[4].strip());
        int speed = minSpeed + new Random().nextInt(maxSpeed - minSpeed + 1);

        return new Node(id,
                new GenericCell(homeXCoordinate, homeYCoordinate, CellType.HOME),
                new GenericCell(workXCoordinate, workYCoordinate, CellType.WORK),
                speed);
    }

    private static Pub readPub(String data) {
        int id = Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());
        int xCoordinate = Integer.parseInt(data.split(EMPTY_SPACE)[1].strip());
        int yCoordinate = Integer.parseInt(data.split(EMPTY_SPACE)[2].strip());

        return new Pub(id, xCoordinate, yCoordinate);
    }

    private static GenericCell readOther(String data) {
        int xCoordinate = Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());
        int yCoordinate = Integer.parseInt(data.split(EMPTY_SPACE)[1].strip());

        return new GenericCell(xCoordinate, yCoordinate, CellType.OTHER);
    }

    private static void readFriendship(String data, Set<Node> nodes) {
        int nodeId = Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());

        final Set<Integer> friendsId = Arrays.stream(data.split(EMPTY_SPACE))
                .map(String::strip)
                .map(Integer::parseInt)
                .filter(id -> id != nodeId)
                .collect(Collectors.toSet());

        Map<Node, Integer> friends = nodes.stream()
                .filter(node -> friendsId.contains(node.getId()))
                .collect(Collectors.toMap(node -> node, node -> 0));

        nodes.forEach(node -> {
            if (node.getId() == nodeId) {
                node.setFriends(friends);
            }
        });
    }

    private static GenericCell readObstacle(String data) {
        int xCoordinate = Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());
        int yCoordinate = Integer.parseInt(data.split(EMPTY_SPACE)[1].strip());

        return new GenericCell(xCoordinate, yCoordinate, CellType.OBSTACLE);
    }

    private static int readMaximumAllowedLastTimeSeen(String data) {
        return Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());
    }

    private static int readChancesToBecomeFriends(String data) {
        return Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());
    }

}