package noexiph.siftear.entity.blub;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum BlubState implements StringRepresentable {
    WANDER("wander", 0),
    STAY("stay", 1),
    FOLLOW("follow", 2);

    public static final Codec<BlubState> CODEC = StringRepresentable.fromEnum(BlubState::values);
    private static final BlubState[] VALUES = values();

    private final String name;
    private final int id;

    BlubState(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }

    public BlubState next() {
        return switch (this) {
            case WANDER -> STAY;
            case STAY -> FOLLOW;
            case FOLLOW -> WANDER;
        };
    }

    public static BlubState fromId(int id) {
        if (id < 0 || id >= VALUES.length) {
            return WANDER;
        }
        return VALUES[id];
    }
}