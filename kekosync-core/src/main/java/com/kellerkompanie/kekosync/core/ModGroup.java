package com.kellerkompanie.kekosync.core;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Schwaggot
 */
public class ModGroup {

    private String name;
    private UUID uuid;
    private List<Mod> mods;

    private ModGroup() {}

    public ModGroup(String name, UUID uuid, List<Mod> mods) {
        this.name = name;
        this.uuid = uuid;
        this.mods = mods;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Mod> getMods() {
        return Collections.unmodifiableList(mods);
    }

    public void addMod(Mod mod) {
        mods.add(mod);
    }

    public void removeMod(Mod mod) {
        mods.remove(mod);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModGroup modGroup = (ModGroup) o;
        return Objects.equals(name, modGroup.name) &&
                Objects.equals(uuid, modGroup.uuid) &&
                Objects.equals(mods, modGroup.mods);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, uuid, mods);
    }
}
