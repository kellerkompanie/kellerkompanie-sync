package com.kellerkompanie.kekosync.server.tasks;

import com.google.gson.Gson;
import com.kellerkompanie.kekosync.core.entities.RunningModset;
import com.kellerkompanie.kekosync.core.entities.Mod;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

@AllArgsConstructor
@EqualsAndHashCode
@Slf4j
public class CreateDemoCurrentlyRunningModsetTask {
    public boolean execute(Path outputFilename) {
        Mod modAce = new Mod("@ace", UUID.fromString("4b7f8899-33d3-45f0-9d35-58e8fe3c26ae"));
        Mod modAceX = new Mod("@acex", UUID.fromString("ce021fb4-90ce-431a-a32a-ac801e1fa47b"));
        Mod modCba = new Mod("@CBA_A3", UUID.fromString("df741f57-3e1f-42e7-a962-8c8bd16732a5"));
        Mod modGradTrenches = new Mod("@GRAD_Trenches", UUID.fromString("3df459ad-104d-488d-a67b-e5a2a6333fe3"));
        RunningModset gcrm = new RunningModset("dynamic", "server.kellerkompanie.com", "2302",
                Arrays.asList(modAce,modCba,modAceX, modGradTrenches));

        String Json = new Gson().toJson(gcrm);
        try {
            Files.write(outputFilename, Json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Could not write modgroup-file.", e);
            return false;
        }
        return true;
    }
}
