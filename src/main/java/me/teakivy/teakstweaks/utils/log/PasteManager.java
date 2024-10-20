package me.teakivy.teakstweaks.utils.log;

import me.teakivy.teakstweaks.TeaksTweaks;
import me.teakivy.teakstweaks.utils.config.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class PasteManager {

    public static String getPasteContent(String playerName, boolean includeLogs) {
        StringBuilder str = new StringBuilder();

        str.append("Teak's Tweaks Plugin Paste");

        str.append("\n\n");

        str.append("Plugin Version: ").append(TeaksTweaks.getInstance().getDescription().getVersion()).append("\n");
        str.append("Server Type: ").append(TeaksTweaks.getInstance().getServer().getName()).append("\n");
        str.append("Server Version: ").append(TeaksTweaks.getInstance().getServer().getVersion()).append("\n");
        str.append("Java Version: ").append(Runtime.version()).append("\n");
        str.append("Operating System: ").append(System.getProperty("os.name")).append(" (").append(System.getProperty("os.version")).append(")\n");

        str.append("\n\n");

        str.append("Config: ").append(uploadConfig(playerName)).append("\n");
        str.append("Config Version: ").append(Config.getVersion()).append("\n");
        str.append("Config Generated: ").append(Config.getCreatedVersion()).append("\n");
        str.append("Dev Mode: ").append(Config.isDevMode()).append("\n");

        str.append("\n\n");

        ArrayList<String> packs = TeaksTweaks.getInstance().getPacks();
        ArrayList<String> ctweaks = TeaksTweaks.getInstance().getCraftingTweaks();
        str.append("Enabled Packs (").append(packs.size()).append("): \n");
        str.append(arrayToString(packs)).append("\n");
        str.append("Enabled Crafting Tweaks (").append(ctweaks.size()).append("): \n");
        str.append(arrayToString(ctweaks)).append("\n");

        str.append("\n\n");

        if (includeLogs) {
            str.append("Logs: \n");
            str.append(Logger.getLogMessagesAsString());
        } else {
            str.append("Logs omitted.");
        }

        return str.toString();
    }

    public static String arrayToString(ArrayList<String> arr) {
        StringBuilder str = new StringBuilder();
        if (!arr.isEmpty()) {
            for (String s : arr) {
                str.append(" - ").append(s).append("\n");
            }
        }

        return str.toString();
    }

    public static String uploadConfig(String playerName) {
        try {
            String config = getConfigFileAsString();
            return PasteBookUploader.uploadText(config, "Teak's Tweaks Config: " + playerName);
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public static String getConfigFileAsString() {
        File file = TeaksTweaks.getInstance().getDataFolder().toPath().resolve("config.yml").toFile();
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}