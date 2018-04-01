package com.kellerkompanie.kekosync.core.entities;

import lombok.*;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Schwaggot
 * @author dth
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@EqualsAndHashCode
public class Mod {
    @Getter @Setter private String name;
    @Getter         private UUID uuid;
}
