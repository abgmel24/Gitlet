package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import static gitlet.Utils.*;

public class Branch implements Serializable {

    private String name;
    private File branch;
    private Commit currentCommit;
    private String commitId;

    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    public static final File BRANCH_DIR = Utils.join(GITLET_DIR, "branches");

    public Branch(String name) {
        this.name = name;
        this.branch = Utils.join(BRANCH_DIR, name);
        if (this.branch.exists()) {
            System.out.println("Branch " + name + "already exists");
            return;
        }
        Utils.writeObject(branch, this);
        Branch thisBranch = Utils.readObject(branch, Branch.class);
        System.out.println("Initialized branch: " + thisBranch.getName());
    }

    public void setHead(Commit commit) {
        commitId = commit.getKey();
        currentCommit = commit;
        Utils.writeObject(branch, this);
    }

    public Commit getCurrentCommit() {
        return currentCommit;
    }

    public String getCurrentCommitId() {
        return commitId;
    }

    public String getName() {
        return name;
    }
}
