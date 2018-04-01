package com.kellerkompanie.kekosync.server.constants;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;

public final class FileMatcher {
    //TODO: See if Filenames.FILENAME_MODID needs to be added to sourceFileMatcher in order to be synced. -- dth
    public final static PathMatcher sourceFileMatcher = FileSystems.getDefault().getPathMatcher("glob:*.{pbo,bisign,bikey,cpp,paa,dll}");
    public final static PathMatcher zsyncFileMatcher = FileSystems.getDefault().getPathMatcher("glob:*.{zsync}");
}
