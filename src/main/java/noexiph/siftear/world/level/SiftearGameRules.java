package noexiph.siftear.world.level;

import noexiph.siftear.Siftear;

public final class SiftearGameRules {
    public static void initialize() {
        Siftear.LOGGER.info("Registered gamerules for {}", Siftear.MOD_ID);
    }

    private SiftearGameRules() {}
}