package meowskers101.tokenmacro.patterns;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Integer outward square spiral sampling (XZ plane).
 * Good for covering the area from center outward.
 */
public class SpiralPattern implements PatternShape {

    @Override
    public List<Vector> sampleOffsets(int radius, int spacing) {
        List<Vector> offsets = new ArrayList<>();
        int x = 0, z = 0, dx = 0, dz = -1;
        int diameter = 2 * radius + 1;
        int max = diameter * diameter;
        for (int i = 0; i < max; i++) {
            if (Math.abs(x) <= radius && Math.abs(z) <= radius) {
                if ((Math.abs(x) % spacing == 0) && (Math.abs(z) % spacing == 0)) {
                    offsets.add(new Vector(x, 0, z));
                }
            }
            if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) {
                int tmp = dx;
                dx = -dz;
                dz = tmp;
            }
            x += dx;
            z += dz;
        }
        // ensure center present and first in list
        if (offsets.isEmpty() || !offsets.get(0).equals(new Vector(0,0,0))) {
            offsets.add(0, new Vector(0,0,0));
        }
        return offsets;
    }
}
