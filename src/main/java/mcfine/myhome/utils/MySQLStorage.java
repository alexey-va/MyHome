package mcfine.myhome.utils;

import mcfine.myhome.MyHome;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MySQLStorage {
    // private final ArrayList<Statement> statementBuffer = new ArrayList<>();
    private final HashMap<String, Integer> playerMap = new HashMap<>();
    private Connection connection;

    public MySQLStorage(String user, String password, String adress, String database) {
        String url = "jdbc:mysql://" + adress + "/" + database + "?autoReconnect=true&useUnicode=yes";
        try {
            this.connection = DriverManager.getConnection(url, user, password);
            this.connection.setAutoCommit(false);
        } catch (SQLException ex) {
            MyHome.plugin.getLogger().warning("Error connecting to MySQL database: " + ex);
            ex.printStackTrace();
            MyHome.plugin.getServer().getPluginManager().disablePlugin(MyHome.plugin);
        }
    }

    public void flush() throws SQLException {
        this.connection.commit();
    }

    public void createTable() throws SQLException {
        String homes = """
                CREATE TABLE IF NOT EXISTS myhome_homes(x double, y double, z double, yaw double,
                pitch double, worldName varchar(255), homeName varchar(255), playerName varchar(255) primary key, invitedPlayers text, isPublic bit, lastEnter Date, visits int)
                """;

        Statement st = connection.createStatement();
        st.execute(homes);
    }

    public void loadData() throws SQLException {
        Callback<ConcurrentHashMap<String, ArrayList<Home>>> callback = response -> HomeStorage.setHomeMap(Objects.requireNonNullElseGet(response, ConcurrentHashMap::new));
        loadDataAsync(callback);
    }

    public void saveData() throws SQLException {
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void execute(Boolean response) {
                if (response) MyHome.plugin.log("Data saved (MySQL).");
                else MyHome.plugin.warn("Data was not saved (MySQL).");
            }
        };
        saveDataAsync(HomeStorage.getHomeMap(), callback);
    }


    private void saveDataAsync(ConcurrentHashMap<String, ArrayList<Home>> homeMap, Callback<Boolean> callback) throws SQLException {

        new BukkitRunnable() {
            @Override
            public void run() {
                boolean result = true;


                try {
                    Statement statement_0 = connection.createStatement();
                    statement_0.execute("DELETE FROM myhome_homes");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                PreparedStatement statement = null;
                try {
                    statement = connection.prepareStatement("""
                            INSERT INTO myhome_homes(x, y, z, yaw, pitch, worldName, homeName, playerName, invitedPlayers, isPublic, lastEnter, visits) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                            ON DUPLICATE KEY UPDATE
                            x = values(x),
                            y = values(y),
                            z = values(z),
                            yaw = values(yaw),
                            pitch = values(pitch),
                            worldName = values(worldName),
                            homeName = values(homeName),
                            invitedPlayers = values(invitedPlayers),
                            isPublic = values(isPublic),
                            lastEnter = values(lastEnter),
                            visits = values(visits)
                            """);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return;
                }

                for (ArrayList<Home> homeList : HomeStorage.getHomeMap().values()) {
                    for (Home home : homeList) {
                        try {


                            StringBuilder invitedPlayers = new StringBuilder();
                            for (String s : home.getInvitedPlayers()) {
                                if (s.contains(";")) {
                                    MyHome.plugin.warn("Name contains forbidden char ';': " + s);
                                    continue;
                                }
                                invitedPlayers.append(s).append(";");
                                MyHome.plugin.warn(invitedPlayers.toString());
                            }
                            String inv = invitedPlayers.toString();
                            if (inv.length() > 0 && inv.charAt(inv.length() - 1) == ';')
                                inv = inv.substring(0, inv.length() - 1);
                            MyHome.plugin.warn(inv);

                            statement.setDouble(1, home.getX());
                            statement.setDouble(2, home.getY());
                            statement.setDouble(3, home.getZ());
                            statement.setDouble(4, home.getYaw());
                            statement.setDouble(5, home.getPitch());
                            statement.setString(6, home.getWorldName());
                            statement.setString(7, home.getHomeName());
                            statement.setString(8, home.getPlayerName());
                            statement.setString(9, inv);
                            statement.setBoolean(10, home.isPublic());
                            statement.setDate(11, new java.sql.Date(home.getLastEnter().getTime()));
                            statement.setInt(12, home.getVisits());

                            statement.addBatch();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            result = false;
                        }

                    }
                }

                try {
                    statement.executeBatch();
                    flush();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                Boolean finalResult = result;
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        callback.execute(finalResult);
                    }
                }.runTask(MyHome.plugin);


            }
        }.runTaskAsynchronously(MyHome.plugin);
    }

    private void loadDataAsync(Callback<ConcurrentHashMap<String, ArrayList<Home>>> callback) throws SQLException {

        new BukkitRunnable() {
            @Override
            public void run() {
                ConcurrentHashMap<String, ArrayList<Home>> homeMap = new ConcurrentHashMap<>();
                try {

                    PreparedStatement statement = connection.prepareStatement("""
                            SELECT * from myhome_homes
                            """);

                    ResultSet res = statement.executeQuery();

                    while (res.next()) {

                        double x = res.getDouble(1);
                        double y = res.getDouble(2);
                        double z = res.getDouble(3);

                        float yaw = res.getFloat(4);
                        float pitch = res.getFloat(5);

                        String worldName = res.getString(6);
                        String homeName = res.getString(7);
                        String playerName = res.getString(8);
                        String invitedPlayers = res.getString(9);

                        boolean isPublic = res.getBoolean(10);

                        java.util.Date lastEnter = new java.util.Date(res.getDate(11).getTime());
                        int visits = res.getInt(12);
                        ArrayList<Home> homeList = new ArrayList<>();
                        homeList.add(new Home(x, y, z, yaw, pitch, homeName, playerName, new ArrayList<String>(Arrays.asList(invitedPlayers.split(";"))), worldName, isPublic, lastEnter, visits));

                        homeMap.put(
                                playerName,
                                homeList
                        );
                    }
                } catch (SQLException ex) {
                    MyHome.plugin.warn("Error loading from MySQL database: " + ex);
                    ex.printStackTrace();
                    return;
                }

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        callback.execute(homeMap);
                    }
                }.runTask(MyHome.plugin);


            }
        }.runTaskAsynchronously(MyHome.plugin);


    }

    public Connection getConnection() {
        return connection;
    }


    public interface Callback<T> {
        void execute(T response);
    }
}
