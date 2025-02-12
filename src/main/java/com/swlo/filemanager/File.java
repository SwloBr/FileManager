package com.swlo.filemanager;

public class File {
    private String name;
    private int size;
    private String content;
    private String owner;
    private String permissions;

    public File(String name, int size, String content) {
        this.name = name;
        this.size = size;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public String getContent() {
        return content;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}