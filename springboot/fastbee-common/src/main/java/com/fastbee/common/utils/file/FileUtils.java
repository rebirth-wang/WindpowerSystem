//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.file;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.fastbee.common.config.RuoYiConfig;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.uuid.IdUtils;

public class FileUtils {
    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream var2 = null;

        try {
            File var3 = new File(filePath);
            if (!var3.exists()) {
                throw new FileNotFoundException(filePath);
            }

            var2 = new FileInputStream(var3);
            byte[] var4 = new byte[1024];

            int var5;
            while((var5 = var2.read(var4)) > 0) {
                os.write(var4, 0, var5);
            }
        } catch (IOException var9) {
            throw var9;
        } finally {
            IOUtils.close(os);
            IOUtils.close(var2);
        }

    }

    public static String writeImportBytes(byte[] data) throws IOException {
        return writeBytes(data, RuoYiConfig.getImportPath());
    }

    public static String writeBytes(byte[] data, String uploadDir) throws IOException {
        FileOutputStream var2 = null;
        String var3 = "";

        try {
            String var4 = getFileExtendName(data);
            var3 = DateUtils.datePath() + "/" + IdUtils.fastUUID() + "." + var4;
            File var5 = FileUploadUtils.getAbsoluteFile(uploadDir, var3);
            var2 = new FileOutputStream(var5);
            var2.write(data);
        } finally {
            IOUtils.close(var2);
        }

        return FileUploadUtils.getPathFileName(uploadDir, var3);
    }

    public static boolean deleteFile(String filePath) {
        boolean var1 = false;
        File var2 = new File(filePath);
        if (var2.isFile() && var2.exists()) {
            var1 = var2.delete();
        }

        return var1;
    }

    public static boolean isValidFilename(String filename) {
        return filename.matches(FILENAME_PATTERN);
    }

    public static boolean checkAllowDownload(String resource) {
        if (StringUtils.contains(resource, "..")) {
            return false;
        } else {
            return ArrayUtils.contains(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, FileTypeUtils.getFileType(resource));
        }
    }

    public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        String var2 = request.getHeader("USER-AGENT");
        String var4;
        if (var2.contains("MSIE")) {
            var4 = URLEncoder.encode(fileName, "utf-8");
            var4 = var4.replace("+", " ");
        } else if (var2.contains("Firefox")) {
            var4 = new String(fileName.getBytes(), "ISO8859-1");
        } else if (var2.contains("Chrome")) {
            var4 = URLEncoder.encode(fileName, "utf-8");
        } else {
            var4 = URLEncoder.encode(fileName, "utf-8");
        }

        return var4;
    }

    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException {
        String var2 = percentEncode(realFileName);
        StringBuilder var3 = new StringBuilder();
        var3.append("attachment; filename=").append(var2).append(";").append("filename*=").append("utf-8''").append(var2);
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
        response.setHeader("Content-disposition", var3.toString());
        response.setHeader("download-filename", var2);
    }

    public static String percentEncode(String s) throws UnsupportedEncodingException {
        String var1 = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        return var1.replaceAll("\\+", "%20");
    }

    public static String getFileExtendName(byte[] photoByte) {
        String var1 = "jpg";
        if (photoByte[0] == 71 && photoByte[1] == 73 && photoByte[2] == 70 && photoByte[3] == 56 && (photoByte[4] == 55 || photoByte[4] == 57) && photoByte[5] == 97) {
            var1 = "gif";
        } else if (photoByte[6] == 74 && photoByte[7] == 70 && photoByte[8] == 73 && photoByte[9] == 70) {
            var1 = "jpg";
        } else if (photoByte[0] == 66 && photoByte[1] == 77) {
            var1 = "bmp";
        } else if (photoByte[1] == 80 && photoByte[2] == 78 && photoByte[3] == 71) {
            var1 = "png";
        }

        return var1;
    }

    public static String getName(String fileName) {
        if (fileName == null) {
            return null;
        } else {
            int var1 = fileName.lastIndexOf(47);
            int var2 = fileName.lastIndexOf(92);
            int var3 = Math.max(var1, var2);
            return fileName.substring(var3 + 1);
        }
    }

    public static String getNameNotSuffix(String fileName) {
        if (fileName == null) {
            return null;
        } else {
            String var1 = FilenameUtils.getBaseName(fileName);
            return var1;
        }
    }

    public static File createTempFile(String data) throws IOException {
            File var1 = createTempFile();
            FileUtil.writeUtf8String(data, var1);
            return var1;

    }

    public static File createTempFile(byte[] data) throws IOException {
            File var1 = createTempFile();
            FileUtil.writeBytes(data, var1);
            return var1;

    }

    public static File createTempFile() throws IOException {
            File var0 = File.createTempFile(IdUtil.simpleUUID(), (String)null);
            var0.deleteOnExit();
            return var0;

    }
}
