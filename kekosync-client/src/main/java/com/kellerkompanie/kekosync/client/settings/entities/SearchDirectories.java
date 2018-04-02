package com.kellerkompanie.kekosync.client.settings.entities;

import lombok.*;

import java.nio.file.Path;
import java.util.HashSet;

/**
 * @author Schwaggot
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@EqualsAndHashCode
public class SearchDirectories {
    @Getter
    @Setter
    private HashSet<Path> directories;
}
