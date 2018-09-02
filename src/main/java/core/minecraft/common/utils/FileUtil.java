package core.minecraft.common.utils;

import java.io.*;
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
    public static void unzipFile(String path, String destination) throws IOException
    {
        byte[] buffer = new byte[1024];
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(path));
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        while (zipEntry != null)
        {
            String fileName = zipEntry.getName();
            File newFile = new File(destination + "/" + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            int len;
            while ((len = zipInputStream.read(buffer)) > 0)
            {
                fileOutputStream.write(buffer, 0, len);
            }
            fileOutputStream.close();
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.closeEntry();
        zipInputStream.close();
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
}
