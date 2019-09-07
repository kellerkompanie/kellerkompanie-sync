package com.kellerkompanie.kekosync.server.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author Schwaggot
 * <p>
 * Container for database table entries in the kekosync.addon_group_member table.
 */
public class SQLAddonGroupMember {
    @Getter
    @Setter
    private int addonGroupId;
    @Getter
    @Setter
    private int addonId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SQLAddonGroupMember that = (SQLAddonGroupMember) o;
        return addonGroupId == that.addonGroupId &&
                addonId == that.addonId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(addonGroupId, addonId);
    }
}
