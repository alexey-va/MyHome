package mcfine.myhome.utils;

import mcfine.myhome.MyHome;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class HomeStorage {

    private static ConcurrentHashMap<String, ArrayList<Home>> homeMap = new ConcurrentHashMap<>();
    private static MySQLStorage mysql = null;
    private static JsonStorage json = null;
    private static String dataStorage = "json";

    public HomeStorage() {
        String dataStore = MyHome.plugin.getConfig().getString("data-storage", "json");

        if (dataStore.equalsIgnoreCase("json")) {
            json = new JsonStorage();
        } else if (dataStore.equalsIgnoreCase("mysql")) {
            dataStorage = "mysql";

            String url = MyHome.plugin.getConfig().getString("mysql.url", "localhost");
            String user = MyHome.plugin.getConfig().getString("mysql.user", "user");
            String password = MyHome.plugin.getConfig().getString("mysql.password", "password");
            String database = MyHome.plugin.getConfig().getString("mysql.database", "database");
            mysql = new MySQLStorage(user, password, url, database);
            try {
                mysql.createTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            json = new JsonStorage();
        }
    }

    public static boolean putHome(Home home) {
        String playerName = home.getPlayerName().toLowerCase();
        if (homeMap.containsKey(playerName)) {
            if (homeMap.get(playerName) != null && homeMap.get(playerName).size() > 0) {
                homeMap.get(playerName).add(home);
                return false;
            } else {
                ArrayList<Home> homeList = new ArrayList<>();
                homeList.add(home);
                homeMap.replace(playerName, homeList);
                return true;
            }
        } else {
            ArrayList<Home> homeList = new ArrayList<>();
            homeList.add(home);
            homeMap.put(playerName, homeList);
            return true;
        }
    }

    @Nullable
    public static Home getHome(String playerName) {
        if (homeMap.containsKey(playerName.toLowerCase())) {
            ArrayList<Home> homeList = homeMap.get(playerName.toLowerCase());
            if (homeList == null || homeList.size() == 0) return null;
            return homeList.get(0);
        }
        return null;
    }

    public static boolean deleteHome(String playerName) {
        if (!homeMap.containsKey(playerName.toLowerCase())) return false;
        ArrayList<Home> homes = homeMap.get(playerName.toLowerCase());
        if (homes == null || homes.size() == 0) return false;
        homes.remove(0);
        homeMap.replace(playerName.toLowerCase(), homes);
        return true;
    }


    public static boolean replaceHome(String playerName, Home newHome) {
        if (!homeMap.containsKey(playerName.toLowerCase())) return false;
        ArrayList<Home> homeList = new ArrayList<>();
        homeList.add(newHome);
        homeMap.replace(playerName.toLowerCase(), homeList);
        return true;
    }

    public static void loadData(boolean sync) {
        try {

            if ("json".equalsIgnoreCase(dataStorage)) {
                json.loadData(sync);
            } else if ("mysql".equalsIgnoreCase(dataStorage)) {
                mysql.loadData();
            } else {
                MyHome.plugin.warn("Data storage type is wrong! " + MyHome.plugin.getConfig().getString("data-storage"));
            }

        } catch (Exception ex) {
            MyHome.plugin.warn("Error reading data");
            ex.printStackTrace();
        }
    }

    public static void saveData(boolean async) throws IOException {

        if (dataStorage.equalsIgnoreCase("json")) {
            json.saveData(async);
        } else if (dataStorage.equalsIgnoreCase("mysql")) {
            try {
                mysql.saveData();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            MyHome.plugin.warn("Data storage type is wrong! " + MyHome.plugin.getConfig().getString("data-storage"));
        }
    }

    public static ConcurrentHashMap<String, ArrayList<Home>> getHomeMap() {
        return homeMap;
    }

    public static void setHomeMap(ConcurrentHashMap<String, ArrayList<Home>> homeMap) {
        HomeStorage.homeMap = homeMap;
    }

    public static MySQLStorage getMysql() {
        return mysql;
    }

    public static JsonStorage getJson() {
        return json;
    }
}
