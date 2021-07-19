package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    public static final File STATE = Utils.join(GITLET_DIR, "state.txt");
    public static final File BRANCH_DIR = Utils.join(GITLET_DIR, "branches");

    /* TODO: fill in the rest of this class. */
    public Repository() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            initializeRepo();
        }
    }

    private void initializeRepo() {
        /** Initialize State */
        HashMap<String,String> state = new HashMap<>();
        /** Create initial commit */
        File commit = Utils.join(GITLET_DIR, "commits.txt");
        HashMap<String,Commit> CommitsMap = new HashMap<>();
        Utils.writeObject(commit, CommitsMap);
        Commit init = new Commit("init commit", null, new Date(0));
        init.addCommit(init.generateKey());
//        state.put("currentCommit", init.getKey());
        /** Create master branch and set its head*/
        File BRANCH_DIR = Utils.join(GITLET_DIR, "branches");
        BRANCH_DIR.mkdir();
        Branch master = new Branch("master.txt");
        master.setHead(init);
        state.put("currentBranch", master.getName());
        /** Create staging area */
        StagingArea stage = new StagingArea();
        /** Create empty blobs arraylist */
        File blobListFile = Utils.join(GITLET_DIR, "blobList.txt");
        ArrayList<Blob> blobList = new ArrayList<>();
        Utils.writeObject(blobListFile, blobList);
        /** Create Saved State */
        Utils.writeObject(STATE, state);
    }

    public static Branch getCurrentBranch() {
        HashMap<String,String> state = Utils.readObject(STATE, HashMap.class);
        String currentBranchName = state.get("currentBranch");
        File branchFile = Utils.join(BRANCH_DIR, currentBranchName);
        Branch currentBranch = Utils.readObject(branchFile, Branch.class);
        return currentBranch;
    }

    public static void setCurrentBranch(String branchName) {
        HashMap<String,String> state = Utils.readObject(STATE, HashMap.class);
        state.put("currentBranch", branchName);
        Utils.writeObject(STATE, state);
    }

    public static Branch getBranch(String branchName) {
        File branchFile = Utils.join(BRANCH_DIR, branchName);
        Branch currentBranch = Utils.readObject(branchFile, Branch.class);
        return currentBranch;
    }

    /** update state with tracked and untracked files */
    public static void updateStagingArea() {

    }
}
