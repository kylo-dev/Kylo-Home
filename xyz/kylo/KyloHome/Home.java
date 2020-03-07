package xyz.kylo.KyloHome;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Home extends JavaPlugin {
  public FileConfiguration config;
  
  public void onEnable() {
    this.config = getConfig();
    this.config.options().copyDefaults(true);
    saveConfig();
    getCommand("home").setExecutor(new CmdHome());
  }
  
  public void setHome(Player caller, String[] args) {
    List<String> homes = new ArrayList();
    String uuid = caller.getUniqueId().toString();
    if (this.config.isSet(uuid)) {
      if (homes.indexOf(args[1]) == -1) {
        homes = this.config.getList(uuid);
        homes.add(args[1]);
        homes.add(caller.getLocation());
        this.config.set(uuid, homes);
      } else {
        caller.sendMessage("§6You already have a home called \"§l" + args[1] + "§r§6\"! Try again after removing.");
        return;
      } 
    } else {
      homes.add(args[1]);
      homes.add(caller.getLocation());
      this.config.set(uuid, homes);
    } 
    caller.sendMessage("§6Your Home §l" + args[1] + "§r§6 has been successfully set!");
    saveConfig();
  }
  
  public void viewHome(Player caller) {
    List<String> homes = new ArrayList();
    String uuid = caller.getUniqueId().toString();
    if (this.config.isSet(uuid)) {
      homes = this.config.getList(uuid);
      caller.sendMessage("§6---------Your Home------------");
      String tmp = "";
      for (int i = 0; i < homes.size(); i++) {
        if (i % 2 == 0) {
          tmp = "§6§l" + homes.get(i) + "§r§6: ";
        } else {
          Location tmploc = (Location)homes.get(i);
          tmp = tmp + tmploc.getX() + " " + tmploc.getY() + " " + tmploc.getZ();
          caller.sendMessage(tmp);
        } 
      } 
    } else {
      caller.sendMessage("§6Sorry, but you haven't set any home.");
    } 
  }
  
  public void delHome(Player caller, String[] args) {
    List homes = new ArrayList();
    String uuid = caller.getUniqueId().toString();
    if (this.config.isSet(uuid)) {
      homes = this.config.getList(uuid);
      int tmpos = homes.indexOf(args[1]);
      if (tmpos != -1) {
        homes.remove(tmpos);
        homes.remove(tmpos);
        saveConfig();
        caller.sendMessage("§6Your Home §l" + args[1] + "§r§6 has been successfully removed!");
      } else {
        caller.sendMessage("§6Sorry, but you don't have a home called \"§l" + args[1] + "§r§6\".");
      } 
    } else {
      caller.sendMessage("§6Sorry, but you haven't set any home.");
    } 
  }
  
  public void goHome(Player caller, String[] args) {
    List<Location> homes = new ArrayList();
    String uuid = caller.getUniqueId().toString();
    if (this.config.isSet(uuid)) {
      homes = this.config.getList(uuid);
      int tmpos = homes.indexOf(args[1]);
      if (tmpos != -1) {
        Location tmploc = homes.get(tmpos + 1);
        caller.teleport(tmploc);
        caller.sendMessage("§6Teleport to §l" + args[1] + "§r§6 successfully!");
      } else {
        caller.sendMessage("§6Sorry, but you don't have a home called \"§l" + args[1] + "§r§6\".");
      } 
    } else {
      caller.sendMessage("§6Sorry, but you haven't set any home.");
    } 
  }
  
  public class CmdHome implements CommandExecutor, TabCompleter {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player) {
        Player player = (Player)sender;
        if (args.length == 0) {
          player.sendMessage("§cUsage: /home go|set|del|list");
          return true;
        } 
        if (args.length == 1) {
          switch (args[0]) {
            case "go":
              player.sendMessage("§cUsage: /home go <HomeName>");
              return true;
            case "set":
              player.sendMessage("§cUsage: /home set <HomeName>");
              return true;
            case "del":
              player.sendMessage("§cUsage: /home del <HomeName>");
            case "list":
              Home.this.viewHome(player);
              return true;
          } 
          player.sendMessage("§cUsage: /home go|set|del|list");
          return true;
        } 
        switch (args[0]) {
          case "go":
            Home.this.goHome(player, args);
            return true;
          case "set":
            Home.this.setHome(player, args);
            return true;
          case "del":
            Home.this.delHome(player, args);
            return true;
        } 
        player.sendMessage("§cUsage: /home go|set|del|list");
      } 
      return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
      ArrayList<String> l = new ArrayList<>();
      Player p = (Player)sender;
      String pn = p.getName();
      if (cmd.getName().equalsIgnoreCase("home"))
        if (args.length == 1) {
          if (args[0].startsWith("d")) {
            l.add("del");
            return l;
          } 
          if (args[0].startsWith("g")) {
            l.add("go");
            return l;
          } 
          if (args[0].startsWith("l")) {
            l.add("list");
            return l;
          } 
          if (args[0].startsWith("s")) {
            l.add("set");
            return l;
          } 
          if (args[0].startsWith("")) {
            l.add("del");
            l.add("go");
            l.add("list");
            l.add("set");
            return l;
          } 
        } else if (args.length == 2) {
          if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("go")) {
            String uuid = p.getUniqueId().toString();
            if (Home.this.config.isSet(uuid))
              l = (ArrayList<String>)Home.this.config.getList(uuid); 
            return null;
          } 
          return l;
        }  
      return null;
    }
  }
}
