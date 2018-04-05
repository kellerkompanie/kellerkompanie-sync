package com.kellerkompanie.kekosync.client.arma;

import com.kellerkompanie.kekosync.client.settings.Settings;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Schwaggot
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArmALauncher {
    private static ArmALauncher instance;

    public static ArmALauncher getInstance() {
        if (instance == null)
            instance = new ArmALauncher();
        return instance;
    }

    public void startArmA() {
        List<String> commandLineArguments = new LinkedList<>();
        String executableLocation = Settings.getInstance().getExecutableLocation();
        commandLineArguments.add(executableLocation);

        Map<String, ArmAParameter> params = Settings.getInstance().getLaunchParams();

        for (ArmAParameter param : params.values()) {
            if (param.isEnabled())
                commandLineArguments.add(param.getArgument());
        }

        ProcessBuilder p = new ProcessBuilder();
        p.command(commandLineArguments);
        try {
            p.start();
        } catch (IOException e) {
            log.error("something went wrong while starting ArmA", e);
        }
    }
}
