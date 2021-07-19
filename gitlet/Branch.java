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
    public static final File COMMITS = join(GITLET_DIR, "commits.txt");

    public Branch(String name) {
        this.name = name;
        File branchFile = Utils.join(BRANCH_DIR, name);
        if (branchFile.exists()) {
            System.out.println("Branch " + name + "already exists");
            return;
        }
        Utils.writeObject(branchFile, this);
        Branch thisBranch = Utils.readObject(branchFile, Branch.class);
        System.out.println("Initialized branch: " + thisBranch.getName());
    }

    public void setHead(Commit commit) {
        commitId = commit.getKey();
        File branchFile = Utils.join(BRANCH_DIR, name);
        Utils.writeObject(branchFile, this);
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

    public String getCurrentCommitId() {
        return commitId;
    }

    public String getName() {
        return name;
    }
}
