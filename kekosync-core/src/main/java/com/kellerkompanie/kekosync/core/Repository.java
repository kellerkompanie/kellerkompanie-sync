package com.kellerkompanie.kekosync.core;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Schwaggot
 */
public class Repository {

    private String name;
    private UUID uuid;
    private List<ModGroup> modGroups;
    private URL url;

    private Repository() {}

    public Repository(String name, UUID uuid, List<ModGroup> modGroups, URL url) {
        this.name = name;
        this.uuid = uuid;
        this.modGroups = modGroups;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public List<ModGroup> getModGroups() {
        return Collections.unmodifiableList(modGroups);
    }

    public void addModGroup(ModGroup modGroup) {
        modGroups.add(modGroup);
    }

    public void removeModGroup(ModGroup modGroup) {
        modGroups.remove(modGroup);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Repository that = (Repository) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(uuid, that.uuid) &&
                Objects.equals(modGroups, that.modGroups) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, uuid, modGroups, url);
    }
}
