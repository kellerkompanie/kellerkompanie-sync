package com.kellerkompanie.kekosync.core;

import lombok.*;

import java.net.URL;
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
public class Repository {

    @Getter @Setter private String name;
    @Getter @Setter private UUID uuid;
    private List<ModGroup> modGroups;
    private URL url;

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
