package com.kellerkompanie.kekosync.client.arma;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Schwaggot
 */
public class ArmALauncher {

    private String executableLocation;

    private ArmALauncher() {
    }

    public ArmALauncher(String executableLocation) {
        this.executableLocation = executableLocation;
    }

    public void startArmA(List<ArmAParameter> params) throws IOException {
        List<String> commandLineArguments = new LinkedList<>();
        commandLineArguments.add(executableLocation);

        for (ArmAParameter param : params) {
            commandLineArguments.add(param.getArgument());
        }

        ProcessBuilder p = new ProcessBuilder();
        p.command(commandLineArguments);
        p.start();
    }

}
