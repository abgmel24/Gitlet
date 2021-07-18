package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Command {

    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
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

    public void commit(String message) {
        List<String> fileNames = Utils.plainFilenamesIn(stageAdd);
        Branch currentBranch = Repository.getCurrentBranch();
        Commit latestCommit = currentBranch.getCurrentCommit();
        HashMap<String, Integer> latestCommitBlobs = latestCommit.getBlobsMap();
        Commit newCommit = new Commit(message, latestCommit, new Date());

        File blobsFile = Utils.join(GITLET_DIR, "blobs.txt");
        ArrayList<Blob> blobsList = Utils.readObject(blobsFile, ArrayList.class);
        for (Object key : latestCommitBlobs.keySet()) {
            int index = latestCommitBlobs.get(key);
            newCommit.addBlob(latestCommit.getBlob(key.toString()), index);
        }
        for (int i = 0; i < fileNames.size(); i++) {
            File curr = Utils.join(stageAdd, fileNames.get(i));
            Blob currBlob = Utils.readObject(curr, Blob.class);
            blobsList.add(currBlob);
            int blobIndex = blobsList.size() - 1;
            newCommit.addBlob(currBlob, blobIndex);
        }
        String newKey = newCommit.generateKey();
        newCommit.addCommit(newKey);
        for(File file: stageAdd.listFiles()) {
            file.delete();
        }
        currentBranch.setHead(newCommit);
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
        File addFile = Utils.join(stageAdd, fileName);
        Blob curr = new Blob(fileName, fileContent);
        Branch currentBranch = Repository.getCurrentBranch();
        Commit latestCommit = currentBranch.getCurrentCommit();
        if (latestCommit.getBlobsMap().containsKey(fileName)) {
            if (!latestCommit.getBlob(fileName).compareBlob(fileContent)) {
                Utils.writeObject(addFile, curr);
                return;
            }
        }
        Utils.writeObject(addFile, curr);
    }

    public void checkout() {

    }


}
