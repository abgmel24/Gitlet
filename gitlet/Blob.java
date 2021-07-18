package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blob implements Serializable {

    private String fileName;
    private byte[] byteArray;

    public Blob (String fileName, byte[] byteArray) {
        this.fileName = fileName;
        this.byteArray = byteArray;
    }

    public String getName() {
        return fileName;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public boolean compareBlob(String other) {
        return new String(byteArray).equals(other);
    }
}
