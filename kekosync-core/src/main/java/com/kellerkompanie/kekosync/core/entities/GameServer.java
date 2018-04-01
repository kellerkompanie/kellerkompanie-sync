package com.kellerkompanie.kekosync.core.entities;

import lombok.*;

import java.io.Serializable;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Schwaggot
 * @author dth
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@EqualsAndHashCode
public class GameServer implements Serializable {
    @Getter private String name;
    @Getter private UUID uuid;
    @Getter private String address;
    @Getter private String port;
    @Getter private String currentlyRunningModsetURL;

}
