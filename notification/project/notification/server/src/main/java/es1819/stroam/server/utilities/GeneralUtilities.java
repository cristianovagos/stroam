package es1819.stroam.server.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GeneralUtilities {

    public static boolean compressFile(File compressingFilePath, File compressionFileResultPath) throws Exception {
        if(!compressingFilePath.exists() || compressionFileResultPath == null)
            return false; //Compression failed

        ZipOutputStream compressedFileOutputStream = new ZipOutputStream(
                new FileOutputStream(compressionFileResultPath));
        compressedFileOutputStream.putNextEntry(new ZipEntry(compressingFilePath.getName()));

        int readLength;
        byte[] buffer = new byte[1024];
        FileInputStream compressingFileInputStream = new FileInputStream(compressingFilePath);
        while ((readLength = compressingFileInputStream.read(buffer)) > 0)
            compressedFileOutputStream.write(buffer, 0, readLength);

        compressingFileInputStream.close();
        compressedFileOutputStream.closeEntry();

        compressedFileOutputStream.close();
        return true; //Compression succeeded (other cases an exception is throwed)
    }

    public static String createString(String... channelParts) {
        if(channelParts == null || channelParts.length == 0)
            return null;

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < channelParts.length; i++) {
            //TODO: fazer algum tipo de validação se necessário
            result.append(channelParts[i]);
        }
        return result.toString();
    }

}
