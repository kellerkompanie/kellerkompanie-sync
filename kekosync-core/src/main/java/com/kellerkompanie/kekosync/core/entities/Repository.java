package com.kellerkompanie.kekosync.core.entities;

import lombok.*;

import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Schwaggot
 * @author dth
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Repository implements Serializable {

    @Getter @Setter private String name;
    @Getter @Setter private UUID uuid;
    private List<ModGroup> modGroups;
    private String url;

    public List<ModGroup> getModGroups() {
        return Collections.unmodifiableList(modGroups);
    }

    public void addModGroup(ModGroup modGroup) {
        modGroups.add(modGroup);
    }

    public void removeModGroup(ModGroup modGroup) {
        modGroups.remove(modGroup);
    }
}
