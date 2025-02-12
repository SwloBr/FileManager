package com.swlo.filemanager;

/**
 * Representa um arquivo no sistema de arquivos.
 * Cada arquivo possui nome, tamanho, conteúdo, dono e permissões.
 */
public class File {
    // Nome do arquivo
    private String name;
    // Tamanho do arquivo (em KB, por exemplo)
    private int size;
    // Conteúdo armazenado no arquivo
    private String content;
    // Dono do arquivo
    private String owner;
    // Permissões do arquivo
    private String permissions;

    /**
     * Construtor para criação de um arquivo.
     *
     * @param name    Nome do arquivo.
     * @param size    Tamanho inicial do arquivo.
     * @param content Conteúdo inicial do arquivo.
     */
    public File(String name, int size, String content) {
        this.name = name;
        this.size = size;
        this.content = content;
    }

    /**
     * Retorna o nome do arquivo.
     *
     * @return Nome do arquivo.
     */
    public String getName() {
        return name;
    }

    /**
     * Retorna o tamanho do arquivo.
     *
     * @return Tamanho do arquivo.
     */
    public int getSize() {
        return size;
    }

    /**
     * Retorna o conteúdo do arquivo.
     *
     * @return Conteúdo do arquivo.
     */
    public String getContent() {
        return content;
    }

    /**
     * Define um novo tamanho para o arquivo.
     *
     * @param size Novo tamanho do arquivo.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Atualiza o conteúdo do arquivo.
     *
     * @param content Novo conteúdo do arquivo.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Define o dono do arquivo.
     *
     * @param owner Nome do dono.
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Define as permissões do arquivo.
     *
     * @param permissions String representando as permissões.
     */
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
