package meowskers101.tokenmacro.patterns;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Fast low-cost cross pattern (cardinal axes).
 * Checks center and cardinal points at spacing steps.
 */
public class CrossPattern implements PatternShape {
    @Override
    public List<Vector> sampleOffsets(int radius, int spacing) {
        List<Vector> offsets = new ArrayList<>();
        offsets.add(new Vector(0, 0, 0));
        int s = Math.max(1, spacing);
        for (int d = s; d <= Math.max(1, radius); d += s) {
            offsets.add(new Vector(d, 0, 0));
            offsets.add(new Vector(-d, 0, 0));
            offsets.add(new Vector(0, 0, d));
            offsets.add(new Vector(0, 0, -d));
        }
        return offsets;
    }
}
