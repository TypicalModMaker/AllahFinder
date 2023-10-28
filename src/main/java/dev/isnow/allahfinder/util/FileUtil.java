package dev.isnow.allahfinder.util;

import dev.isnow.allahfinder.AllahFinderImpl;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@UtilityClass
public class FileUtil {
    public boolean copyResource(String res, File path) {
        final InputStream src = AllahFinderImpl.class.getResourceAsStream(res);
        if(src == null) {
            MessageUtil.error("FAILED TO COPY DEFAULT RESOURCE:", res);
            return false;
        }
        try {
            Files.copy(src, Paths.get(path.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            MessageUtil.error("FAILED TO COPY DEFAULT RESOURCE:", res);
        }
        return false;
    }
}
