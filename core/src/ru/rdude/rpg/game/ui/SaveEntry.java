package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveEntry implements Comparable<SaveEntry> {

    FileHandle file;
    String name;
    Date date;
    String dateString;

    public SaveEntry(FileHandle file) {
        this.file = file;
        this.name = file.nameWithoutExtension();
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
            FileTime fileTime = Files.readAttributes(file.file().toPath(), BasicFileAttributes.class).lastModifiedTime();
            dateString = dateFormat.format(fileTime.toMillis());
            date = dateFormat.parse(dateFormat.format(fileTime.toMillis()));
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(SaveEntry saveEntry) {
        return date.compareTo(saveEntry.date);
    }

    @Override
    public String toString() {
        return "(" + dateString + ")  " + name;
    }
}
