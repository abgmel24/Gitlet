package gitlet;

import java.io.File;
import java.util.ArrayList;

import static gitlet.Utils.*;

public class Blob {
    // Blob class

    private File file;
    private byte[] byteArray;
    private static ArrayList<Blob> blobs = new ArrayList<Blob>();

    public Blob (File f) {
        this.file = f;
        byteArray = readContents(f);
        blobs.add(this);
    }

}
