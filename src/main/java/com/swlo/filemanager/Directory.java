package com.swlo.filemanager;

import java.util.HashMap;
import java.util.Map;

public class Directory {
    private String name;
    private Directory parent;
    private Map<String, Directory> subdirs;
    private Map<String, File> files;
    private String owner;
    private String permissions;

    public Directory(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
        this.subdirs = new HashMap<>();
        this.files = new HashMap<>();
    }

    public String getName() {
        return name;
    }


    public boolean isEmpty() {
        return subdirs.isEmpty() && files.isEmpty();
    }

    public Directory getParent() {
        return parent;
    }

    public Map<String, Directory> getSubdirs() {
        return subdirs;
    }

    public Map<String, File> getFiles() {
        return files;
    }

    public void addSubdir(Directory dir) {
        subdirs.put(dir.getName(), dir);
    }

    public void addFile(File file) {
        files.put(file.getName(), file);
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}