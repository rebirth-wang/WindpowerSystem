//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.web.multipart.MultipartFile;

public class Base64ToMultipartFile implements MultipartFile {
    private final byte[] al;
    private final String am;
    private final String an;

    public Base64ToMultipartFile(String base64, String dataUri) {
        this.al = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
        this.am = dataUri.split(";")[0].split("/")[1];
        this.an = dataUri.split(";")[0].split(":")[1];
    }

    public Base64ToMultipartFile(String base64, String extension, String contentType) {
        this.al = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
        this.am = extension;
        this.an = contentType;
    }

    public String getName() {
        return "param_" + System.currentTimeMillis();
    }

    public String getOriginalFilename() {
        return "file_" + System.currentTimeMillis() + "." + this.am;
    }

    public String getContentType() {
        return this.an;
    }

    public boolean isEmpty() {
        return this.al == null || this.al.length == 0;
    }

    public long getSize() {
        return (long)this.al.length;
    }

    public byte[] getBytes() throws IOException {
        return this.al;
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.al);
    }

    public void transferTo(File file) throws IOException, IllegalStateException {
        try (FileOutputStream var2 = new FileOutputStream(file)) {
            var2.write(this.al);
        }

    }
}
