package com.swlo.filemanager;

/**
 * Simula o gerenciamento do armazenamento utilizando um bitmap para controle de blocos.
 * Implementa a alocação de blocos de forma contígua.
 */
public class Storage {
    // Array booleano que representa o bitmap dos blocos (true = ocupado, false = livre)
    private boolean[] bitmap;
    // Número total de blocos disponíveis
    private int totalBlocks;
    // Número de blocos livres atualmente
    private int freeBlocks;

    /**
     * Construtor para inicializar o armazenamento.
     *
     * @param totalKB     Espaço total em KB.
     * @param blockSizeKB Tamanho de cada bloco em KB.
     */
    public Storage(int totalKB, int blockSizeKB) {
        totalBlocks = totalKB / blockSizeKB;
        freeBlocks = totalBlocks;
        bitmap = new boolean[totalBlocks];
    }

    /**
     * Aloca uma quantidade especificada de blocos de forma contígua.
     *
     * @param blocks Número de blocos a serem alocados.
     * @return Índice do primeiro bloco alocado.
     * @throws Exception se não houver blocos contíguos suficientes.
     */
    public int allocate(int blocks) throws Exception {
        int start = findContiguousBlocks(blocks);
        if (start == -1) throw new Exception("Espaço insuficiente");
        // Marca os blocos como ocupados
        for (int i = start; i < start + blocks; i++) {
            bitmap[i] = true;
        }
        freeBlocks -= blocks;
        return start;
    }

    /**
     * Procura uma sequência de blocos contíguos livres que atenda a quantidade requerida.
     *
     * @param blocks Número de blocos contíguos necessários.
     * @return Índice do primeiro bloco da sequência encontrada ou -1 se não encontrado.
     */
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

    /**
     * Libera (marca como livre) os blocos a partir de um índice inicial.
     *
     * @param start  Índice do primeiro bloco a ser liberado.
     * @param blocks Número de blocos a serem liberados.
     */
    public void free(int start, int blocks) {
        for (int i = start; i < start + blocks; i++) {
            bitmap[i] = false;
        }
        freeBlocks += blocks;
    }

    /**
     * Libera uma quantidade de blocos (utilizado quando se exclui ou altera um arquivo).
     *
     * @param blocks Número de blocos a liberar.
     */
    public void deallocate(int blocks) {
        freeBlocks += blocks;
    }

    /**
     * Retorna a quantidade de blocos livres disponíveis.
     *
     * @return Número de blocos livres.
     */
    public int getFreeBlocks() {
        return freeBlocks;
    }
}
