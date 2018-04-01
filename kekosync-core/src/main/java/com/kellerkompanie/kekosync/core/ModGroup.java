package com.kellerkompanie.kekosync.core;

import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Schwaggot
 * @author dth
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@EqualsAndHashCode
public class ModGroup {

    @Getter @Setter private String name;
    private UUID uuid;
    private List<Mod> mods;

    public List<Mod> getMods() {
        return Collections.unmodifiableList(mods);
    }

    public void addMod(Mod mod) {
        mods.add(mod);
    }

    public void removeMod(Mod mod) {
        mods.remove(mod);
    }
}
