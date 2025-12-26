package meowskers101.tokenmacro.patterns;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

/**
 * Skeleton WorldGuard integration for FieldChecker.
 *
 * Behavior:
 * - If a plugin named "WorldGuard" is present on the server, this class will mark itself as available.
 * - By default (in this skeleton) {@link #isInside(Location)} will return false when WorldGuard is not present
 *   and will attempt to use WorldGuard when present. The actual WorldGuard API calls are intentionally not
 *   implemented here (keeps this class compile-safe when WorldGuard is not on the classpath).
 *
 * Implementation notes for maintainers:
 * - To enable real checks, implement the commented guidance using reflection or by adding WorldGuard as a
 *   compile-time dependency.
 * - Typical WorldGuard approach (WG7+):
 *     RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
 *     RegionQuery query = container.createQuery();
 *     ApplicableRegionSet set = query.getApplicableRegions(BlockVector or a Bukkit-adapted vector);
 *     return set.size() > 0 (or check for a specific region flag/ID).
 *
 * This skeleton logs presence/absence of WorldGuard so you can implement the integration later or choose
 * to compile-time depend on WorldGuard.
 */
public class WorldGuardFieldChecker implements FieldChecker {

    private final boolean worldGuardPresent;
    private final Plugin plugin;

    /**
     * Create checker. Pass your plugin instance so we can log helpful messages.
     */
    public WorldGuardFieldChecker(Plugin plugin) {
        this.plugin = plugin;
        Plugin wg = Bukkit.getPluginManager().getPlugin("WorldGuard");
        this.worldGuardPresent = (wg != null);
        if (plugin != null) {
            if (worldGuardPresent) {
                plugin.getLogger().info("WorldGuard detected: WorldGuardFieldChecker available (skeleton).");
                plugin.getLogger().info("Note: WorldGuardFieldChecker currently uses a skeleton implementation. Implement WG API calls or add reflection integration.");
            } else {
                plugin.getLogger().info("WorldGuard not detected: WorldGuardFieldChecker will return false for isInside.");
            }
        }
    }

    /**
     * Returns true if the location is inside the WorldGuard-allowed field.
     *
     * Current skeleton behavior:
     * - If WorldGuard is not present: returns false.
     * - If WorldGuard is present: this method still returns false until you implement actual WG API querying.
     *
     * Implementers: replace the body with either reflection-based calls or direct API calls
     * to WorldGuard's RegionQuery to check applicable regions for the given Location.
     */
    @Override
    public boolean isInside(Location loc) {
        if (loc == null) return false;
        if (!worldGuardPresent) return false;

        // TODO: Implement actual WorldGuard region checks here.
        //
        // Example guidance (pseudo-code; will not compile without WG on classpath):
        //
        // com.sk89q.worldguard.WorldGuard wg = com.sk89q.worldguard.WorldGuard.getInstance();
        // com.sk89q.worldguard.platform.RegionContainer container = wg.getPlatform().getRegionContainer();
        // com.sk89q.worldguard.protection.managers.RegionManager rm = container.get(BukkitAdapter.adapt(loc.getWorld()));
        // com.sk89q.worldguard.protection.regions.ApplicableRegionSet set = container.createQuery().getApplicableRegions(BukkitAdapter.adapt(loc));
        // return set != null && !set.getRegions().isEmpty();
        //
        // If you prefer not to add WorldGuard as a compile dependency, implement the above with reflection.
        //
        // For now return false to avoid accidental collection outside allowed areas.
        return false;
    }
}
