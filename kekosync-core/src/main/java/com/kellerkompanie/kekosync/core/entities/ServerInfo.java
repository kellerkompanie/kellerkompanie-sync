package com.kellerkompanie.kekosync.core.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    @Getter
    private List<String> repositoryIdentifiers;

    public ServerInfo(String baseURL, String infoURL, Collection<String> repositoryIdentifiers) {
        this.baseURL = baseURL;
        this.infoURL = infoURL;
        this.repositoryIdentifiers = new ArrayList<>(repositoryIdentifiers);
    }

}
