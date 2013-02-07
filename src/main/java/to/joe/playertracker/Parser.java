package to.joe.playertracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class Parser {

    enum Action {
        ALL, PLAYERS, VANISHED, ADMINS, TRUSTED, OFFLINE,
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage 'java -jar parser.jar [all,players,vanished,admins,trusted] [filename]");
            return;
        }
        InputStream input;
        try {
            input = new FileInputStream(new File(args[1]));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        Yaml yaml = new Yaml();
        Map<String, Object> data = (Map<String, Object>) yaml.load(input);

        Action choice = Action.valueOf(args[0].toUpperCase());
        int lastUpdate = (Integer) data.get("updated");
        if (lastUpdate + 60 < System.currentTimeMillis() / 1000L) {
            if (choice == Action.ALL) {
                System.out.println("maxplayers:" + data.get("maxplayers") + " vanished:0 admins:0 trusted:0 players:0");
            } else {
                System.out.println(0);
            }
            return;
        }

        switch (choice) {
            case PLAYERS:
                System.out.println(((List<String>) data.get("players")).size());
                return;
            case VANISHED:
                System.out.println(((List<String>) data.get("vanished")).size());
                return;
            case ADMINS:
                System.out.println(((List<String>) data.get("admins")).size());
                return;
            case TRUSTED:
                System.out.println(((List<String>) data.get("trusted")).size());
                return;
            case OFFLINE:
                System.out.println(0);
                return;
        }

        StringBuilder builder = new StringBuilder();

        if (!(Boolean) data.get("online")) {
            System.out.println("maxplayers:" + data.get("maxplayers") + " vanished:0 admins:0 trusted:0 players:0");
            return;
        }

        //builder.append("online:").append(data.get("online")).append(" ");
        builder.append("maxplayers:").append(data.get("maxplayers")).append(" ");

        //Vanished, admins, trusted, players
        List<String> players = (List<String>) data.get("players");
        List<String> admins = (List<String>) data.get("admins");
        List<String> vanished = (List<String>) data.get("vanished");
        List<String> trusted = (List<String>) data.get("trusted");

        admins.removeAll(vanished);

        trusted.removeAll(vanished);
        trusted.removeAll(admins);

        players.removeAll(vanished);
        players.removeAll(admins);
        players.removeAll(trusted);

        builder.append("vanished:").append(vanished.size()).append(" ");
        builder.append("admins:").append(admins.size()).append(" ");
        builder.append("trusted:").append(trusted.size()).append(" ");
        builder.append("players:").append(players.size()).append(" ");

        System.out.println(builder.toString());
    }
}