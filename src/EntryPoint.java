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
        Set<Node> nodes = new HashSet<>();
        Set<Pub> pubs = new HashSet<>();

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

            // read nr of nodes and nr of pubs
            scanner.nextLine();
            data = scanner.nextLine();
            nrNodes = Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());
            nrPubs = Integer.parseInt(data.split(EMPTY_SPACE)[1].strip());

            while (i < nrNodes) {
                data = scanner.nextLine();
                if (data.startsWith("#"))
                    continue;
                nodes.add(readNode(data));
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

            //read friendship
            while (scanner.hasNextLine()) {
                data = scanner.nextLine();
                if (data.startsWith("#"))
                    continue;
                readFriendship(data, nodes);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return new Context(contextX, contextY, minSpeed, maxSpeed, nodes, pubs);
    }

    private static Node readNode(String data) {
        int id = Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());
        int homeXCoordinate = Integer.parseInt(data.split(EMPTY_SPACE)[1].strip());
        int homeYCoordinate = Integer.parseInt(data.split(EMPTY_SPACE)[2].strip());

        return new Node(id, new GenericCell(homeXCoordinate, homeYCoordinate, CellType.HOME), 1);
    }

    private static Pub readPub(String data) {
        int id = Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());
        int XCoordinate = Integer.parseInt(data.split(EMPTY_SPACE)[1].strip());
        int YCoordinate = Integer.parseInt(data.split(EMPTY_SPACE)[2].strip());

        return new Pub(id, XCoordinate, YCoordinate);
    }

    private static void readFriendship(String data, Set<Node> nodes) {
        int nodeId = Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());

        final Set<Integer> friendsId = Arrays.stream(data.split(EMPTY_SPACE))
                .map(String::strip)
                .map(Integer::parseInt)
                .filter(id -> id != nodeId)
                .collect(Collectors.toSet());

         Set<Node> friends = nodes.stream()
                .filter(node -> friendsId.contains(node.getId()))
                .collect(Collectors.toSet());

         nodes.forEach(node -> {
             if (node.getId() == nodeId) {
                 node.setFriends(friends);
             }
         });
    }

}