package mcfine.myhome.config;

import mcfine.myhome.MyHome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CustomConfig {

    private static File file;
    private static FileConfiguration customFile;

    public static void setup() {

        file = new File(MyHome.plugin.getDataFolder().getAbsolutePath() + File.separator + "translation", MyHome.locale + ".yml");

        file.getParentFile().mkdirs();

        if (!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException ex) {
                System.out.println("Error generating messages.yml " + ex);
                ex.printStackTrace();
            }
        }
        customFile = YamlConfiguration.loadConfiguration(file);

        customFile.addDefault("prefix", "&9Дом &7» ");
        customFile.addDefault("home-tp-success", "%prefix%&2Вы переместились в ваш дом!");


        customFile.addDefault("not-safe-home", "%prefix%&cВаш дом не безопасен! &7Введите &f/home confirm &7чтобы все равно телепортироваться.");
        customFile.addDefault("not-safe-home-other", "%prefix%&cЭтот дом не безопасен! &7Введите &f/home %player% confirm &7чтобы все равно телепортироваться.");
        customFile.addDefault("not-safe-respawn", "%prefix%&cВаш дом не безопасен для того чтобы там возродиться!");
        customFile.addDefault("no-space-for-portal", "&cНе нашлось места для портала!");


        customFile.addDefault("admin-tp-no-player","%prefix%&cНе нашлось игрока с таким именем!");
        customFile.addDefault("admin-tp-success","%prefix%&6Игрок был телепортирован!");


        customFile.addDefault("no-permissions", "%prefix%&cУ вас нет доступа к данной команде!");
        customFile.addDefault("no-home", "%prefix%&c&cУ вас нет дома. &7Введите &f/home set &7для установки дома.");
        customFile.addDefault("other-no-home", "%prefix%&cУ игрока &e%player%&c нет дома.");
        customFile.addDefault("it-is-you", "%prefix%&cЭто же ваш ник!");
        customFile.addDefault("no-nearby-safe", "%prefix%&cПо близости от вашего дома не нашлось ни одного безопасного места!");
        customFile.addDefault("no-home-respawn", "%prefix%&cУ вас нету дома, в котором вы могли бы возродиться. &7Установить дом: &f/home set");
        customFile.addDefault("not-enough-money", "%prefix%&cУ вас недостаточно денег для телепортации. &7Вам нужно &a%cost%&7.");
        customFile.addDefault("not-enough-money-other", "%prefix%&cУ вас недостаточно денег для телепортации домой к &f%player%&c.&7 Вам нужно &a%cost%&7.");
        customFile.addDefault("on-cooldown", "%prefix%&cВы не можете телепортироваться еще &7%time% &cсекунд.");
        customFile.addDefault("cancel-on-movement", "%prefix%&cТелепортация отменена. &7Вы сдвинулись с места.");


        customFile.addDefault("respawned", "%prefix%&2Вы возродились у себя дома.");
        customFile.addDefault("reload", "%prefix%&6Конфигурация перезагружена.");
        customFile.addDefault("withdraw", " &7С вашего счета списано %cost%.");


        customFile.addDefault("invited-list", "%prefix%&eК вам приглашены&7 (%amount%)&e: &7");
        customFile.addDefault("invited-delimiter", "&e, &7");
        customFile.addDefault("invited-nobody", "%prefix%&cВы еще никого не пригласили!");
        customFile.addDefault("invited-by-nobody", "%prefix%&cВас еще никто не пригласил.");


        customFile.addDefault("invited-to-etc", "&e...");
        customFile.addDefault("invited-to-list", "%prefix%&eВы приглашены к &7(%amount%)&e: &7");
        customFile.addDefault("invited-to-delimiter", "&e, &7");
        customFile.addDefault("invited-to-player", "%prefix%&6Вы были приглашены к &e%player% &6домой");


        customFile.addDefault("home-deleted", "%prefix%&eДом удален успешно. &7Введите &f/home set &7для установки нового.");
        customFile.addDefault("home-moved", "&6Ваш дом перемещен на данное место.");
        customFile.addDefault("home-set", "%prefix%&2Точка дома установлена! &7Введите &f/home &7для телепортации.");
        customFile.addDefault("home-set-error", "%prefix%&cОшибка при установке дома.");


        customFile.addDefault("teleported-to-player-home", "%prefix%&eВы дома у &6%player%.");
        customFile.addDefault("not-invited", "%prefix%&cИгрок &6%player% &cне приглашал вас к себе домой!");
        customFile.addDefault("wrong-syntax", "%prefix%&cНеверный синтаксис. &7Введите &f/home help &7для справки.");
        customFile.addDefault("wrong-command", "%prefix%&cНеверная команда. &7Введите &f/home help &7для справки.");


        customFile.addDefault("invited-player", "%prefix%&6Вы пригласили игрока &e%player% &6к себе домой!");
        customFile.addDefault("invited-already", "%prefix%&6Игрок &e%player%&6 уже приглашен!");
        customFile.addDefault("you-were-invited", "%prefix%&6Вы были приглашены к игроку &e%player%&6 домой!");
        customFile.addDefault("uninvited-player", "%prefix%&6Вы больше не приглашаете &e%player%&6 к себе домой!");
        customFile.addDefault("uninvited-already", "%prefix%&6Игрок и так не приглашен к вам!");
        customFile.addDefault("you-were-uninvited", "%prefix%&6Вы более не приглашены к игроку &e%player%&6 домой!");


        customFile.addDefault("home-public", "%prefix%&6Ваш дом теперь &aпубличный!");
        customFile.addDefault("home-private", "%prefix%&6Ваш дом теперь &cприватный!");
        customFile.addDefault("home-already-public", "%prefix%&cДом уже является публичным!");
        customFile.addDefault("home-already-private", "%prefix%&cДом и так является приватным!");


        customFile.addDefault("help-message", """
                &8&l&m------------------&8&l<&r&3&l Дома &8&l>&8&l&m------------------
                                
                &9/home                   &6-  &6Телепортироваться домой
                                
                &9/home set              &6-  &6Установить точку дома
                                
                &9/home invite &7<Ник>     &6-  &6Пригласить домой
                                
                &9/home uninvite &7<Ник>  &6-  &6Удалить из приглашенных
                                
                &9/home &7<Ник>            &6-  &6Телепортироваться в дом игрока
                                
                &9/home delete           &6-  &6Удалить точку дома
                                
                &9/home invited           &6-  &6Посмотреть список приглашенных
                                
                &9/home invites           &6-  &6Кто меня пригласил
                                
                &9/home public           &6-  &6Сделать дом общедоступным
                                
                &9/home private         &6-  &6Сделать дом приватным
                                
                &9/home help             &6-  &6Получить информацию о плагине
                                
                &8&l&m-------------------------------------------
                """);


        customFile.options().copyDefaults(true);
        save();
        reload();

        Locale.readLocale(customFile);

    }

    public static FileConfiguration getCustomFile() {
        return customFile;
    }

    public static void save() {
        try {
            customFile.save(file);
        } catch (IOException e) {
            System.out.println("Couldnt save file");
        }
    }

    public static void reload() {
        customFile = YamlConfiguration.loadConfiguration(file);
    }

}
