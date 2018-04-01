package com.kellerkompanie.kekosync.core;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Schwaggot
 */
public class Mod {

    private String name;
    private UUID uuid;

    private Mod() {}

    public Mod(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mod mod = (Mod) o;
        return Objects.equals(name, mod.name) &&
                Objects.equals(uuid, mod.uuid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, uuid);
    }
}
