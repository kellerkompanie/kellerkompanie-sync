package com.kellerkompanie.kekosync.server.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UUIDGenerator {
    public static UUID generateUUID() {
        return UUID.randomUUID();
    }
}
