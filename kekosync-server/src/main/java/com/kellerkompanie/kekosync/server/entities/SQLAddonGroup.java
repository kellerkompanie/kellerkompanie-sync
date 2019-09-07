package com.kellerkompanie.kekosync.server.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class SQLAddonGroup {
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
    private String name;
    @Getter
    @Setter
    private String author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SQLAddonGroup sqlAddon = (SQLAddonGroup) o;
        return id == sqlAddon.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
