package gitlet;

// TODO: any imports you need here
 import java.io.File;
 import java.io.Serializable;
 import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.Formatter;
 import java.util.HashMap;

 import static gitlet.Utils.join;
 import static gitlet.Utils.readObject;

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
    private String branchName;
    private String message;
    private Date timestamp;
    private String parent;
    private String key;
    private HashMap<String, Integer> blobsInCommit;

    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** The Commit File. */
    public static final File COMMITS = join(GITLET_DIR, "commits");

    public static final File BLOBS = join(GITLET_DIR, "blobList");

    /* TODO: fill in the rest of this class. */

    public Commit(String message, String parent, Date timestamp, String branchName) {
        this.message = message;
        this.parent = parent;
        this.timestamp = timestamp;
        this.branchName = branchName;
        blobsInCommit = new HashMap<>();
    }

    /** Create Commit Helper Functions */
    public String generateKey() {
        String key = Utils.sha1(Utils.serialize(this));
        this.key = key;
        return key;
    }

    public void addCommit(String key) {
        HashMap<String,Commit> CommitsMap = readObject(COMMITS, HashMap.class);
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

    /** Commit Identifiers */
    public String getKey() {
        return key;
    }

    public String getMessage() {
        return message;
    }

    public String getParent() {
        return parent;
    }

    //===
    //commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
    //Date: Thu Nov 9 20:00:05 2017 -0800
    //A commit message.

    public String getBranchName() {
        return branchName;
    }

    public void printCommitLog() {
        System.out.println("===");
        System.out.println("commit " + key);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("E MMM dd hh:mm:ss y Z");
        System.out.println("Date: " + dateFormatter.format(timestamp));
        System.out.println(message + "\n");
    }
}
