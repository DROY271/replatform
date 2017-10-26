package org.superbiz.moviefun.albums;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;

public class FileStore implements BlobStore{
    @Override
    public void put(Blob blob) throws IOException {
        File targetFile = new File(blob.name);
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(targetFile))) {
            InputStream is = buffered(blob.inputStream);
            int c;
            while ((c = is.read()) != -1){
                outputStream.write(c);
            }
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        File file = new File(name);
        Blob blob = null;
        if (file.exists()) {
            blob = new Blob(name, buffered(new FileInputStream(file)), "");
        }
        return Optional.ofNullable(blob);
    }

    private InputStream buffered(InputStream is) {
        return (is instanceof BufferedInputStream)? is: new BufferedInputStream(is);
    }
}
