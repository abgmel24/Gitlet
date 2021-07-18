package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blob implements Serializable {

    private File file;
    private byte[] byteArray;

    public Blob (File f) {
        this.file = f;
        byteArray = readContents(f);
    }

    public String getName() {
        return file.getName();
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public boolean compareBlob(Blob other) {
        return byteArray.equals(other.getByteArray());
    }
}
