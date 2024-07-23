import app.Application;
import context.Context;
import entity.Home;
import entity.Node;
import entity.Pub;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
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
        Set<Node> nodes = new HashSet<>();
        Set<Pub> pubs = new HashSet<>();

        try {
            int i = 0;
            File file = new File(INPUT_PATH);
            Scanner scanner = new Scanner(file);
            String data = null;

            // read contextX and ContextY
            data = scanner.nextLine();
            contextX = Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());
            contextY = Integer.parseInt(data.split(EMPTY_SPACE)[1].strip());

            // read minSpeed and maxSpeed
            data = scanner.nextLine();
            minSpeed = Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());
            maxSpeed = Integer.parseInt(data.split(EMPTY_SPACE)[1].strip());

            // read nodes
            data = scanner.nextLine();
            nrNodes = Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());

            while (i < nrNodes) {
                data = scanner.nextLine();
                nodes.add(readNode(data));
                i++;
            }

            // read Pubs
            while (scanner.hasNextLine()) {
                data = scanner.nextLine();
                pubs.add(readPub(data));
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

        Set<Integer> friendsId = null;
        if (data.split(EMPTY_SPACE).length > 3) {
            friendsId = Arrays.stream(data.split(EMPTY_SPACE))
                    .map(String::strip)
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
        }

       return new Node(id, new Home(homeXCoordinate, homeYCoordinate), 1, friendsId);
    }
    private static Pub readPub(String data) {
        int id = Integer.parseInt(data.split(EMPTY_SPACE)[0].strip());
        int XCoordinate = Integer.parseInt(data.split(EMPTY_SPACE)[1].strip());
        int YCoordinate = Integer.parseInt(data.split(EMPTY_SPACE)[2].strip());

       return new Pub(id, XCoordinate, YCoordinate);
    }
}