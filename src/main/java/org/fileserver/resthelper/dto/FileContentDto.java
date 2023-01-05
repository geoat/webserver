package org.fileserver.resthelper.dto;

public class FileContentDto {

    private final String name;
    private final String path;

    private final Type type;

    public FileContentDto(String name, String path, Type type) {
        this.name = name;
        this.path = path;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public static enum Type {
        FOLDER,
        FILE
    }
}
