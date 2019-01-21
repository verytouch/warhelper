package com.izhonghong.plugin.idea.warhelper.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.izhonghong.plugin.idea.warhelper.ui.WarHelperDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DefaultWarHelper {

    private WarHelperDialog dialog;
    private Project project;

    public DefaultWarHelper(WarHelperDialog dialog, Project project) {
        Objects.requireNonNull(dialog);
        Objects.requireNonNull(project);
        this.dialog = dialog;
        this.project = project;
    }

    public void doWar() {
        dialog.showProgress();
        String webDir = dialog.getWebRootTextField().getText();
        File projectRoot = new File(project.getProjectFilePath()).getParentFile().getParentFile();
        boolean hasWebDir = false;
        for (File f : projectRoot.listFiles()) {
            if (f.isDirectory() && f.getName().equals(webDir)) {
                hasWebDir = true;
            }
        }
        if (!hasWebDir) {
            afterWar(false, "webdir not found: " + webDir);
            return;
        }
        String path =  projectRoot.getAbsolutePath();
        String warName = dialog.getcUsersAdministratorDesktopTextField().getText() + dialog.getAaaWarTextField().getText();
        String iteratDate = dialog.getTextField1().getText();

        boolean isIterat = StringUtil.isNotEmpty(iteratDate);
        if (isIterat) {
            doIteratWar(path, webDir, warName, iteratDate);
        } else {
            doFullWar(path, webDir, warName);
        }

    }

    private void afterWar(boolean success, String msg) {
        dialog.stopProgress(msg);
        if (success) {
            dialog.dispose();
        }
    }

    private void doFullWar(String path, String webDir, String warName) {
        String command = "cmd /c " + path.substring(0, 2) + " && cd " + path + "/" + webDir
                + " && jar -cf " + warName + " *";
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            File check = new File(warName);
            if (check.exists()) {
                afterWar(true, "package success");
            } else {
                afterWar(false, "unkhnow errro");
            }
        } catch (Exception e) {
            afterWar(false, "package error: " + Optional.ofNullable(e.getMessage()).orElse(e.getClass().getName()));
        }
    }

    private void doIteratWar(String path, String webDir, String warName, String iteratDate) {
        final String datePattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);
        LocalDateTime date = null;
        try {
            date = LocalDateTime.parse(iteratDate, dateTimeFormatter);
        } catch (Exception e) {
            afterWar(false, "iterat must be a date like " + datePattern);
            return;
        }
        long from = date.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        List<File> toWarFiles = new ArrayList<>();
        final String webPath = path + File.separator + webDir;
        addToWarFiles(toWarFiles, new File(webPath), from);
        if (toWarFiles.isEmpty()) {
            afterWar(false, "no file changed after " + date.format(dateTimeFormatter));
            return;
        }

        try {
            warName = warName.replaceAll("(.*)war$", "$1zip");
            try (ZipOutputStream zip  = new ZipOutputStream(new FileOutputStream(warName))){
                for (File file : toWarFiles) {
                    FileInputStream is = new FileInputStream(file);
                    zip.putNextEntry(new ZipEntry(file.getAbsolutePath().replace(webPath + File.separator, "")));
                    int len = -1;
                    byte[] buff = new byte[1024];
                    while ((len = is.read(buff)) != -1) {
                        zip.write(len);
                    }
                    is.close();
                    zip.closeEntry();
                    zip.flush();
                }
            }
        } catch (IOException e) {
            afterWar(false, "package error: " + Optional.ofNullable(e.getMessage()).orElse(e.getClass().getName()));
            return;
        }
        afterWar(true, "success");
    }

    private void addToWarFiles(List<File> files, File file, long from) {
        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                if (file.isFile() && from <= file.lastModified()) {
                    files.add(file);
                } else {
                    addToWarFiles(files, listFile, from);
                }
            }
        } else if(from <= file.lastModified()) {
            files.add(file);
        }
    }

}
