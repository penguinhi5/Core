package core.minecraft.common.utils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * File utilities.
 *
 * @author Preston Brown
 */
public class FileUtil {

    /**
     * Unzips the zip file at the given path to the specified destination.
     *
     * @param path the location of the zip file being copied
     * @param destination the location that the zip file should be extracted to
     * @throws IOException
     */
    public static void unzipFile(String path, String destination) throws ZipException
    {
        ZipFile zipFile = new ZipFile(path);
        zipFile.extractAll(destination);
    }

    /**
     * Searches for the file with the given name and returns the filepath if it exists, otherwise null.
     *
     * @param container The directory that is being searched
     * @param searchFor The name of the file that is being searched for
     * @return the filepath that leads to the file if it exists, otherwise null
     */
    public static String searchForFile(File container, String searchFor)
    {
        File[] listFiles = container.listFiles();
        if (listFiles == null)
        {
            return null;
        }
        for (File file : listFiles)
        {
            if (file.getName().equalsIgnoreCase(searchFor))
            {
                return file.getPath();
            }
            if (file.isDirectory())
            {
                String results = searchForFile(file, searchFor);
                if (results != null)
                {
                    return results;
                }
            }
        }
        return null;
    }

    /**
     * Deletes the specified file or directory and all of it contents.
     *
     * @param file the file that is being deleted
     */
    public static void deleteFile(File file)
    {
        File[] contents = file.listFiles();
        if (contents != null)
        {
            for (File f : contents)
            {
                if (! Files.isSymbolicLink(f.toPath()))
                {
                    deleteFile(f);
                }
            }
        }
        file.delete();
    }

    /**
     * Returns a list containing all of the files found inside of the directory provided and
     * all of its child directories.
     *
     * <p>If the file provided is not a directory an empty list will be returned.</p>
     *
     * @param file the directory being searched
     * @return a list containing all of the files found in the directory and its children
     */
    public static List<File> getFileContents(File file)
    {
        if (file == null || !file.exists() || !file.isDirectory())
        {
            return new ArrayList<>();
        }

        // Collects all of the files
        ArrayList<File> files = new ArrayList<>();
        File[] contents = file.listFiles();
        for (File fileL : contents)
        {
            if (fileL.isDirectory())
            {
                files.addAll(getFileContents(fileL));
            }
            else
            {
                files.add(fileL);
            }
        }
        return files;
    }
}
