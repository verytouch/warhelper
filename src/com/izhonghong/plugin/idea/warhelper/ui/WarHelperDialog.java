package com.izhonghong.plugin.idea.warhelper.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.izhonghong.plugin.idea.warhelper.core.DefaultWarHelper;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class WarHelperDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField aaaWarTextField;
    private JTextField cUsersAdministratorDesktopTextField;
    private JTextField webRootTextField;
    private JProgressBar progressBar1;
    private JPanel panel1;
    private JTextField textField1;

    public JTextField getAaaWarTextField() {
        return aaaWarTextField;
    }

    public JTextField getcUsersAdministratorDesktopTextField() {
        return cUsersAdministratorDesktopTextField;
    }

    public JTextField getWebRootTextField() {
        return webRootTextField;
    }

    public JTextField getTextField1() {
        return textField1;
    }

    private Project project;

    public WarHelperDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        textField1.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                JTextField date = (JTextField) e.getSource();
                if (StringUtil.isEmpty(date.getText())) {
                    date.setText(LocalDateTime.now().minus(2, ChronoUnit.HOURS)
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        final WarHelperDialog dialog = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new DefaultWarHelper(dialog, project).doWar();
                } catch (Exception e) {
                    if (!progressBar1.isVisible()) {
                        progressBar1.setVisible(true);
                    }
                    dialog.stopProgress("package error: " + Optional.ofNullable(e.getMessage()).orElse(e.getClass().getName()));
                }
            }
        }).start();
    }

    private void onCancel() {
        dispose();
    }

    private void createUIComponents() {
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setWarName(String name) {
        if (aaaWarTextField != null && StringUtil.isNotEmpty(name)) {
            aaaWarTextField.setText(name);
        }
    }

    public void setPackagePath(String path) {
        if (aaaWarTextField != null && StringUtil.isNotEmpty(path)) {
            cUsersAdministratorDesktopTextField.setText(path);
        }
    }

    public void showProgress() {
        buttonOK.setEnabled(false);
        progressBar1.setIndeterminate(true);
        progressBar1.setString("command is processing, please wait...");
        if (!progressBar1.isVisible()) {
            progressBar1.setVisible(true);
        }
    }

    public void stopProgress(String msg) {
        buttonOK.setEnabled(true);
        progressBar1.setIndeterminate(false);
        progressBar1.setValue(0);
        progressBar1.setString( msg);
    }

    public static void init(Project project) {
        UIManager.put("ProgressBar.selectionBackground", new Color(0, 179, 191));
        UIManager.put("ProgressBar.selectionForeground", new Color(0, 179, 191));

        WarHelperDialog dialog = new WarHelperDialog();
        dialog.setTitle("warhelper");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width / 2 - 250;
        int y = screenSize.height / 2 - 150;
        dialog.setLocation(x, y);

        if (project != null) {
            dialog.setWarName(project.getName() + ".war");
        }
        dialog.setPackagePath(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "\\");
        dialog.setProject(project);
        dialog.pack();
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        init(null);
        System.exit(0);
    }
}
