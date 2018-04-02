package com.kellerkompanie.kekosync.client.settings.entities;

import lombok.*;

/**
 * @author Schwaggot
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@EqualsAndHashCode
public class ExecutableLocation {
    @Getter
    @Setter
    private String path;
}
