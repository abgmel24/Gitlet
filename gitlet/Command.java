package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static gitlet.Utils.join;

public class Command {

    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    public static final File COMMITS = join(GITLET_DIR, "commits.txt");
    public static final File stageAdd = Utils.join(GITLET_DIR, "stage/stageAdd");

    public void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        Repository newRepo = new Repository();
//        File repoState = Utils.join(REPO_DIR, "repoState");
//        Utils.writeObject(repoState, (Serializable) newRepo);
    }

    /** gitlet commit - creates new commit and compares current stageAdd files to previous commit files to
     * determine new blobs that need to be initialized. */
    public void commit(String message) {
        List<String> fileNames = Utils.plainFilenamesIn(stageAdd);
        Branch currentBranch = Repository.getCurrentBranch();
        Commit latestCommit = currentBranch.getCurrentCommit();
        HashMap<String, Integer> latestCommitBlobs = latestCommit.getBlobsMap();
        Commit newCommit = new Commit(message, latestCommit.getKey(), new Date());
        File blobsFile = Utils.join(GITLET_DIR, "blobList.txt");
        ArrayList<Blob> blobsList = Utils.readObject(blobsFile, ArrayList.class);
        for (int i = 0; i < fileNames.size(); i++) {
            File curr = Utils.join(stageAdd, fileNames.get(i));
            Blob currBlob = Utils.readObject(curr, Blob.class);
            blobsList.add(currBlob);
            int blobIndex = blobsList.size() - 1;
            newCommit.addBlob(currBlob, blobIndex);
        }
        Utils.writeObject(blobsFile, blobsList);
        for (Object key : latestCommitBlobs.keySet()) {
            if (!newCommit.getBlobsMap().containsKey(key)) {
                int index = latestCommitBlobs.get(key);
                newCommit.addBlob(latestCommit.getBlob(key.toString()), index);
            }
        }
        String newKey = newCommit.generateKey();
        newCommit.addCommit(newKey);
        for(File file: stageAdd.listFiles()) {
            file.delete();
        }
        currentBranch.setHead(newCommit);
        // Test
        Branch newBranch = Repository.getCurrentBranch();
        System.out.println("Updated Branch");
        System.out.println(newBranch);
        HashMap<String,Commit> commitsMap = Utils.readObject(COMMITS, HashMap.class);
        System.out.println("Updated Commits Hash Map");
        System.out.println(commitsMap);
        ArrayList<Blob> newBlobsList = Utils.readObject(blobsFile, ArrayList.class);
        System.out.println(newBlobsList);
    }

    /** gitlet add - checks if file exists, then compares to latest commit's iteration and adds if different */
    public void add(String filePath) {
        File fileToAdd = Utils.join(CWD, filePath);
        String fileName = fileToAdd.getName();
        System.out.println(fileName);
        if (!fileToAdd.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        byte[] fileContent = Utils.readContents(fileToAdd);
        String fileContentString = Utils.readContentsAsString(fileToAdd);
        File addFile = Utils.join(stageAdd, fileName);
        Blob curr = new Blob(fileName, fileContent);
        Branch currentBranch = Repository.getCurrentBranch();
        Commit latestCommit = currentBranch.getCurrentCommit();
        System.out.println("Previous Commit Blobs: \n" + latestCommit.getBlobsMap().toString());
        if (latestCommit.getBlobsMap().containsKey(fileName)) {
            System.out.println(latestCommit.getBlob(fileName).getName());
            System.out.println(new String(latestCommit.getBlob(fileName).getByteArray(), StandardCharsets.UTF_8));
            System.out.println(fileContentString);
            if (!latestCommit.getBlob(fileName).compareBlob(fileContentString)) {
                System.out.println("Adding");
                Utils.writeObject(addFile, curr);
                return;
            }
        } else {
            Utils.writeObject(addFile, curr);
        }
    }

    public void log() {
        Branch currentBranch = Repository.getCurrentBranch();
        HashMap<String,Commit> commitHashMap = Utils.readObject(COMMITS, HashMap.class);
        Commit latestCommit = currentBranch.getCurrentCommit();
        while (latestCommit != null) {
            latestCommit.printCommitLog();
            System.out.println(commitHashMap);
            System.out.println(latestCommit.getParent());
            String parent = latestCommit.getParent();
            if (parent != null) {
                latestCommit = commitHashMap.get(parent);
            } else {
                latestCommit = null;
            }
        }
    }

    public void fileCheckout(String dash, String fileName) {

    }

    public void commitCheckout(String commitId, String dash, String fileName){

    }

    public void branchCheckout(String branchName) {

    }
}
