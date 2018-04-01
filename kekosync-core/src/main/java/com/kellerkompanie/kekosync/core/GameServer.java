package com.kellerkompanie.kekosync.core;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Schwaggot
 */
public class GameServer {

    private String name;
    private UUID uuid;
    private String address;
    private String port;

    private GameServer() {
    }

    public GameServer(String name, UUID uuid, String address, String port) {
        this.name = name;
        this.uuid = uuid;
        this.address = address;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getAddress() {
        return address;
    }

    public String getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameServer that = (GameServer) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(uuid, that.uuid) &&
                Objects.equals(address, that.address) &&
                Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, uuid, address, port);
    }
}
