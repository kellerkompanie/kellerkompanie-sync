package com.kellerkompanie.kekosync.server.entities;

import com.sun.istack.internal.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author Schwaggot
 */
@EqualsAndHashCode
@ToString
public class ServerRepository implements Serializable {

    @Getter
    private final String identifier;
    @Getter
    private final String name;
    @Getter
    private final String folder;
    @Getter
    private final String url;

    public ServerRepository(@NotNull String identifier, @NotNull String name, @NotNull String folder, @NotNull String url) {
        this.identifier = identifier;
        this.name = name;
        this.folder = folder;
        this.url = url;
    }
}
