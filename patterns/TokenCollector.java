package meowskers101.tokenmacro.collector;

import meowskers101.tokenmacro.patterns.FieldChecker;
import meowskers101.tokenmacro.patterns.TargetedSampler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * AutoCollector: scheduled task you can instantiate from your plugin to detect nearby
 * Item entities (tokens) and collect them into players' inventories. This class does
 * not move players; it samples along paths toward tokens and attempts to pick items
 * by adding them to the player's inventory and removing the entity if fully picked.
 *
 * To use:
 *   AutoCollector collector = new AutoCollector(plugin, fieldChecker);
 *   collector.start();
 *
 * The collector reads configuration from the plugin's config() under the "auto_collect"
 * section if present. Defaults are used when keys are missing.
 */
public class AutoCollector {

    private final Plugin plugin;
    private final FieldChecker fieldChecker; // may be null (no field restrictions)
    private BukkitTask task;

    public AutoCollector(Plugin plugin, FieldChecker fieldChecker) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.fieldChecker = fieldChecker;
    }

    /** Start or restart the collector task. */
    public void start() {
        stop();
        int interval = plugin.getConfig().getInt("auto_collect.interval_ticks", 20);
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::runOnce, 0L, Math.max(1, interval));
        plugin.getLogger().info("AutoCollector started (interval_ticks=" + interval + ").");
    }

    /** Stop the collector task if running. */
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
            plugin.getLogger().info("AutoCollector stopped.");
        }
    }

    /** Single run: detect tokens and try to collect them for each online player. */
    private void runOnce() {
        if (!plugin.getConfig().getBoolean("auto_collect.enabled", true)) return;

        int radius = plugin.getConfig().getInt("auto_collect.radius", 6);
        int spacing = plugin.getConfig().getInt("auto_collect.spacing", 1);
        int maxPoints = plugin.getConfig().getInt("auto_collect.max_points_per_token", 8);
        int maxPerPlayer = plugin.getConfig().getInt("auto_collect.max_per_player_per_tick", 64);
        String soundName = plugin.getConfig().getString("auto_collect.pickup_sound", "ENTITY_ITEM_PICKUP");

        // whitelist (Material names) - empty means accept all
        List<String> wl = plugin.getConfig().getStringList("auto_collect.whitelist");
        Set<String> whitelist = new HashSet<>();
        for (String s : wl) if (s != null && !s.trim().isEmpty()) whitelist.add(s.trim().toUpperCase(Locale.ROOT));

        Set<UUID> processed = new HashSet<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == null || !player.isOnline()) continue;
            Location playerLoc = player.getLocation();

            // gather nearby item entities (tokens) around player within radius
            List<Location> tokenLocs = new ArrayList<>();
            Collection<Entity> nearby = player.getNearbyEntities(radius, 2.0, radius);
            for (Entity e : nearby) {
                if (!(e instanceof Item)) continue;
                Item item = (Item) e;
                if (processed.contains(item.getUniqueId())) continue;
                Location loc = item.getLocation();
                if (fieldChecker != null && !fieldChecker.isInside(loc)) continue;
                ItemStack stack = item.getItemStack();
                if (stack == null) continue;
                if (!whitelist.isEmpty() && !whitelist.contains(stack.getType().name())) continue;
                tokenLocs.add(loc);
            }

            if (tokenLocs.isEmpty()) continue;

            // produce absolute sample locations that move toward tokens
            List<Location> samples = TargetedSampler.sampleTowardsTokensAsLocations(playerLoc, tokenLocs, radius, spacing, maxPoints);
            int collectedThisPlayer = 0;

            outer:
            for (Location sample : samples) {
                if (collectedThisPlayer >= maxPerPlayer) break;
                Collection<Entity> ents = sample.getWorld().getNearbyEntities(sample, 0.75, 1.0, 0.75);
                for (Entity ent : ents) {
                    if (collectedThisPlayer >= maxPerPlayer) break outer;
                    if (!(ent instanceof Item)) continue;
                    Item item = (Item) ent;
                    if (processed.contains(item.getUniqueId())) continue;
                    ItemStack stack = item.getItemStack();
                    if (stack == null) continue;

                    // whitelist check again
                    if (!whitelist.isEmpty() && !whitelist.contains(stack.getType().name())) continue;

                    // try to add to player's inventory
                    ItemStack clone = stack.clone();
                    Map<Integer, ItemStack> leftover = player.getInventory().addItem(clone);
                    if (leftover.isEmpty()) {
                        // fully picked up
                        item.remove();
                        collectedThisPlayer += clone.getAmount();
                    } else {
                        int remaining = 0;
                        for (ItemStack s : leftover.values()) if (s != null) remaining += s.getAmount();
                        int picked = clone.getAmount() - remaining;
                        if (picked > 0) {
                            ItemStack rem = stack.clone();
                            rem.setAmount(remaining);
                            item.setItemStack(rem);
                            collectedThisPlayer += picked;
                        } else {
                            // none picked, inventory full
                            continue;
                        }
                    }

                    // play pickup sound if possible
                    try {
                        Sound sound = Sound.valueOf(soundName);
                        player.playSound(player.getLocation(), sound, 0.7f, 1.0f);
                    } catch (IllegalArgumentException ignore) {
                    }

                    processed.add(item.getUniqueId());
                }
            }
        }
    }
}
