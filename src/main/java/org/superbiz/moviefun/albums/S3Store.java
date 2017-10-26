package org.superbiz.moviefun.albums;

import com.amazonaws.RequestClientOptions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.tika.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class S3Store implements BlobStore {

    private final AmazonS3Client client;
    private final String bucketName;

    public S3Store(AmazonS3Client s3Client, String s3BucketName) {
        this.client = s3Client;
        this.bucketName = s3BucketName;
    }

    @Override
    public void put(Blob blob) throws IOException {
        byte[] bytes = IOUtils.toByteArray(blob.inputStream);
        client.putObject(bucketName, blob.name, new ByteArrayInputStream(bytes), null);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        Blob blob = null;
        if (client.doesObjectExist(bucketName, name)) {
            S3Object object = client.getObject(bucketName, name);
            InputStream is = object.getObjectContent();
            blob = new Blob(name, is, "");
        }
        return Optional.ofNullable(blob);
    }
}
