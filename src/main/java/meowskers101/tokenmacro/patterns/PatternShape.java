package meowskers101.tokenmacro.patterns;

import org.bukkit.util.Vector;

import java.util.List;

/**
 * Produces a list of relative offsets (Vector) to sample around a center location.
 * Offsets are in block coordinates (x, y, z). Implementations sample on the XZ plane
 * and return vectors with y = 0 by convention.
 */
public interface PatternShape {
    /**
     * @param radius  max radius in blocks (inclusive)
     * @param spacing spacing between sample points in blocks (>=1)
     * @return list of relative offsets (x,y,z) to inspect/check
     */
    List<Vector> sampleOffsets(int radius, int spacing);
}
