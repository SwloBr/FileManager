package com.swlo.filemanager;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa um diretório no sistema de arquivos.
 * Cada diretório possui um nome, um diretório pai, subdiretórios, arquivos, dono e permissões.
 */
public class Directory {
    // Nome do diretório
    private String name;
    // Diretório pai (null se for a raiz)
    private Directory parent;
    // Mapa dos subdiretórios (chave: nome do diretório, valor: objeto Directory)
    private Map<String, Directory> subdirs;
    // Mapa dos arquivos contidos no diretório (chave: nome do arquivo, valor: objeto File)
    private Map<String, File> files;
    // Dono do diretório
    private String owner;
    // Permissões do diretório (pode ser um string representando as permissões)
    private String permissions;

    /**
     * Construtor para criação de um diretório.
     *
     * @param name   Nome do diretório.
     * @param parent Diretório pai.
     */
    public Directory(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
        this.subdirs = new HashMap<>();
        this.files = new HashMap<>();
    }

    /**
     * Retorna o nome do diretório.
     *
     * @return Nome do diretório.
     */
    public String getName() {
        return name;
    }

    /**
     * Verifica se o diretório está vazio (sem subdiretórios e arquivos).
     *
     * @return true se estiver vazio; caso contrário, false.
     */
    public boolean isEmpty() {
        return subdirs.isEmpty() && files.isEmpty();
    }

    /**
     * Retorna o diretório pai.
     *
     * @return Diretório pai.
     */
    public Directory getParent() {
        return parent;
    }

    /**
     * Retorna o mapa de subdiretórios.
     *
     * @return Mapa contendo os subdiretórios.
     */
    public Map<String, Directory> getSubdirs() {
        return subdirs;
    }

    /**
     * Retorna o mapa de arquivos.
     *
     * @return Mapa contendo os arquivos.
     */
    public Map<String, File> getFiles() {
        return files;
    }

    /**
     * Adiciona um subdiretório ao diretório atual.
     *
     * @param dir Objeto Directory que representa o novo subdiretório.
     */
    public void addSubdir(Directory dir) {
        subdirs.put(dir.getName(), dir);
    }

    /**
     * Adiciona um arquivo ao diretório atual.
     *
     * @param file Objeto File que representa o novo arquivo.
     */
    public void addFile(File file) {
        files.put(file.getName(), file);
    }

    /**
     * Define o dono do diretório.
     *
     * @param owner Nome do dono.
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Define as permissões do diretório.
     *
     * @param permissions String representando as permissões.
     */
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
