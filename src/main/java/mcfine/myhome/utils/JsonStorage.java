package mcfine.myhome.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mcfine.myhome.MyHome;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class JsonStorage {

    public void loadData(boolean async) {
        if (async) {
            Callback<ConcurrentHashMap<String, ArrayList<Home>>> callback = response -> HomeStorage.setHomeMap(Objects.requireNonNullElseGet(response, ConcurrentHashMap::new));
            loadDataAsync(callback);
        } else {
            loadDataSync();
        }
    }

    public void saveData(boolean async) {
        if (async) {
            Callback<Boolean> callback = response -> {
                if (response) MyHome.plugin.log("Data saving went successful!");
                else MyHome.plugin.warn("Data saving went wrong!");
            };
            ConcurrentHashMap<String, ArrayList<Home>> finalStorageMap = HomeStorage.getHomeMap();
            saveDataAsync(finalStorageMap, callback);
        } else {
            saveDataSync();
        }
    }

    public void loadDataAsync(final Callback<ConcurrentHashMap<String, ArrayList<Home>>> callback) {
        new BukkitRunnable() {

            @Override
            public void run() {
                ConcurrentHashMap<String, ArrayList<Home>> storageMap = new ConcurrentHashMap<>();
                Gson gson = new Gson();
                File file = new File(MyHome.plugin.getDataFolder().getAbsolutePath() + File.separator + "storage" + File.separator + "json_storage.json");

                try {
                    //noinspection ResultOfMethodCallIgnored
                    file.getParentFile().mkdirs();
                    //noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                } catch (IOException ignored) {
                }

                Reader reader;
                try {
                    reader = new FileReader(file);
                } catch (FileNotFoundException e) {
                    MyHome.plugin.warn("Error creating a file reader " + e);
                    return;
                }

                try {
                    Type listType = new TypeToken<ConcurrentHashMap<String, ArrayList<Home>>>() {
                    }.getType();
                    storageMap = gson.fromJson(reader, listType);
                } catch (Exception ex) {
                    MyHome.plugin.warn("Exception reading values from JSON: " + ex);
                }

                ConcurrentHashMap<String, ArrayList<Home>> finalStorageMap = storageMap;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        callback.execute(finalStorageMap);
                    }
                }.runTask(MyHome.plugin);
            }
        }.runTaskAsynchronously(MyHome.plugin);
    }

    private void saveDataAsync(ConcurrentHashMap<String, ArrayList<Home>> homeMap, Callback<Boolean> callback) {
        new BukkitRunnable() {

            @Override
            public void run() {
                boolean res = true;

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                File file = new File(MyHome.plugin.getDataFolder().getAbsolutePath() + File.separator + "storage" + File.separator + "json_storage.json");
                try {
                    //noinspection ResultOfMethodCallIgnored
                    file.getParentFile().mkdirs();
                    //noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                } catch (IOException e) {
                    MyHome.plugin.warn("Error creating file " + e);
                    res = false;
                }
                Writer writer;
                try {
                    writer = new FileWriter(file, false);
                } catch (IOException e) {
                    MyHome.plugin.warn("Error creating file writer " + e);
                    return;
                }

                gson.toJson(homeMap, writer);
                try {
                    writer.flush();
                } catch (IOException e) {
                    MyHome.plugin.warn("Error flushing file " + e);
                    res = false;
                }
                try {
                    writer.close();
                } catch (IOException e) {
                    MyHome.plugin.warn("Error closing writer " + e);
                }

                Boolean finalRes = res;
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        callback.execute(finalRes);
                    }
                }.runTask(MyHome.plugin);

            }
        }.runTaskAsynchronously(MyHome.plugin);
    }

    private void saveDataSync() {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(MyHome.plugin.getDataFolder().getAbsolutePath() + File.separator + "storage" + File.separator + "json_storage.json");
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();
        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        } catch (IOException e) {
            MyHome.plugin.warn("Error creating file " + e);
        }
        Writer writer;
        try {
            writer = new FileWriter(file, false);
        } catch (IOException e) {
            MyHome.plugin.warn("Error creating file writer " + e);
            return;
        }

        gson.toJson(HomeStorage.getHomeMap(), writer);
        try {
            writer.flush();
        } catch (IOException e) {
            MyHome.plugin.warn("Error flushing file " + e);
        }
        try {
            writer.close();
        } catch (IOException e) {
            MyHome.plugin.warn("Error closing writer " + e);
        }

    }

    private void loadDataSync() {
        Gson gson = new Gson();
        File file = new File(MyHome.plugin.getDataFolder().getAbsolutePath() + File.separator + "storage" + File.separator + "json_storage.json");

        try {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        } catch (IOException ignored) {
        }

        Reader reader;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            MyHome.plugin.warn("Error creating a file reader " + e);
            return;
        }

        try {
            Type listType = new TypeToken<ConcurrentHashMap<String, ArrayList<Home>>>() {
            }.getType();
            HomeStorage.setHomeMap(gson.fromJson(reader, listType));
        } catch (Exception ex) {
            MyHome.plugin.warn("Exception reading values from JSON: " + ex);
        }
    }

    public interface Callback<T> {
        void execute(T response);
    }

}
