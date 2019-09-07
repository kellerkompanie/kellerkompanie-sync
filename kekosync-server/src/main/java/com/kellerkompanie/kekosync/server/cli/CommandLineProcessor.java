package com.kellerkompanie.kekosync.server.cli;

import com.kellerkompanie.kekosync.server.KekoSyncServer;
import org.apache.commons.cli.*;

/**
 * @author Schwaggot
 */
public class CommandLineProcessor {

    private static final String DEFAULT_CONFIG_FILE = "kekosync_config.json";

    private static final String BUILD = "build";
    private static final String BUILD_ALL = "buildall";
    private static final String LIST = "list";
    private static final String HELP = "help";
    private static final String CONFIG = "config";

    public void process(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption(CONFIG, true, "specify settings .json filepath to be loaded");
        options.addOption(BUILD, true, "build addon source folder");
        options.addOption(BUILD_ALL, false, "build all addon source folders");
        options.addOption(LIST, false, "list addon source folders");
        options.addOption(HELP, false, "list available commands");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = parser.parse(options, args);

        String jsonFile = DEFAULT_CONFIG_FILE;
        if(cmd.hasOption(CONFIG)) {
            jsonFile = cmd.getOptionValue(CONFIG);
        }
        KekoSyncServer kekoSyncServer = new KekoSyncServer(jsonFile);

        if (cmd.hasOption(BUILD)) {
            String addonSourceFolder = cmd.getOptionValue(BUILD);
            kekoSyncServer.buildAddonSourceFolder(addonSourceFolder);
        } else if (cmd.hasOption(BUILD_ALL)) {
            kekoSyncServer.buildAllAddonSourceFolders();
        } else if (cmd.hasOption(LIST)) {
            kekoSyncServer.printAddonSourceFolders();
        } else if (cmd.hasOption(HELP)) {
            formatter.printHelp("utility-name", options);
            System.exit(0);
        } else {
            System.out.println("error: specify additional parameters");
            formatter.printHelp("utility-name", options);
            System.exit(0);
        }
    }

}
