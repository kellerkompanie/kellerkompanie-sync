package com.kellerkompanie.kekosync.core.helper;

import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class HttpHelperEntry {
    @Getter @Setter private String name;
    @Getter @Setter private boolean directory;
    private List<HttpHelperEntry> children = new ArrayList<>();
    public List<HttpHelperEntry> getChildren() { return Collections.unmodifiableList(children); }

    public void addChild(HttpHelperEntry child) { children.add(child); }

    public void removeChild(HttpHelperEntry child) {
        children.remove(child);
    }
}