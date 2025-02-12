package com.swlo.filemanager;

import java.util.HashMap;
import java.util.Map;

public class FileSystem {
    private Directory root;
    private Directory currentDir;
    private Map<String, User> users;
    private User currentUser;
    private Storage storage;
    public static final int TOTAL_SPACE = 100 * 1024; // 100MB em KB
    public static final int BLOCK_SIZE = 1; // 1KB por bloco

    public FileSystem() {

        root = new Directory("root", null);
        currentDir = root;
        users = new HashMap<>();
        users.put("admin", new User("admin", "admin"));
        currentUser = users.get("admin");
        storage = new Storage(TOTAL_SPACE, BLOCK_SIZE); // Example storage size
    }

    public void addUser(String username, String role) {
        users.put(username, new User(username, role));
    }

    public void setCurrentUser(String username) {
        User user = users.get(username);
        if (user != null) {
            currentUser = user;
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Storage getStorage() {
        return storage;
    }

    public Directory getRoot() {
        return root;
    }

    public Directory getCurrentDir() {
        return currentDir;
    }

    public void changeDirectory(Directory dir) {
        currentDir = dir;
    }

    public void createDirectory(String name, String owner, String permissions) {
        Directory newDir = new Directory(name, currentDir);
        newDir.setOwner(owner);
        newDir.setPermissions(permissions);
        currentDir.addSubdir(newDir);
    }

    public void createFile(String name, int size, String content, String owner, String permissions) throws Exception {
        File newFile = new File(name, size, content);
        newFile.setOwner(owner);
        newFile.setPermissions(permissions);
        currentDir.addFile(newFile);
        storage.allocate(size);
    }

    public String readFile(String name) {
        File file = currentDir.getFiles().get(name);
        if (file != null) {
            return file.getContent();
        }
        throw new IllegalArgumentException("File not found");
    }

    public void writeFile(String name, String content) throws Exception {
        File file = currentDir.getFiles().get(name);
        if (file != null) {
            int newSize = (content.getBytes().length / 1024) + 1;
            storage.deallocate(file.getSize());
            file.setContent(content);
            file.setSize(newSize);
            storage.allocate(newSize);
        } else {
            throw new IllegalArgumentException("File not found");
        }
    }

    public void deleteFile(String name) {
        File file = currentDir.getFiles().remove(name);
        if (file != null) {
            storage.deallocate(file.getSize());
        } else {
            throw new IllegalArgumentException("File not found");
        }
    }

    public void deleteDirectory(Directory dir) throws Exception {
        if (dir == null) throw new Exception("Diretório não existe");
        if (!dir.isEmpty()) throw new Exception("Diretório não está vazio");

        // Remove do diretório pai
        Directory parent = dir.getParent();
        if (parent != null) {
            parent.getSubdirs().remove(dir.getName());
        }
    }

    public void changePermissions(File file, String permissions) {
        file.setPermissions(permissions);
    }

    public void changePermissions(Directory dir, String permissions) {
        dir.setPermissions(permissions);
    }

    public boolean isAdmin() {
        return currentUser.getRole().equals("admin");
    }
}