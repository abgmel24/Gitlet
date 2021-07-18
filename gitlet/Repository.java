package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File REPO_DIR = join(GITLET_DIR, "gitletRepo");
    private static Branch currentBranch;

    /* TODO: fill in the rest of this class. */
    public Repository() {
        if (!REPO_DIR.exists()) {
            GITLET_DIR.mkdir();
            REPO_DIR.mkdir();
            initializeRepo();
        }
    }

    private void initializeRepo() {
        /** Create initial commit */
        Commit init = new Commit("init commit", null, new Date(0));
        init.addCommit(init.generateKey());
        /** Create master branch and set its head*/
        File BRANCH_DIR = Utils.join(REPO_DIR, "branches");
        BRANCH_DIR.mkdir();
        Branch master = new Branch("master");
        master.setHead(init);
        currentBranch = master;
        /** Create staging area */
        StagingArea stage = new StagingArea();
        /** Create empty blobs arraylist */
        File blobListFile = Utils.join(REPO_DIR, "blobList");
        ArrayList<Blob> blobList = new ArrayList<>();
        Utils.writeObject(blobListFile, blobList);
    }

    public static Branch getCurrentBranch() {
        return currentBranch;
    }
}
