package com.kellerkompanie.kekosync.server.cli;

import org.apache.commons.cli.*;

/**
 * @author Schwaggot
 */
public class CommandLineProcessor {

    private static final String CONSOLE = "console";
    private static final String BUILD = "build";
    private static final String BUILD_ALL = "build-all";
    private static final String LIST = "list";
    private static final String HELP = "help";

    public void process(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption(CONSOLE, false, "interactive mode");
        options.addOption(BUILD, true, "build repository");
        options.addOption(BUILD_ALL, true, "build all repositories");
        options.addOption(LIST, false, "list repositories");
        options.addOption(HELP, false, "list available commands");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption(CONSOLE)) {

        } else if (cmd.hasOption(BUILD)) {
            String repositoryName = cmd.getOptionValue(BUILD);
        } else if (cmd.hasOption(BUILD_ALL)) {

        } else if (cmd.hasOption(LIST)) {

        } else if (cmd.hasOption(HELP)) {

        }
    }

}
