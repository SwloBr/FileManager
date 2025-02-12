package com.swlo.filemanager;

public class Storage {
    private boolean[] bitmap;
    private int totalBlocks;
    private int freeBlocks;

    public Storage(int totalKB, int blockSizeKB) {
        totalBlocks = totalKB / blockSizeKB;
        freeBlocks = totalBlocks;
        bitmap = new boolean[totalBlocks];
    }

    public int allocate(int blocks) throws Exception {
        int start = findContiguousBlocks(blocks);
        if (start == -1) throw new Exception("Espaço insuficiente");
        for (int i = start; i < start + blocks; i++) {
            bitmap[i] = true;
        }
        freeBlocks -= blocks;
        return start;
    }

    private int findContiguousBlocks(int blocks) {
        int count = 0;
        for (int i = 0; i < totalBlocks; i++) {
            if (!bitmap[i]) {
                if (++count == blocks) return i - blocks + 1;
            } else {
                count = 0;
            }
        }
        return -1;
    }

    public void free(int start, int blocks) {
        for (int i = start; i < start + blocks; i++) {
            bitmap[i] = false;
        }
        freeBlocks += blocks;
    }
    public void deallocate(int blocks) {
        freeBlocks += blocks;
    }

    public int getFreeBlocks() {
        return freeBlocks;
    }
}