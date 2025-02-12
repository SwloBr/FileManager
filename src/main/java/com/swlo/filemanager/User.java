package com.swlo.filemanager;

/**
 * Representa um usuário do sistema de arquivos.
 * Cada usuário possui um nome (username) e um papel (role).
 */
public class User {
    // Nome do usuário
    private String username;
    // Papel do usuário (ex.: admin, usuário comum)
    private String role;

    /**
     * Construtor para criação de um usuário.
     *
     * @param username Nome do usuário.
     * @param role     Papel ou função do usuário.
     */
    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    /**
     * Retorna o nome do usuário.
     *
     * @return Nome do usuário.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Retorna o papel do usuário.
     *
     * @return Papel do usuário.
     */
    public String getRole() {
        return role;
    }
}
