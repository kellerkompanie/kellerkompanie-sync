package com.kellerkompanie.kekosync.core.entities;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FileindexEntry implements Serializable {
    @Getter @Setter private String name;
    @Getter @Setter private long size;
    @Getter @Setter private boolean directory;
    private List<FileindexEntry> children = new ArrayList<>();
    public List<FileindexEntry> getChildren() { return Collections.unmodifiableList(children); }

    public void addChild(FileindexEntry child) { children.add(child); }

    public void removeChild(FileindexEntry child) {
        children.remove(child);
    }
}