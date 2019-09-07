package com.kellerkompanie.kekosync.server.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author Schwaggot
 * <p>
 * Container for database table entries in the kekosync.addon table.
 */
public class SQLAddon {
    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    private String uuid;
    @Getter
    @Setter
    private String version;
    @Getter
    @Setter
    private String foldername;
    @Getter
    @Setter
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SQLAddon sqlAddon = (SQLAddon) o;
        return id == sqlAddon.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
