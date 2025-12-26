package meowskers101.tokenmacro.patterns;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Samples concentric rings around the center (XZ plane).
 * Produces roughly evenly spaced points per ring depending on circumference.
 */
public class CirclePattern implements PatternShape {
    @Override
    public List<Vector> sampleOffsets(int radius, int spacing) {
        List<Vector> offsets = new ArrayList<>();
        int s = Math.max(1, spacing);
        for (int r = 0; r <= radius; r += s) {
            if (r == 0) {
                offsets.add(new Vector(0, 0, 0));
                continue;
            }
            int points = Math.max(8, (int) (2 * Math.PI * r / s));
            for (int i = 0; i < points; i++) {
                double theta = 2 * Math.PI * i / points;
                int dx = (int) Math.round(r * Math.cos(theta));
                int dz = (int) Math.round(r * Math.sin(theta));
                offsets.add(new Vector(dx, 0, dz));
            }
        }
        return offsets;
    }
}
