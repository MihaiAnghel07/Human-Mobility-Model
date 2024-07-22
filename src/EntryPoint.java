import app.Application;
import context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static utils.Utils.INPUT_PATH;

public final class EntryPoint {
    private static final Application application = new Application();
    private static final Context context = null;

    public static void main(String[] args) {
        initContext(context);
        application.start(context);
    }

    private static void initContext(Context context) {
        int x = 1;
        int y = 1;
        int minSpeed = 0;
        int maxSpeed = 0;

        try {
            int i = 0;
            File file = new File(INPUT_PATH);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                if (i == 0) {
                    x = Integer.parseInt(data.split(" ")[0].strip());
                    y = Integer.parseInt(data.split(" ")[1].strip());
                } else if (i == 1) {
                    minSpeed = Integer.parseInt(data.split(" ")[0].strip());
                    maxSpeed = Integer.parseInt(data.split(" ")[1].strip());
                }
                
                System.out.println(data);
                i++;
            }

            context = new Context(x, y, minSpeed, maxSpeed);
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}