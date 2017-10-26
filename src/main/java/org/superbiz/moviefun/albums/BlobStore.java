package org.superbiz.moviefun.albums;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface BlobStore {

    void put(Blob blob) throws IOException;

    Optional<Blob> get(String name) throws IOException;

    public static class Blob {
        public final String name;
        public final InputStream inputStream;
        public final String contentType;

        public Blob(String name, InputStream inputStream, String contentType) {
            this.name = name;
            this.inputStream = inputStream;
            this.contentType = contentType;
        }
    }

}
