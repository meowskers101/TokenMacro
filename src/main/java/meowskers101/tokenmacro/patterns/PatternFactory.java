package meowskers101.tokenmacro.patterns;

/**
 * Simple factory for obtaining a PatternShape by name.
 * Names (case-insensitive): "spiral", "circle", "cross".
 * Falls back to SpiralPattern.
 */
public final class PatternFactory {

    public static PatternShape get(String name) {
        if (name == null) return new SpiralPattern();
        switch (name.toLowerCase()) {
            case "circle":
                return new CirclePattern();
            case "cross":
                return new CrossPattern();
            case "spiral":
            default:
                return new SpiralPattern();
        }
    }

    private PatternFactory() { /* no instantiation */ }
}
