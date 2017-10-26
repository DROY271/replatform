package org.superbiz.moviefun.albums;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private static final Logger LOG = LoggerFactory.getLogger(AlbumsController.class);

    private final AlbumsBean albumsBean;

    private final BlobStore store;

    public AlbumsController(AlbumsBean albumsBean, BlobStore store) {
        this.albumsBean = albumsBean;
        this.store = store;
    }


    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {

        BlobStore.Blob blob = toBlob(albumId, uploadedFile);
        store.put(blob);
        return format("redirect:/albums/%d", albumId);
    }

    private BlobStore.Blob toBlob(long albumId, MultipartFile file) throws IOException {
        BlobStore.Blob b = new BlobStore.Blob(getBlobName(albumId), new BufferedInputStream(file.getInputStream()), "");
        return b;
    }

    private String getBlobName(long albumId) {
        return format("covers/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {
        // Get blob InputStream for album id
        // If blob is not found, use default
        // Convert InputStream to byte[]
        // Detect Content-Type header & length
        // return HttpEntity

        Optional<BlobStore.Blob> blob = store.get(getBlobName(albumId));
        InputStream is;
        if (blob.isPresent()) {
            LOG.info("Found blob for album {}", albumId);
            is = blob.get().inputStream;
        } else {
            LOG.info("Fallback to default for album {}", albumId);
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("default-cover.jpg");
        }
        byte[] imageBytes = IOUtils.toByteArray(is);
        HttpHeaders headers = createImageHttpHeaders(imageBytes);
        return new HttpEntity<>(imageBytes, headers);
    }

    private HttpHeaders createImageHttpHeaders(byte[] imageBytes) throws IOException {
        String contentType = new Tika().detect(imageBytes);
        LOG.info("Identified image as {}", contentType);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(imageBytes.length);
        return headers;
    }

    private File getCoverFile(@PathVariable long albumId) {
        String coverFileName = getBlobName(albumId);
        return new File(coverFileName);
    }

    private Path getExistingCoverPath(@PathVariable long albumId) throws URISyntaxException {
        File coverFile = getCoverFile(albumId);
        Path coverFilePath;

        if (coverFile.exists()) {
            coverFilePath = coverFile.toPath();
        } else {
            coverFilePath = Paths.get(getSystemResource("default-cover.jpg").toURI());
        }

        return coverFilePath;
    }
}
