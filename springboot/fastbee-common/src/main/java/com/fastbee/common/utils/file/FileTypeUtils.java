//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.file;

import java.io.File;

public class FileTypeUtils {
    public static String getFileType(File file) {
        return null == file ? "" : getFileType(file.getName());
    }

    public static String getFileType(String fileName) {
        int var1 = fileName.lastIndexOf(".");
        return var1 < 0 ? "" : fileName.substring(var1 + 1).toLowerCase();
    }

    public static String getFileExtendName(byte[] photoByte) {
        String var1 = "JPG";
        if (photoByte[0] == 71 && photoByte[1] == 73 && photoByte[2] == 70 && photoByte[3] == 56 && (photoByte[4] == 55 || photoByte[4] == 57) && photoByte[5] == 97) {
            var1 = "GIF";
        } else if (photoByte[6] == 74 && photoByte[7] == 70 && photoByte[8] == 73 && photoByte[9] == 70) {
            var1 = "JPG";
        } else if (photoByte[0] == 66 && photoByte[1] == 77) {
            var1 = "BMP";
        } else if (photoByte[1] == 80 && photoByte[2] == 78 && photoByte[3] == 71) {
            var1 = "PNG";
        }

        return var1;
    }
}
