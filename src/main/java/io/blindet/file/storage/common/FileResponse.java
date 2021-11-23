package io.blindet.file.storage.common;

public class FileResponse {
    private String name;
    private String uri;
    private String type;
    private long size;

    public FileResponse(String name, String uri, String type, long size) {
        this.name = name;
        this.uri = uri;
        this.type = type;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public String getType() {
        return type;
    }

    public long getSize() {
        return size;
    }
}
