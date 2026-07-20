//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.common.config.RuoYiConfig;
import com.fastbee.common.exception.file.FileNameLengthLimitExceededException;
import com.fastbee.common.exception.file.FileSizeLimitExceededException;
import com.fastbee.common.exception.file.InvalidExtensionException;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.uuid.Seq;

public class FileUploadUtils {
    public static final long DEFAULT_MAX_SIZE = 52428800L;
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;
    private static String aW = RuoYiConfig.getProfile();

    public static void setDefaultBaseDir(String defaultBaseDir) {
        aW = defaultBaseDir;
    }

    public static String getDefaultBaseDir() {
        return aW;
    }

    public static final String upload(MultipartFile file) throws IOException {
        try {
            return upload(getDefaultBaseDir(), file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception var2) {
            throw new IOException(var2.getMessage(), var2);
        }
    }

    public static final String upload(String baseDir, MultipartFile file) throws IOException {
        try {
            return upload(baseDir, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception var3) {
            throw new IOException(var3.getMessage(), var3);
        }
    }

    public static final String upload(String baseDir, MultipartFile file, String[] allowedExtension) throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException, InvalidExtensionException {
        int var3 = ((String)Objects.requireNonNull(file.getOriginalFilename())).length();
        if (var3 > 100) {
            throw new FileNameLengthLimitExceededException(100);
        } else {
            assertAllowed(file, allowedExtension);
            String var4 = extractFilename(file);
            String var5 = getAbsoluteFile(baseDir, var4).getAbsolutePath();
            file.transferTo(Paths.get(var5));
            return getPathFileName(baseDir, var4);
        }
    }

    public static final String extractFilename(MultipartFile file) {
        return StringUtils.format("{}/{}_{}.{}", new Object[]{DateUtils.datePath(), FilenameUtils.getBaseName(file.getOriginalFilename()), Seq.getId("UPLOAD"), getExtension(file)});
    }

    public static final File getAbsoluteFile(String uploadDir, String fileName) throws IOException {
        File var2 = new File(uploadDir + File.separator + fileName);
        if (!var2.exists() && !var2.getParentFile().exists()) {
            var2.getParentFile().mkdirs();
        }

        return var2;
    }

    public static final String getPathFileName(String uploadDir, String fileName) throws IOException {
        int var2 = RuoYiConfig.getProfile().length() + 1;
        String var3 = StringUtils.substring(uploadDir, var2);
        return "/profile/" + var3 + "/" + fileName;
    }

    public static final void assertAllowed(MultipartFile file, String[] allowedExtension) throws FileSizeLimitExceededException, InvalidExtensionException {
        long var2 = file.getSize();
        if (var2 > 52428800L) {
            throw new FileSizeLimitExceededException(50L);
        } else {
            String var4 = file.getOriginalFilename();
            String var5 = getExtension(file);
            if (allowedExtension != null && !isAllowedExtension(var5, allowedExtension)) {
                if (allowedExtension == MimeTypeUtils.IMAGE_EXTENSION) {
                    throw new InvalidExtensionException.InvalidImageExtensionException(allowedExtension, var5, var4);
                } else if (allowedExtension == MimeTypeUtils.FLASH_EXTENSION) {
                    throw new InvalidExtensionException.InvalidFlashExtensionException(allowedExtension, var5, var4);
                } else if (allowedExtension == MimeTypeUtils.MEDIA_EXTENSION) {
                    throw new InvalidExtensionException.InvalidMediaExtensionException(allowedExtension, var5, var4);
                } else if (allowedExtension == MimeTypeUtils.VIDEO_EXTENSION) {
                    throw new InvalidExtensionException.InvalidVideoExtensionException(allowedExtension, var5, var4);
                } else {
                    throw new InvalidExtensionException(allowedExtension, var5, var4);
                }
            }
        }
    }

    public static final boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for(String var5 : allowedExtension) {
            if (var5.equalsIgnoreCase(extension)) {
                return true;
            }
        }

        return false;
    }

    public static final String getExtension(MultipartFile file) {
        String var1 = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(var1)) {
            var1 = MimeTypeUtils.getExtension((String)Objects.requireNonNull(file.getContentType()));
        }

        return var1;
    }
}
