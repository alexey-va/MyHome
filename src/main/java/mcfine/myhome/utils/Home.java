package mcfine.myhome.utils;

import mcfine.myhome.MyHome;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;

public class Home {

    private double x, y, z;
    private float yaw, pitch;
    private String worldName;
    private String homeName;
    private String playerName;
    private boolean isPublic;
    private ArrayList<String> invitedPlayers;
    private Date lastEnter;
    private int visits;

    public Home(double x, double y, double z, float yaw, float pitch, String homeName, String playerName, ArrayList<String> invitedNames, String worldName, boolean isPublic) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.homeName = homeName;
        this.playerName = playerName;
        this.invitedPlayers = invitedNames;
        this.worldName = worldName;
        this.isPublic = isPublic;
        lastEnter = new Date();
        visits = 0;
    }

    public Home(Player p, String homeName) {
        this.x = p.getLocation().getX();
        this.y = p.getLocation().getY();
        this.z = p.getLocation().getZ();
        this.homeName = homeName;
        this.invitedPlayers = new ArrayList<>();
        this.worldName = p.getLocation().getWorld().getName();
        this.pitch = p.getLocation().getPitch();
        this.yaw = p.getLocation().getYaw();
        this.playerName = p.getName();
        isPublic = MyHome.plugin.getConfig().getBoolean("public-by-default");
        lastEnter = new Date();
        visits = 0;
    }

    public Home(Player p, Location loc, String homeName) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.homeName = homeName;
        this.invitedPlayers = new ArrayList<>();
        this.worldName = loc.getWorld().getName();
        this.pitch = loc.getPitch();
        this.yaw = loc.getYaw();
        this.playerName = p.getName();
        isPublic = MyHome.plugin.getConfig().getBoolean("public-by-default");
        lastEnter = new Date();
    }

    public Home(double x, double y, double z, float yaw, float pitch, String homeName, String playerName, ArrayList<String> strings, String worldName, boolean isPublic, java.util.Date lastEnter, int visits) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.homeName = homeName;
        this.playerName = playerName;
        this.invitedPlayers = strings;
        this.worldName = worldName;
        this.isPublic = isPublic;
        this.lastEnter = lastEnter;
        this.visits = visits;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public ArrayList<String> getInvitedPlayers() {
        return invitedPlayers;
    }

    public void setInvitedPlayers(ArrayList<String> invitedPlayers) {
        this.invitedPlayers = invitedPlayers;
    }

    public Date getLastEnter() {
        return lastEnter;
    }

    public void setLastEnter(Date lastEnter) {
        this.lastEnter = lastEnter;
    }

    public boolean isInvited(String name) {
        return invitedPlayers.contains(name);
    }


    public void invitePlayer(String name) {
        if (invitedPlayers.contains(name)) {
        }
        else {
            invitedPlayers.add(name);
        }
    }

    public void uninvitePlayer(String name) {
        if (!invitedPlayers.contains(name)) {
        }
        else {
            invitedPlayers.remove(name);
        }
    }

    public void setPublic(boolean pub) {
        if (isPublic == pub) {
        }
        else {
            isPublic = pub;
        }
    }

    public Location getLocation() {
        Location location;
        try {
            location = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
            return location;
        } catch (Exception ex) {
            System.out.println("Error getting location " + ex);
            return null;
        }
    }


    public void updateDate() {
        this.lastEnter = new Date();
    }

    public void incrementVisits() {
        this.visits += 1;
    }
}
