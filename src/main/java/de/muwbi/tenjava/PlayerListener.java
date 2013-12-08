package de.muwbi.tenjava;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.SpawnEgg;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
class PlayerListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Snowball) {
            if (event.getEntity() instanceof Player || !(event.getEntity() instanceof LivingEntity)) {
                return;
            }

            Player player = (Player) ((Snowball) event.getDamager()).getShooter();

            Snowball snowball = (Snowball) event.getDamager();
            LivingEntity mob = (LivingEntity) event.getEntity();
            EntityType entityType = event.getEntityType();
            if (PokeballZ.getInstance().getThrownSnowballs().containsKey(snowball.getEntityId())) {
                PokeballZ.getInstance().getThrownSnowballs().remove(snowball.getEntityId());

                if (mob.hasMetadata("owner") && mob.getMetadata("owner").get(0).asString().equalsIgnoreCase(player.getName())) {
                    player.sendMessage(PokeballZ.PREFIX + ChatColor.GREEN + "Come back, " + entityType.getName().toLowerCase() + "!");
                } else {
                    if (new Random().nextBoolean()) {
                        player.sendMessage(PokeballZ.PREFIX + ChatColor.GREEN + "Congratulations, you've caught a " + entityType.getName().toLowerCase() + "!");
                    } else {
                        player.sendMessage(PokeballZ.PREFIX + ChatColor.RED + "Your attempt to catch this mob failed!");
                        return;
                    }
                }

                SpawnEgg spawnEgg = new SpawnEgg(entityType);
                ItemStack itemStack = spawnEgg.toItemStack(1);
                ItemMeta itemMeta = itemStack.getItemMeta();

                String capitalizedName = WordUtils.capitalizeFully(entityType.getName().toLowerCase());
                itemMeta.setDisplayName(ChatColor.GOLD + "Pokeball: " + capitalizedName + ChatColor.RESET);
                itemMeta.setLore(Arrays.asList(ChatColor.GRAY + "Mob: " + capitalizedName,
                        ChatColor.GRAY + "Owner: " + player.getName()));

                itemStack.setItemMeta(itemMeta);

                player.getInventory().addItem(itemStack);

                mob.remove();
            }
        }
    }


    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (PokeballZ.getInstance().getThrownSnowballs().containsKey(event.getEntity().getEntityId())) {
            PokeballZ.getInstance().getThrownSnowballs().remove(event.getEntity().getEntityId());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) && item.getType() == Material.SNOW_BALL
                && item.hasItemMeta()
                && item.getItemMeta().getDisplayName().equals(ChatColor.DARK_PURPLE + "Pokeball")) {
            event.setCancelled(true);

            Snowball snowball = player.launchProjectile(Snowball.class);
            snowball.setShooter(player);

            PokeballZ.getInstance().getThrownSnowballs().put(snowball.getEntityId(), player.getName());

            //Workaround to remove pokeball
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack itemStack = player.getInventory().getItem(i);
                if (itemStack != null && itemStack.getType() == Material.SNOW_BALL && itemStack.hasItemMeta()
                        && itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "Pokeball")) {
                    itemStack.setAmount(itemStack.getAmount() - 1);
                    player.getInventory().setItem(i, itemStack);
                }
            }
        } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && item != null && item.getType() == Material.MONSTER_EGG
                && item.hasItemMeta()
                && item.getItemMeta().getDisplayName().startsWith(ChatColor.GOLD + "Pokeball:")) {
            event.setCancelled(true);

            List<String> lore = item.getItemMeta().getLore();

            String owner = lore.get(1).split(": ")[1];
            String mob = WordUtils.capitalizeFully(lore.get(0).split(": ")[1]);

            if (!player.getName().equalsIgnoreCase(owner)) {
                player.sendMessage(PokeballZ.PREFIX + ChatColor.RED + "This is not your mob!");
                return;
            }

            Location clickedAt = event.getClickedBlock().getLocation();
            clickedAt.setY(clickedAt.getY() + 1.0);

            LivingEntity entity = clickedAt.getWorld().spawnCreature(clickedAt, EntityType.valueOf(mob.toUpperCase()));

            entity.setCustomName(ChatColor.GOLD + owner + "'s " + mob);
            entity.setCustomNameVisible(true);

            entity.setMetadata("owner", new FixedMetadataValue(PokeballZ.getInstance(), owner));

            //Workaround to remove pokeball
            for(int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack itemStack = player.getInventory().getItem(i);
                if(itemStack != null && itemStack.getType() == Material.MONSTER_EGG && itemStack.hasItemMeta()
                        && itemStack.getItemMeta().getDisplayName().startsWith(ChatColor.GOLD + "Pokeball:")) {
                    itemStack.setAmount(itemStack.getAmount() - 1);
                    player.getInventory().setItem(i, itemStack);
                }
            }

            player.getInventory().addItem(PokeballZ.getInstance().getItemStack(1));

            player.updateInventory();
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if(player.getItemInHand() != null && player.getItemInHand().getType() == Material.MONSTER_EGG
                && player.getItemInHand().hasItemMeta()
                && player.getItemInHand().getItemMeta().getDisplayName().startsWith(ChatColor.GOLD + "Pokeball:")) {
            event.setCancelled(true);
        }
    }

}
