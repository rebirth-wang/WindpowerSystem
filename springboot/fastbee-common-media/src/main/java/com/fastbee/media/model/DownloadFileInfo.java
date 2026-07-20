package com.fastbee.media.model;

import lombok.Data;

@Data
public class DownloadFileInfo {
    private String httpPath;
    private String httpsPath;
    private String httpDomainPath;
    private String httpsDomainPath;
}
