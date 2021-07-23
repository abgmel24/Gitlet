package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import static gitlet.Utils.*;

public class Branch implements Serializable {

    private String name;
    private String commitId;

    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    public static final File BRANCH_DIR = Utils.join(GITLET_DIR, "branches");
    public static final File COMMITS = join(GITLET_DIR, "commits");
    public static final File STATE = Utils.join(GITLET_DIR, "state");

    public Branch(String name) {
        this.name = name;
    }

    public void setHead(String commitId) {
        this.commitId = commitId;
        File branchFile = Utils.join(BRANCH_DIR, name);
        Utils.writeObject(branchFile, this);
        if (STATE.exists()) {
            Repository.setCurrentBranch(name);
        }
    }

    public Commit getCurrentCommit() {
        HashMap<String,Commit> commitsHashMap = Utils.readObject(COMMITS, HashMap.class);
        Commit currentCommit = commitsHashMap.get(commitId);
        return currentCommit;
    }

    public Commit getCommit(String id) {
        HashMap<String,Commit> commitsHashMap = Utils.readObject(COMMITS, HashMap.class);
        Commit currentCommit = commitsHashMap.get(id);
        return currentCommit;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getCurrentCommitId() {
        return commitId;
    }

    public String getName() {
        return name;
    }
}
