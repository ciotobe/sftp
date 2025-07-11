package jp.co.jri.epix.sftp.model;

import java.io.InputStream;

public class StreamWithFilename {
    private InputStream stream;
    private String filename;

    public InputStream getStream() {
        return stream;
    }

    public String getFilename() {
        return filename;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}