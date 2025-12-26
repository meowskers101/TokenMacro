package meowskers101.tokenmacro.patterns;

import org.bukkit.Location;

/**
 * Simple axis-aligned bounding box for field checks.
 */
public final class BoundingBox {
    private final double minX, minY, minZ, maxX, maxY, maxZ;
    public BoundingBox(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }
    public boolean contains(Location loc) {
        if (loc == null) return false;
        double x = loc.getX(), y = loc.getY(), z = loc.getZ();
        return x >= minX && x <= maxX
            && y >= minY && y <= maxY
            && z >= minZ && z <= maxZ;
    }
}
