package com.kellerkompanie.kekosync.core.entities;

import lombok.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * @author Schwaggot
 * @author dth
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@EqualsAndHashCode(of = {"uuid"})
@ToString
public class ModGroup implements Serializable {

    @Getter
    @Setter
    private String name;
    @Getter
    private UUID uuid;
    private Set<Mod> mods;

    public Set<Mod> getMods() {
        return Collections.unmodifiableSet(mods);
    }

    public void addMod(Mod mod) {
        mods.add(mod);
    }

    public void removeMod(Mod mod) {
        mods.remove(mod);
    }
}
