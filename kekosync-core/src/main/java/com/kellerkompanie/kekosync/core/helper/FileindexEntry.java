package com.kellerkompanie.kekosync.core.helper;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class FileindexEntry implements Serializable {
    @Getter @Setter private String name;
    @Getter @Setter private long size;
    @Getter @Setter private boolean directory;
    @Getter @Setter private String UUID; //this property is filled with the content of the .id-file in case this is a primary mod-folder
    @Getter @Setter private String hash;
    @Getter @Setter private long lastModified;
    @Getter private List<FileindexEntry> children = new ArrayList<>();
    @Getter @Setter private transient FileindexEntry parent;

    public void addChild(FileindexEntry child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(FileindexEntry child) {
        children.remove(child);
        child.setParent(null);
    }

    public boolean hasParent() {
        return parent != null;
    }

    public String getFileindexTreePath() {
        if (hasParent()) {
            return parent.getFileindexTreePath() + "/" + name;
        } else {
            return name;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getFileindexTreePath());

        return sb.toString();
    }
}