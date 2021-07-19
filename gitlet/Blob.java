package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blob implements Serializable {

    private String fileName;
    private byte[] byteArray;
    private String fileContent;

    public Blob (String fileName, byte[] byteArray, String fileContent) {
        this.fileName = fileName;
        this.byteArray = byteArray;
        this.fileContent = fileContent;
    }

    public String getName() {
        return fileName;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public String getFileContent() { return fileContent; }

    public boolean compareBlob(String other) {
        return new String(byteArray).equals(other);
    }
}
