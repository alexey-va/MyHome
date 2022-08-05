package mcfine.myhome.config;

import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Locale {

    public static @NotNull Map<String, Object> lang = new HashMap<>();
    private static File file;
    private static FileConfiguration customFile;

    public static void readLocale(@NotNull FileConfiguration fc) {
        try {
            lang = Objects.requireNonNull(fc.getConfigurationSection("")).getValues(false);
        } catch (NullPointerException ex) {
            System.out.println("Error reading language file.");
        }

    }

    public static String getString(@NotNull String s) {
        try {
            return ColorTranslator.translateColorCodes(((String) lang.get(s)).replace("%prefix%", (String) lang.get("prefix")));
        } catch (Exception ex) {
            System.out.println("Error parsing string: (" + s + ") " + ex);
            return s;
        }
    }


}
