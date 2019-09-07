package com.kellerkompanie.kekosync.core.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Schwaggot
 */
@EqualsAndHashCode
@ToString
public class ServerInfo {

    @Getter
    private String baseURL;
    @Getter
    private String infoURL;

    public ServerInfo(String baseURL, String infoURL) {
        this.baseURL = baseURL;
        this.infoURL = infoURL;
    }

}
