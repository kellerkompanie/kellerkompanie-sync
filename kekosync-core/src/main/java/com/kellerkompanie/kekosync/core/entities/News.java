package com.kellerkompanie.kekosync.core.entities;

import com.kellerkompanie.kekosync.core.constants.NewsType;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@EqualsAndHashCode(of = {"uuid"})
@ToString
public class News implements Serializable {

    private UUID uuid;
    @Getter private NewsType newsType;
    @Getter private String title;
    @Getter private String content;
    @Getter private String weblink;
    @Getter private long timestamp;

    public static News createDefaultNews() {
        return new News (UUID.randomUUID(), NewsType.NEWS, "Launcher Online", "Our Launcher is now available for everyone", "https://server.kellerkompanie.com", System.currentTimeMillis());
    }
}
