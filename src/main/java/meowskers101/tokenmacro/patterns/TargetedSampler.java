package meowskers101.tokenmacro.patterns;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Utility that generates an ordered list of relative XZ offsets that move toward
 * given token Locations from the player's location, and an overload that returns
 * absolute Locations.
 *
 * - All sampling is performed on the XZ plane (y = 0). If you need vertical sampling,
 *   call the absolute overload and then expand vertically as needed in your collector.
 */
public final class TargetedSampler {

    private TargetedSampler() { /* static helper */ }

    /**
     * Produce offsets toward tokens (relative vectors, y = 0).
     *
     * @param playerLoc player's location (center)
     * @param tokenLocs list of token locations to target (may be empty)
     * @param radius max radius to sample (blocks)
     * @param spacing sampling spacing (blocks, >=1)
     * @param maxPointsPerToken max number of points to generate per token (safety cap)
     * @return ordered List of Vector offsets (x,y,z) relative to playerLoc (y = 0)
     */
    public static List<Vector> sampleTowardsTokens(Location playerLoc,
                                                  List<Location> tokenLocs,
                                                  int radius,
                                                  int spacing,
                                                  int maxPointsPerToken) {
        if (playerLoc == null) return Collections.emptyList();
        int s = Math.max(1, spacing);
        int maxPoints = Math.max(1, maxPointsPerToken);
        // sort tokens by horizontal distance (XZ) ascending
        List<Location> tokens = new ArrayList<>(tokenLocs == null ? Collections.emptyList() : tokenLocs);
        tokens.sort(Comparator.comparingDouble(loc -> horizontalDistanceSquared(playerLoc, loc)));

        List<Vector> result = new ArrayList<>();
        // use set of "x:z" keys to avoid duplicates
        Set<String> seen = new HashSet<>();

        for (Location token : tokens) {
            if (token == null) continue;
            double dx = token.getX() - playerLoc.getX();
            double dz = token.getZ() - playerLoc.getZ();
            double horizDist = Math.sqrt(dx * dx + dz * dz);
            if (horizDist < 0.0001) {
                // token is essentially at the player's XZ — include center
                addIfNew(result, seen, 0, 0);
                continue;
            }
            double cappedDist = Math.min(horizDist, Math.max(0, radius));
            double nx = dx / horizDist; // normalized X on XZ plane
            double nz = dz / horizDist; // normalized Z on XZ plane

            int points = Math.min(maxPoints, 1 + (int)Math.ceil(cappedDist / s));
            for (int i = 1; i <= points; i++) {
                double distAlong = Math.min(i * s, cappedDist);
                double px = nx * distAlong;
                double pz = nz * distAlong;
                int ix = (int) Math.round(px);
                int iz = (int) Math.round(pz);
                // ensure offset inside radius
                if (Math.sqrt(ix * ix + iz * iz) > radius) continue;
                addIfNew(result, seen, ix, iz);
                // stop early if we already reached token XZ
                if (Math.abs(distAlong - horizDist) < 0.5) break;
            }
        }

        // always ensure center is present as first element
        String centerKey = key(0,0);
        if (!seen.contains(centerKey)) {
            result.add(0, new Vector(0, 0, 0));
        } else {
            // move the center to index 0 if it exists elsewhere
            for (int i = 0; i < result.size(); i++) {
                Vector v = result.get(i);
                if (v.getBlockX() == 0 && v.getBlockZ() == 0) {
                    if (i != 0) {
                        result.remove(i);
                        result.add(0, new Vector(0,0,0));
                    }
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Overload: produce absolute Locations to sample (based on playerLoc).
     *
     * This returns Locations with the same world and Y coordinate as playerLoc (y unchanged).
     * If you want sampling at token heights or to scan vertically, expand the returned locations
     * with vertical offsets in your collector.
     *
     * @param playerLoc player's location (center)
     * @param tokenLocs list of token locations to target
     * @param radius max radius to sample (blocks)
     * @param spacing sampling spacing (blocks, >=1)
     * @param maxPointsPerToken max number of points to generate per token (safety cap)
     * @return ordered List of absolute Locations to check
     */
    public static List<Location> sampleTowardsTokensAsLocations(Location playerLoc,
                                                                List<Location> tokenLocs,
                                                                int radius,
                                                                int spacing,
                                                                int maxPointsPerToken) {
        List<Vector> rel = sampleTowardsTokens(playerLoc, tokenLocs, radius, spacing, maxPointsPerToken);
        if (rel.isEmpty()) return Collections.emptyList();
        List<Location> out = new ArrayList<>(rel.size());
        for (Vector v : rel) {
            Location sample = playerLoc.clone().add(v);
            out.add(sample);
        }
        return out;
    }

    private static void addIfNew(List<Vector> list, Set<String> seen, int x, int z) {
        String k = key(x, z);
        if (seen.add(k)) {
            list.add(new Vector(x, 0, z));
        }
    }

    private static String key(int x, int z) {
        return x + ":" + z;
    }

    private static double horizontalDistanceSquared(Location a, Location b) {
        if (a == null || b == null) return Double.MAX_VALUE;
        double dx = a.getX() - b.getX();
        double dz = a.getZ() - b.getZ();
        return dx*dx + dz*dz;
    }
}
