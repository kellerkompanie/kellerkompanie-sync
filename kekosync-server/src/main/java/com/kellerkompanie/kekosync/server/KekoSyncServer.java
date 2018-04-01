package com.kellerkompanie.kekosync.server;

import com.kellerkompanie.kekosync.server.cli.CommandLineProcessor;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

/**
 * @author Schwaggot
 */
public class KekoSyncServer {

    public static void main(String[] args) throws IOException, ParseException {
        if (args.length > 1) {
            CommandLineProcessor commandLineProcessor = new CommandLineProcessor();
            commandLineProcessor.process(args);
        } else {
            String directory = "E:\\kekosync-demo-repository";
            ZsyncGenerator zg = new ZsyncGenerator();
            zg.cleanDirectory(directory);
            zg.processDirectory(directory);
        }
    }
}
