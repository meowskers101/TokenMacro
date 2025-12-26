package meowskers101.tokenmacro.patterns;

import org.bukkit.Location;

/**
 * Abstraction for detecting whether a Location is "in the field" (inside an allowed/target area).
 * Implementations may check WorldGuard regions, claim plugins, or simple bounding boxes.
 */
public interface FieldChecker {
    /**
     * Return true if the given location is considered inside the field/area where tokens
     * should be targeted/collected.
     */
    boolean isInside(Location loc);
}
