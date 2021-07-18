package gitlet;

// TODO: any imports you need here
 import java.io.File;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.HashMap;

 import static gitlet.Utils.join;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date timestamp;
    private Commit parent;
    private String key;
    private static HashMap<String, Commit> CommitsMap = new HashMap<>();
    private HashMap<String, Integer> blobsInCommit = new HashMap<>();

    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** The .gitletRepo directory. */
    public static final File REPO_DIR = Utils.join(GITLET_DIR, "gitletRepo");

    /** The Commit File. */
    public static final File COMMITS = join(REPO_DIR, "commits.txt");

    public static final File BLOBS = join(REPO_DIR, "blobs.txt");

    /* TODO: fill in the rest of this class. */

    public Commit(String message, Commit parent, Date timestamp) {
        this.message = message;
        this.parent = parent;
        this.timestamp = timestamp;
    }

    public String generateKey() {
        String key = Utils.sha1(Utils.serialize(this));
        this.key = key;
        return key;
    }

    public void addCommit(String key) {
        CommitsMap.put(key, this);
        Utils.writeObject(COMMITS, CommitsMap);
    }

    public void addBlob (Blob blob, int blobIndex) {
        blobsInCommit.put(blob.getName(), blobIndex);
    }

    public Blob getBlob(String name) {
        int blobIndex = blobsInCommit.get(name);
        ArrayList<Blob> blobList = Utils.readObject(BLOBS, ArrayList.class);
        return blobList.get(blobIndex);
    }

    public HashMap getBlobsMap() {
        return blobsInCommit;
    }

    public String getKey() {
        return key;
    }

    public String getMessage() {
        return message;
    }

    public Commit getParent() {
        return parent;
    }
}
