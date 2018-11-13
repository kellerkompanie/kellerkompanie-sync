package com.kellerkompanie.kekosync.core.helper;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

@Slf4j
public class HttpHelper {
    public static String readUrl(String urlString) throws Exception {
        URL url = new URL(urlString);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        }
    }

    static void downloadFile(String urlString, Path path, long size) {
        URL website = null;
        try {
            website = new URL(urlString);
        } catch (MalformedURLException e) {
            log.error("{} is not a well formed URL", urlString, e);
        }
        ReadableByteChannel rbc;
        try {
            assert website != null;
            rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(path.toFile());
            fos.getChannel().transferFrom(rbc, 0, size);
        } catch (IOException e) {
            log.error("error while downloading {}", urlString, e);
        }
    }
}
