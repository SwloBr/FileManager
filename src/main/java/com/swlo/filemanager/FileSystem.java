package com.swlo.filemanager;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa o sistema de arquivos simulado.
 * Essa classe gerencia as operações de criação, leitura, escrita e exclusão
 * de arquivos e diretórios, bem como a alocação de espaço no armazenamento.
 */
public class FileSystem {
    // Diretório raiz do sistema
    private Directory root;
    // Diretório atual onde as operações estão sendo realizadas
    private Directory currentDir;
    // Mapa de usuários registrados no sistema (chave: username)
    private Map<String, User> users;
    // Usuário atualmente logado
    private User currentUser;
    // Gerenciador de armazenamento (simula o espaço em disco)
    private Storage storage;

    // Espaço total em KB (100MB convertidos para KB)
    public static final int TOTAL_SPACE = 100 * 1024;
    // Tamanho de cada bloco em KB
    public static final int BLOCK_SIZE = 1;

    /**
     * Construtor do sistema de arquivos.
     * Inicializa o diretório raiz, usuário padrão (admin) e o armazenamento.
     */
    public FileSystem() {
        root = new Directory("root", null);
        currentDir = root;
        users = new HashMap<>();
        // Cria usuário admin com papel "admin"
        users.put("admin", new User("admin", "admin"));
        currentUser = users.get("admin");
        // Inicializa o armazenamento com espaço total e tamanho de bloco definido
        storage = new Storage(TOTAL_SPACE, BLOCK_SIZE);
    }

    /**
     * Adiciona um novo usuário ao sistema.
     *
     * @param username Nome do usuário.
     * @param role     Papel ou função do usuário (ex.: admin, usuário comum).
     */
    public void addUser(String username, String role) {
        users.put(username, new User(username, role));
    }

    /**
     * Define o usuário atual (login) do sistema.
     *
     * @param username Nome do usuário a ser definido como atual.
     * @throws IllegalArgumentException se o usuário não for encontrado.
     */
    public void setCurrentUser(String username) {
        User user = users.get(username);
        if (user != null) {
            currentUser = user;
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    /**
     * Retorna o usuário atualmente logado.
     *
     * @return Usuário atual.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Retorna o objeto de armazenamento do sistema.
     *
     * @return Objeto Storage.
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * Retorna o diretório raiz.
     *
     * @return Diretório raiz.
     */
    public Directory getRoot() {
        return root;
    }

    /**
     * Retorna o diretório atual.
     *
     * @return Diretório atual.
     */
    public Directory getCurrentDir() {
        return currentDir;
    }

    /**
     * Muda o diretório atual para o informado.
     *
     * @param dir Novo diretório atual.
     */
    public void changeDirectory(Directory dir) {
        currentDir = dir;
    }

    /**
     * Cria um novo diretório dentro do diretório atual.
     *
     * @param name        Nome do novo diretório.
     * @param owner       Dono do diretório.
     * @param permissions Permissões definidas para o diretório.
     */
    public void createDirectory(String name, String owner, String permissions) {
        Directory newDir = new Directory(name, currentDir);
        newDir.setOwner(owner);
        newDir.setPermissions(permissions);
        currentDir.addSubdir(newDir);
    }

    /**
     * Cria um novo arquivo no diretório atual e aloca o espaço necessário.
     *
     * @param name        Nome do arquivo.
     * @param size        Tamanho do arquivo (em KB).
     * @param content     Conteúdo inicial do arquivo.
     * @param owner       Dono do arquivo.
     * @param permissions Permissões do arquivo.
     * @throws Exception se não houver espaço suficiente para alocar o arquivo.
     */
    public void createFile(String name, int size, String content, String owner, String permissions) throws Exception {
        File newFile = new File(name, size, content);
        newFile.setOwner(owner);
        newFile.setPermissions(permissions);
        currentDir.addFile(newFile);
        // Aloca espaço no armazenamento conforme o tamanho do arquivo
        storage.allocate(size);
    }

    /**
     * Lê e retorna o conteúdo de um arquivo no diretório atual.
     *
     * @param name Nome do arquivo a ser lido.
     * @return Conteúdo do arquivo.
     * @throws IllegalArgumentException se o arquivo não for encontrado.
     */
    public String readFile(String name) {
        File file = currentDir.getFiles().get(name);
        if (file != null) {
            return file.getContent();
        }
        throw new IllegalArgumentException("File not found");
    }

    /**
     * Escreve (ou sobrescreve) o conteúdo de um arquivo no diretório atual,
     * atualizando seu tamanho e realocando o espaço de armazenamento.
     *
     * @param name    Nome do arquivo.
     * @param content Novo conteúdo do arquivo.
     * @throws Exception se o arquivo não for encontrado ou se não houver espaço.
     */
    public void writeFile(String name, String content) throws Exception {
        File file = currentDir.getFiles().get(name);
        if (file != null) {
            // Calcula o novo tamanho com base no conteúdo em KB (simplificação: bytes/1024 + 1)
            int newSize = (content.getBytes().length / 1024) + 1;
            // Libera o espaço anteriormente alocado para o arquivo
            storage.deallocate(file.getSize());
            // Atualiza o conteúdo e o tamanho do arquivo
            file.setContent(content);
            file.setSize(newSize);
            // Aloca novamente o espaço conforme o novo tamanho
            storage.allocate(newSize);
        } else {
            throw new IllegalArgumentException("File not found");
        }
    }

    /**
     * Exclui um arquivo do diretório atual e libera o espaço de armazenamento utilizado.
     *
     * @param name Nome do arquivo a ser excluído.
     * @throws IllegalArgumentException se o arquivo não for encontrado.
     */
    public void deleteFile(String name) {
        File file = currentDir.getFiles().remove(name);
        if (file != null) {
            // Libera o espaço ocupado pelo arquivo
            storage.deallocate(file.getSize());
        } else {
            throw new IllegalArgumentException("File not found");
        }
    }

    /**
     * Exclui um diretório (caso esteja vazio) e remove sua referência do diretório pai.
     *
     * @param dir Diretório a ser excluído.
     * @throws Exception se o diretório for nulo ou não estiver vazio.
     */
    public void deleteDirectory(Directory dir) throws Exception {
        if (dir == null) throw new Exception("Diretório não existe");
        if (!dir.isEmpty()) throw new Exception("Diretório não está vazio");

        // Remove o diretório do mapa de subdiretórios do diretório pai
        Directory parent = dir.getParent();
        if (parent != null) {
            parent.getSubdirs().remove(dir.getName());
        }
    }

    /**
     * Altera as permissões de um arquivo.
     *
     * @param file        Arquivo a ter as permissões alteradas.
     * @param permissions Novas permissões.
     */
    public void changePermissions(File file, String permissions) {
        file.setPermissions(permissions);
    }

    /**
     * Altera as permissões de um diretório.
     *
     * @param dir         Diretório a ter as permissões alteradas.
     * @param permissions Novas permissões.
     */
    public void changePermissions(Directory dir, String permissions) {
        dir.setPermissions(permissions);
    }

    /**
     * Verifica se o usuário atual possui o papel de administrador.
     *
     * @return true se o usuário atual for admin; caso contrário, false.
     */
    public boolean isAdmin() {
        return currentUser.getRole().equals("admin");
    }
}
