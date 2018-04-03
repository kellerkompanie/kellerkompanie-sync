package com.kellerkompanie.kekosync.server;

import com.kellerkompanie.kekosync.server.cli.CommandLineProcessor;
import com.kellerkompanie.kekosync.server.helper.ZsyncGenerator;
import com.kellerkompanie.kekosync.server.tasks.RebuildRepositoryTask;
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
            /*
            String directory = "E:\\kekosync-demo-repository";
            ZsyncGenerator.cleanDirectory(directory);
            ZsyncGenerator.processDirectory(directory);
            */
            RebuildRepositoryTask rrTask = new RebuildRepositoryTask("E:\\kekosync-demo-repository");
            rrTask.execute();
        }
    }
}
