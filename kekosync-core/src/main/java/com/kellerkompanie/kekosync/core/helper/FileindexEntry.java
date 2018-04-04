package com.kellerkompanie.kekosync.core.helper;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FileindexEntry implements Serializable {
    @Getter @Setter private String name;
    @Getter @Setter private long size;
    @Getter @Setter private boolean directory;
    @Getter @Setter private String UUID; //this property is filled with the content of the .id-file in case this is a primary mod-folder
    @Getter @Setter private String hash;
    @Getter private List<FileindexEntry> children = new ArrayList<>();

    public void addChild(FileindexEntry child) { children.add(child); }

    public void removeChild(FileindexEntry child) {
        children.remove(child);
    }
}