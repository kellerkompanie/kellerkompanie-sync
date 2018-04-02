package com.kellerkompanie.kekosync.core.entities;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author dth
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@EqualsAndHashCode
public class GameServerCurrentlyRunningModset implements Serializable {
    @Getter private String name;
    @Getter private String address;
    @Getter private String port;

    private List<Mod> requiredMods = new ArrayList<>();
    private List<Mod> optionalMods = new ArrayList<>();

    public List<Mod> getRequiredMods() {
        return Collections.unmodifiableList(requiredMods);
    }
    public List<Mod> getOptionalMods() {
        return Collections.unmodifiableList(optionalMods);
    }

    public void addRequiredMod(Mod mod) {
        requiredMods.add(mod);
    }
    public void removeRequiredMod(Mod mod) {
        requiredMods.remove(mod);
    }

    public void addOptionalMod(Mod mod) { optionalMods.add(mod); }
    public void removeOptionalMod(Mod mod) { optionalMods.remove(mod); }
}