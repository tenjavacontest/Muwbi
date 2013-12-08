package de.muwbi.tenjava;

import com.google.common.collect.Maps;
import de.muwbi.tenjava.commands.PokeballCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Map;

public class PokeballZ extends JavaPlugin {

    private static PokeballZ instance;

    private final Map<String, Long> lastGets = Maps.newHashMap();
    private final Map<Integer, String> thrownSnowballs = Maps.newHashMap();

    public static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "PokeballZ" + ChatColor.GRAY + "] ";

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        getCommand("pokeball").setExecutor(new PokeballCommand());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    public Map<String, Long> getLastGets() {
        return lastGets;
    }

    public ItemStack getItemStack(int amount) {
        ItemStack itemStack = new ItemStack(Material.SNOW_BALL, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColor.DARK_PURPLE + "Pokeball");
        itemMeta.setLore(Arrays.asList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Throw to catch a mob"));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public Map<Integer, String> getThrownSnowballs() {
        return thrownSnowballs;
    }

    public static PokeballZ getInstance() {
        return instance;
    }

}
