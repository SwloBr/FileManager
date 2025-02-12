package com.swlo.filemanager;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;

public class FileSystemSimulator extends JFrame {
    private JTree tree;
    private DefaultTreeModel treeModel;
    private final FileSystem fs;
    private JLabel statusLabel;
    private JLabel pathLabel;
    private JLabel userLabel;

    public FileSystemSimulator() {
        fs = new FileSystem();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Simulador de Sistema de Arquivos v2.0");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Painel de informações
        JPanel infoPanel = new JPanel(new BorderLayout());
        pathLabel = new JLabel(" Caminho atual: /");
        statusLabel = new JLabel(" Espaço livre: " + fs.getStorage().getFreeBlocks() + "KB");
        userLabel = new JLabel(" Usuário: " + fs.getCurrentUser().getUsername() + " (Permissão: " + fs.getCurrentUser().getRole() + ")");

        JPanel statusPanel = new JPanel(new GridLayout(3, 1));
        statusPanel.add(pathLabel);
        statusPanel.add(statusLabel);
        statusPanel.add(userLabel);
        infoPanel.add(statusPanel, BorderLayout.NORTH);

        // Árvore de diretórios
        tree = new JTree();
        updateTree(); // Agora statusLabel já está inicializado
        tree.addTreeSelectionListener(e -> {
            TreePath path = tree.getSelectionPath();
            if (path != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                navigateToNode(node);
            }else {
                System.out.println("Nenhum item selecionado.");
                navigateToNode((DefaultMutableTreeNode) tree.getModel().getRoot());
            }
        });
        mainPanel.add(new JScrollPane(tree), BorderLayout.CENTER);

        // Painel de controle
        JPanel controlPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        String[] buttons = {
                "Criar Diretório", "Criar Arquivo", "Editar Arquivo",
                "Excluir", "Ler Arquivo", "Permissões", "Criar Usuário", "Login"
        };

        for (String text : buttons) {
            JButton btn = new JButton(text);
            btn.addActionListener(e -> handleButtonClick(text));
            controlPanel.add(btn);
        }

        infoPanel.add(controlPanel, BorderLayout.EAST);
        mainPanel.add(infoPanel, BorderLayout.WEST);

        add(mainPanel);
    }

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
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPermissionError() {
        JOptionPane.showMessageDialog(this, "Apenas o usuário admin pode realizar esta ação.", "Erro", JOptionPane.ERROR_MESSAGE);
    }

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
                JOptionPane.showMessageDialog(this, "User created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

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
                    userLabel.setText(" Usuário: " + fs.getCurrentUser().getUsername() + " (Permissão: " + fs.getCurrentUser().getRole() + ")");
                    JOptionPane.showMessageDialog(this, "User logged in successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateTree() {
        DefaultMutableTreeNode rootNode = buildTree(fs.getRoot());
        treeModel = new DefaultTreeModel(rootNode);
        tree.setModel(treeModel);
        updateStatus();
    }

    private DefaultMutableTreeNode buildTree(Directory dir) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir.getName());
        for (Directory subdir : dir.getSubdirs().values()) {
            node.add(buildTree(subdir));
        }
        for (File file : dir.getFiles().values()) {
            node.add(new DefaultMutableTreeNode(file.getName() + " (" + file.getSize() + "KB)"));
        }
        return node;
    }

    private void updateStatus() {
        statusLabel.setText(" Espaço livre: " + fs.getStorage().getFreeBlocks() + "KB");
        pathLabel.setText(" Caminho atual: " + getCurrentPath());
    }

    private String getCurrentPath() {
        StringBuilder path = new StringBuilder();
        Directory dir = fs.getCurrentDir();
        while (dir != null) {
            path.insert(0, "/" + dir.getName());
            dir = dir.getParent();
        }
        return path.toString();
    }

    private void navigateToNode(DefaultMutableTreeNode node) {
        Directory target = findDirectory(node);
        if (target != null) {
            fs.changeDirectory(target);
            updateStatus();
        }
    }

    private Directory findDirectory(DefaultMutableTreeNode node) {
        return findDirectoryRecursive(fs.getRoot(), node);
    }

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

    private void createDirectory() {
        String name = JOptionPane.showInputDialog("Nome do diretório:");
        if (name != null && !name.isEmpty()) {
            try {
                fs.createDirectory(name, "admin", "rwxr-xr-x");
                updateTree();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createFile() {
        String name = JOptionPane.showInputDialog("Nome do arquivo:");
        String content = JOptionPane.showInputDialog("Conteúdo:");
        if (name != null && !name.isEmpty()) {
            try {
                int size = (content != null) ? (content.getBytes().length *104) + 1 : 0;
                fs.createFile(name, size, content, "admin", "rw-r--r--");
                updateTree();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editFile() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null) {
            System.out.println(node.getUserObject().toString());
            String name = node.getUserObject().toString();
            if (name.endsWith("KB)")) {
                String fileName = name.substring(0, name.lastIndexOf(" ("));
                try {
                    String content = fs.readFile(fileName);
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
        }
        else {
            JOptionPane.showMessageDialog(this, "Nenhum item selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteItem() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null) {
            String name = node.getUserObject().toString();
            try {
                if (name.endsWith("KB)")) {
                    String fileName = name.substring(0, name.lastIndexOf(" ("));
                    String content = fs.readFile(fileName);
                    if (content != null && !content.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Não é possível excluir arquivos com conteúdo.", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    fs.deleteFile(fileName);
                } else {
                    Directory dir = findDirectory(node);
                    fs.deleteDirectory(dir);
                }
                updateTree();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void readFile() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null) {
            String name = node.getUserObject().toString();
            if (name.endsWith("KB)")) {
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

    private void showPermissions() {
        if (!fs.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Apenas o usuário admin pode alterar permissões.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null) {
            String name = node.getUserObject().toString();
            try {
                String newPermissions = JOptionPane.showInputDialog(
                        this,
                        "Novas permissões (ex: rwxr-xr-x):",
                        "Alterar Permissões",
                        JOptionPane.QUESTION_MESSAGE
                );

                if (newPermissions != null && !newPermissions.trim().isEmpty()) {
                    if (name.endsWith("KB)")) {
                        String fileName = name.substring(0, name.lastIndexOf(" ("));
                        File file = fs.getCurrentDir().getFiles().get(fileName);
                        if (file != null) {
                            fs.changePermissions(file, newPermissions);
                        } else {
                            JOptionPane.showMessageDialog(this, "Arquivo não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        Directory dir = findDirectory(node);
                        if (dir != null) {
                            fs.changePermissions(dir, newPermissions);
                        } else {
                            JOptionPane.showMessageDialog(this, "Diretório não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    updateTree();
                } else {
                    JOptionPane.showMessageDialog(this, "Permissões inválidas ou vazias.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nenhum item selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FileSystemSimulator().setVisible(true);
        });
    }
}