package com.onlym.file.storage.common;

public class FileResponse {
    private String name;
    private String downloadUri;
    private String type;
    private long size;

    public FileResponse(String name, String downloadUri, String type, long size) {
        this.name = name;
        this.downloadUri = downloadUri;
        this.type = type;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public String getType() {
        return type;
    }

    public long getSize() {
        return size;
    }
}
