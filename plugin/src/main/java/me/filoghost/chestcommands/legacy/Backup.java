/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.legacy;

import me.filoghost.fcommons.Preconditions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class Backup {

    private final Path dataFolder;
    private final Path backupFolder;
    private final Path infoFile;

    public Backup(Path dataFolder, String backupName) {
        this.dataFolder = dataFolder;
        Path backupsFolder = dataFolder.resolve("old_files");
        this.backupFolder = backupsFolder.resolve(backupName);
        this.infoFile = backupsFolder.resolve("readme.txt");
    }

    public static Backup newTimestampedBackup(Path dataFolder) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm"));
        String backupName = "backup_" + date;
        return new Backup(dataFolder, backupName);
    }

    public void addFile(Path fileToBackup) throws IOException {
        Preconditions.checkArgument(fileToBackup.startsWith(dataFolder), "file is not inside data folder");
        Path destination = backupFolder.resolve(dataFolder.relativize(fileToBackup));
        Files.createDirectories(destination.getParent());

        // Add backup file if not already present
        if (!Files.isRegularFile(destination)) {
            Files.copy(fileToBackup, destination);
        }

        // Add README file if not already present
        if (!Files.isRegularFile(infoFile)) {
            Files.write(infoFile, Arrays.asList(
                    "此文件夹中的文件是原始配置文件的副本，这些文件已自动升级。",
                    "",
                    "注意：某些配置升级会删除注释和其他格式（例如空行）。"
            ));
        }
    }

}
