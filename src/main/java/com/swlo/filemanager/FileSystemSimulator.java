package com.swlo.filemanager;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;

/**
 * Janela principal do simulador de sistema de arquivos.
 * Implementa uma interface gráfica que permite visualizar a estrutura de diretórios
 * e arquivos em um JTree e realizar operações através de botões de controle.
 */
public class FileSystemSimulator extends JFrame {
    // Árvore que exibe a estrutura de diretórios e arquivos
    private JTree tree;
    // Modelo da árvore, usado para atualizar a visualização
    private DefaultTreeModel treeModel;
    // Instância do sistema de arquivos simulado
    private final FileSystem fs;
    // Rótulos para exibir informações de status, caminho atual e usuário logado
    private JLabel statusLabel;
    private JLabel pathLabel;
    private JLabel userLabel;

    /**
     * Construtor do simulador.
     * Inicializa o sistema de arquivos e configura a interface gráfica.
     */
    public FileSystemSimulator() {
        fs = new FileSystem();
        initializeUI();
    }

    /**
     * Configura a interface gráfica (UI) do simulador.
     * Define a janela, os painéis de informação, a árvore de diretórios e os botões de controle.
     */
    private void initializeUI() {
        setTitle("Simulador de Sistema de Arquivos v2.0");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Painel principal com layout BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Painel de informações, exibindo caminho atual, espaço livre e usuário logado
        JPanel infoPanel = new JPanel(new BorderLayout());
        pathLabel = new JLabel(" Caminho atual: /");
        statusLabel = new JLabel(" Espaço livre: " + fs.getStorage().getFreeBlocks() + "KB");
        userLabel = new JLabel(" Usuário: " + fs.getCurrentUser().getUsername() +
                " (Permissão: " + fs.getCurrentUser().getRole() + ")");

        // Painel que agrupa os rótulos de status em um layout de grade (3 linhas, 1 coluna)
        JPanel statusPanel = new JPanel(new GridLayout(3, 1));
        statusPanel.add(pathLabel);
        statusPanel.add(statusLabel);
        statusPanel.add(userLabel);
        infoPanel.add(statusPanel, BorderLayout.NORTH);

        // Configuração da árvore de diretórios
        tree = new JTree();
        updateTree(); // Inicializa a árvore com a estrutura atual do sistema de arquivos
        // Adiciona ouvinte para mudança de seleção na árvore
        tree.addTreeSelectionListener(e -> {
            TreePath path = tree.getSelectionPath();
            if (path != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                navigateToNode(node);
            } else {
                System.out.println("Nenhum item selecionado.");
                navigateToNode((DefaultMutableTreeNode) tree.getModel().getRoot());
            }
        });
        mainPanel.add(new JScrollPane(tree), BorderLayout.CENTER);

        // Painel de controle com botões para as operações
        JPanel controlPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        String[] buttons = {
                "Criar Diretório", "Criar Arquivo", "Editar Arquivo",
                "Excluir", "Ler Arquivo", "Permissões", "Criar Usuário", "Login"
        };

        // Cria os botões e associa os eventos a cada ação
        for (String text : buttons) {
            JButton btn = new JButton(text);
            btn.addActionListener(e -> handleButtonClick(text));
            controlPanel.add(btn);
        }

        // Adiciona o painel de controle ao painel de informações (lado leste)
        infoPanel.add(controlPanel, BorderLayout.EAST);
        // Adiciona o painel de informações ao painel principal (lado oeste)
        mainPanel.add(infoPanel, BorderLayout.WEST);

        add(mainPanel);
    }

    /**
     * Trata os cliques nos botões de controle, direcionando para a operação correspondente.
     *
     * @param action Texto do botão clicado.
     */
    private void handleButtonClick(String action) {
        try {
            switch (action) {
                case "Criar Diretório" -> {
                    if (fs.isAdmin()) {
                        createDirectory();
                    } else {
                        showPermissionError();
                    }
                }
                case "Criar Arquivo" -> {
                    if (fs.isAdmin()) {
                        createFile();
                    } else {
                        showPermissionError();
                    }
                }
                case "Editar Arquivo" -> {
                    if (fs.isAdmin()) {
                        editFile();
                    } else {
                        showPermissionError();
                    }
                }
                case "Excluir" -> {
                    if (fs.isAdmin()) {
                        deleteItem();
                    } else {
                        showPermissionError();
                    }
                }
                case "Ler Arquivo" -> readFile();
                case "Permissões" -> {
                    if (fs.isAdmin()) {
                        showPermissions();
                    } else {
                        showPermissionError();
                    }
                }
                case "Criar Usuário" -> {
                    if (fs.isAdmin()) {
                        createUser();
                    } else {
                        showPermissionError();
                    }
                }
                case "Login" -> loginUser();
            }
        } catch (Exception ex) {
            // Exibe mensagem de erro caso ocorra alguma exceção durante a operação
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exibe uma mensagem de erro indicando que apenas o usuário admin pode realizar a ação.
     */
    private void showPermissionError() {
        JOptionPane.showMessageDialog(this, "Apenas o usuário admin pode realizar esta ação.",
                "Erro", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Cria um novo usuário solicitando os dados por meio de diálogos.
     */
    private void createUser() {
        JTextField usernameField = new JTextField();
        JComboBox<String> roleField = new JComboBox<>(new String[]{"admin", "user"});
        Object[] message = {
                "Username:", usernameField,
                "Role:", roleField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Create User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String role = (String) roleField.getSelectedItem();
            if (username != null && !username.trim().isEmpty() && role != null) {
                fs.addUser(username, role);
                JOptionPane.showMessageDialog(this, "User created successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Realiza o login de um usuário solicitando o username.
     */
    private void loginUser() {
        JTextField usernameField = new JTextField();
        Object[] message = {
                "Username:", usernameField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            if (username != null && !username.trim().isEmpty()) {
                try {
                    fs.setCurrentUser(username);
                    // Atualiza o rótulo do usuário logado
                    userLabel.setText(" Usuário: " + fs.getCurrentUser().getUsername() +
                            " (Permissão: " + fs.getCurrentUser().getRole() + ")");
                    JOptionPane.showMessageDialog(this, "User logged in successfully.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Atualiza a árvore de diretórios com a estrutura atual do sistema de arquivos.
     */
    private void updateTree() {
        DefaultMutableTreeNode rootNode = buildTree(fs.getRoot());
        treeModel = new DefaultTreeModel(rootNode);
        tree.setModel(treeModel);
        updateStatus();
    }

    /**
     * Constrói recursivamente um nó da árvore (DefaultMutableTreeNode) a partir de um objeto Directory.
     *
     * @param dir Diretório de onde partir a construção da árvore.
     * @return Nó da árvore representando o diretório e seus conteúdos.
     */
    private DefaultMutableTreeNode buildTree(Directory dir) {
        // Cria o nó para o diretório atual
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir.getName());
        // Adiciona recursivamente os subdiretórios
        for (Directory subdir : dir.getSubdirs().values()) {
            node.add(buildTree(subdir));
        }
        // Adiciona os arquivos (mostrando o nome e o tamanho)
        for (File file : dir.getFiles().values()) {
            node.add(new DefaultMutableTreeNode(file.getName() + " (" + file.getSize() + "KB)"));
        }
        return node;
    }

    /**
     * Atualiza os rótulos de status, exibindo o espaço livre e o caminho atual.
     */
    private void updateStatus() {
        statusLabel.setText(" Espaço livre: " + fs.getStorage().getFreeBlocks() + "KB");
        pathLabel.setText(" Caminho atual: " + getCurrentPath());
    }

    /**
     * Constrói o caminho atual (string) a partir do diretório corrente.
     *
     * @return Caminho atual no formato /root/subdir/...
     */
    private String getCurrentPath() {
        StringBuilder path = new StringBuilder();
        Directory dir = fs.getCurrentDir();
        while (dir != null) {
            // Insere cada diretório no início do caminho
            path.insert(0, "/" + dir.getName());
            dir = dir.getParent();
        }
        return path.toString();
    }

    /**
     * Altera o diretório atual do sistema para o diretório correspondente ao nó selecionado na árvore.
     *
     * @param node Nó selecionado na árvore.
     */
    private void navigateToNode(DefaultMutableTreeNode node) {
        Directory target = findDirectory(node);
        if (target != null) {
            fs.changeDirectory(target);
            updateStatus();
        }
    }

    /**
     * Retorna o objeto Directory correspondente ao nó selecionado.
     *
     * @param node Nó da árvore.
     * @return Objeto Directory ou null se não encontrado.
     */
    private Directory findDirectory(DefaultMutableTreeNode node) {
        return findDirectoryRecursive(fs.getRoot(), node);
    }

    /**
     * Busca recursivamente o diretório que corresponda ao nó alvo.
     *
     * @param current    Diretório atual na recursão.
     * @param targetNode Nó que se deseja encontrar.
     * @return Diretório encontrado ou null caso não exista.
     */
    private Directory findDirectoryRecursive(Directory current, DefaultMutableTreeNode targetNode) {
        if (current.getName().equals(targetNode.getUserObject().toString())) {
            return current;
        }
        for (Directory subdir : current.getSubdirs().values()) {
            Directory found = findDirectoryRecursive(subdir, targetNode);
            if (found != null) return found;
        }
        return null;
    }

    /**
     * Cria um novo diretório solicitando o nome por meio de um diálogo.
     */
    private void createDirectory() {
        String name = JOptionPane.showInputDialog("Nome do diretório:");
        if (name != null && !name.isEmpty()) {
            try {
                // Cria o diretório com dono "admin" e permissões padrão
                fs.createDirectory(name, "admin", "rwxr-xr-x");
                updateTree();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Cria um novo arquivo solicitando o nome e o conteúdo por meio de diálogos.
     */
    private void createFile() {
        String name = JOptionPane.showInputDialog("Nome do arquivo:");
        String content = JOptionPane.showInputDialog("Conteúdo:");
        if (name != null && !name.isEmpty()) {
            try {
                // Calcula o tamanho do arquivo (exemplo de cálculo; pode ser ajustado conforme necessário)
                int size = (content != null) ? (content.getBytes().length * 104) + 1 : 0;
                fs.createFile(name, size, content, "admin", "rw-r--r--");
                updateTree();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Permite a edição de um arquivo selecionado na árvore.
     * Se o nó selecionado representa um arquivo, é exibido um JTextArea com o conteúdo atual,
     * permitindo a edição. Após confirmação, o arquivo é atualizado.
     */
    private void editFile() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null) {
            System.out.println(node.getUserObject().toString());
            String name = node.getUserObject().toString();
            // Verifica se o nó representa um arquivo (pelo padrão " (xKB)")
            if (name.endsWith("KB)")) {
                String fileName = name.substring(0, name.lastIndexOf(" ("));
                try {
                    // Lê o conteúdo atual do arquivo
                    String content = fs.readFile(fileName);
                    // Cria um JTextArea para edição do conteúdo
                    JTextArea textArea = new JTextArea(content, 10, 40);
                    int result = JOptionPane.showConfirmDialog(
                            this,
                            new JScrollPane(textArea),
                            "Editar Arquivo",
                            JOptionPane.OK_CANCEL_OPTION
                    );

                    if (result == JOptionPane.OK_OPTION) {
                        fs.writeFile(fileName, textArea.getText());
                        updateTree();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nenhum item selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exclui o item (arquivo ou diretório) selecionado na árvore.
     * Se for um arquivo, verifica se o conteúdo está vazio; se for um diretório, tenta excluí-lo.
     */
    private void deleteItem() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null) {
            String name = node.getUserObject().toString();
            try {
                if (name.endsWith("KB)")) { // Se for um arquivo
                    String fileName = name.substring(0, name.lastIndexOf(" ("));
                    String content = fs.readFile(fileName);
                    // Impede a exclusão de arquivos com conteúdo
                    if (content != null && !content.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Não é possível excluir arquivos com conteúdo.",
                                "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    fs.deleteFile(fileName);
                } else { // Se for um diretório
                    Directory dir = findDirectory(node);
                    fs.deleteDirectory(dir);
                }
                updateTree();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Lê o conteúdo de um arquivo selecionado na árvore e exibe em um diálogo.
     */
    private void readFile() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null) {
            String name = node.getUserObject().toString();
            if (name.endsWith("KB)")) { // Verifica se o nó representa um arquivo
                String fileName = name.substring(0, name.lastIndexOf(" ("));
                try {
                    String content = fs.readFile(fileName);
                    JOptionPane.showMessageDialog(this, content, "Conteúdo do Arquivo", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Permite a alteração das permissões do item (arquivo ou diretório) selecionado.
     * Exibe um diálogo para informar as novas permissões e atualiza o item correspondente.
     */
    private void showPermissions() {
        if (!fs.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Apenas o usuário admin pode alterar permissões.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null) {
            String name = node.getUserObject().toString();
            try {
                // Solicita ao usuário as novas permissões (ex: rwxr-xr-x)
                String newPermissions = JOptionPane.showInputDialog(
                        this,
                        "Novas permissões (ex: rwxr-xr-x):",
                        "Alterar Permissões",
                        JOptionPane.QUESTION_MESSAGE
                );

                if (newPermissions != null && !newPermissions.trim().isEmpty()) {
                    if (name.endsWith("KB)")) { // Se for um arquivo
                        String fileName = name.substring(0, name.lastIndexOf(" ("));
                        File file = fs.getCurrentDir().getFiles().get(fileName);
                        if (file != null) {
                            fs.changePermissions(file, newPermissions);
                        } else {
                            JOptionPane.showMessageDialog(this, "Arquivo não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    } else { // Se for um diretório
                        Directory dir = findDirectory(node);
                        if (dir != null) {
                            fs.changePermissions(dir, newPermissions);
                        } else {
                            JOptionPane.showMessageDialog(this, "Diretório não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    updateTree();
                } else {
                    JOptionPane.showMessageDialog(this, "Permissões inválidas ou vazias.",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nenhum item selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método principal para inicializar o simulador.
     *
     * @param args Argumentos da linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FileSystemSimulator().setVisible(true);
        });
    }
}
