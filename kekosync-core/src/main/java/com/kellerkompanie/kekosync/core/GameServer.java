package com.kellerkompanie.kekosync.core;

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
public class GameServer {
    @Getter private String name;
    @Getter private UUID uuid;
    @Getter private String address;
    @Getter private String port;
}
