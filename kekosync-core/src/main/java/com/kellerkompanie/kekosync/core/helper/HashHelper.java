package com.kellerkompanie.kekosync.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class HashHelper {
    public static byte[] generateSHA512(Path file) {
        MessageDigest md;
        byte[] hash = null;
        try {
            md = MessageDigest.getInstance("SHA-512");

            try (InputStream is = Files.newInputStream(file);
                 DigestInputStream dis = new DigestInputStream(is, md))
            {
                byte[] buffer = new byte[4096];
                while (dis.read(buffer) > -1) {}
            } catch (IOException e) {
                log.error("error while creating digest for file {}", file.getFileName(), e);
            }
            hash = md.digest();
        } catch (NoSuchAlgorithmException e) {
            log.error("Error while hashing", e);
        }
        return hash;
    }

    public static byte[] generateSHA512(String input) {
        MessageDigest md;
        byte[] hash = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
            hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            log.error("Error while hashing", e);
        }
        return hash;
    }

    public static String convertToHex(byte[] raw) {
        StringBuilder sb = new StringBuilder();
        for (byte aRaw : raw) {
            sb.append(Integer.toString((aRaw & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
