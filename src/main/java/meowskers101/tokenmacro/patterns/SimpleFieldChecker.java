package meowskers101.tokenmacro.patterns;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple FieldChecker implementation backed by one or more bounding boxes.
 * Use this when you manage a small set of rectangular allowed areas.
 */
public class SimpleFieldChecker implements FieldChecker {

    private final List<BoundingBox> boxes = new ArrayList<>();

    public SimpleFieldChecker() { }

    /** Add a bounding box to the allowed field list. */
    public void addBox(BoundingBox box) {
        if (box != null) boxes.add(box);
    }

    /** Add a box defined by two corner locations (may be in different worlds — caller should ensure world checks). */
    public void addBox(Location a, Location b) {
        if (a == null || b == null) return;
        // Note: this does not check world equality — if you need world checks add them in contains logic.
        addBox(new BoundingBox(a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ()));
    }

    @Override
    public boolean isInside(Location loc) {
        if (loc == null) return false;
        for (BoundingBox b : boxes) if (b.contains(loc)) return true;
        return false;
    }
}
