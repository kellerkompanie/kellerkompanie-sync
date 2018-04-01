package com.kellerkompanie.kekosync.server;

import java.io.IOException;

public class KekoSyncServer {

    public static void main(String[] args) throws IOException {
        String directory = "E:\\kekosync-demo-repository";
        ZsyncGenerator zg = new ZsyncGenerator();
        zg.cleanDirectory(directory);
        zg.processDirectory(directory);
    }



}
