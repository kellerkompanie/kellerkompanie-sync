package com.kellerkompanie.kekosync.core.entities;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author dth
 * @author Schwaggot
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@EqualsAndHashCode
public class RunningModset implements Serializable {
    @Getter private String name;
    @Getter private String address;
    @Getter private String port;

    private List<Mod> mods = new ArrayList<>();

    public List<Mod> getMods() {
        return Collections.unmodifiableList(mods);
    }
}