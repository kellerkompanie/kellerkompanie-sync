package com.kellerkompanie.kekosync.server.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class ServerConfig {

    @Getter
    private final String baseURL;
    @Getter
    private final String infoURL;
    @Getter
    private List<ServerRepository> repositories;

    public ServerConfig(String baseURL, String infoURL) {
        this.baseURL = baseURL;
        this.infoURL = infoURL;
        this.repositories = new LinkedList<>();
    }

    public static ServerConfig getDefaultConfig() {
        String baseURL = "http://server.kellerkompanie.com/repository/";
        String infoURL = "http://server.kellerkompanie.com/modpack_info.json";
        ServerConfig defaultConfig = new ServerConfig(baseURL, infoURL);

        ServerRepository minimalRepository = new ServerRepository("kellerkompanie-minimal", "Kellerkompanie Minimal", "/home/arma3server/serverfiles/mods.minimal", baseURL + "minimal");
        ServerRepository mainRepository = new ServerRepository("kellerkompanie-main", "Kellerkompanie Main", "/home/arma3server/serverfiles/mods.main", baseURL + "main");
        ServerRepository eventRepository = new ServerRepository("kellerkompanie-event", "Kellerkompanie Event", "/home/arma3server/serverfiles/mods.event", baseURL + "event");
        ServerRepository mapsRepository = new ServerRepository("kellerkompanie-maps", "Kellerkompanie Maps", "/home/arma3server/serverfiles/mods.maps", baseURL + "maps");
        ServerRepository optionalRepository = new ServerRepository("kellerkompanie-optional", "Kellerkompanie Optional", "/home/arma3server/serverfiles/mods.optional", baseURL + "optional");
        ServerRepository vietnamRepository = new ServerRepository("kellerkompanie-vietnam", "Kellerkompanie Vietnam", "/home/arma3server/serverfiles/mods.vietnam", baseURL + "vietnam");
        ServerRepository ironfrontRepository = new ServerRepository("kellerkompanie-ironfront", "Kellerkompanie Ironfront", "/home/arma3server/serverfiles/mods.ironfront", baseURL + "ironfront");

        defaultConfig.addRepository(minimalRepository);
        defaultConfig.addRepository(mainRepository);
        defaultConfig.addRepository(eventRepository);
        defaultConfig.addRepository(mapsRepository);
        defaultConfig.addRepository(optionalRepository);
        defaultConfig.addRepository(vietnamRepository);
        defaultConfig.addRepository(ironfrontRepository);

        return defaultConfig;
    }

    public void addRepository(ServerRepository repository) {
        repositories.add(repository);
    }

}
