//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.exception.file;

import java.util.Arrays;

import org.apache.commons.fileupload.FileUploadException;

public class InvalidExtensionException extends FileUploadException {
    private static final long M = 1L;
    private String[] N;
    private String O;
    private String P;

    public InvalidExtensionException(String[] allowedExtension, String extension, String filename) {
        super("文件[" + filename + "]后缀[" + extension + "]不正确，请上传" + Arrays.toString(allowedExtension) + "格式");
        this.N = allowedExtension;
        this.O = extension;
        this.P = filename;
    }

    public String[] getAllowedExtension() {
        return this.N;
    }

    public String getExtension() {
        return this.O;
    }

    public String getFilename() {
        return this.P;
    }

    public static class InvalidImageExtensionException extends InvalidExtensionException {
        private static final long R = 1L;

        public InvalidImageExtensionException(String[] allowedExtension, String extension, String filename) {
            super(allowedExtension, extension, filename);
        }
    }

    public static class InvalidFlashExtensionException extends InvalidExtensionException {
        private static final long Q = 1L;

        public InvalidFlashExtensionException(String[] allowedExtension, String extension, String filename) {
            super(allowedExtension, extension, filename);
        }
    }

    public static class InvalidMediaExtensionException extends InvalidExtensionException {
        private static final long S = 1L;

        public InvalidMediaExtensionException(String[] allowedExtension, String extension, String filename) {
            super(allowedExtension, extension, filename);
        }
    }

    public static class InvalidVideoExtensionException extends InvalidExtensionException {
        private static final long T = 1L;

        public InvalidVideoExtensionException(String[] allowedExtension, String extension, String filename) {
            super(allowedExtension, extension, filename);
        }
    }
}
