package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static gitlet.Utils.join;

public class Command implements Serializable{

    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    public static final File COMMITS = join(GITLET_DIR, "commits.txt");
    public static final File stageAdd = Utils.join(GITLET_DIR, "stage/stageAdd");
    public static final File BLOBS = Utils.join(GITLET_DIR, "blobList.txt");

    public void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        Repository newRepo = new Repository();
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
//        Branch newBranch = Repository.getCurrentBranch();
//        System.out.println("Updated Branch");
//        System.out.println(newBranch);
//        HashMap<String,Commit> commitsMap = Utils.readObject(COMMITS, HashMap.class);
//        System.out.println("Updated Commits Hash Map");
//        System.out.println(commitsMap);
//        ArrayList<Blob> newBlobsList = Utils.readObject(blobsFile, ArrayList.class);
//        System.out.println(newBlobsList);
    }

    /** gitlet add - checks if file exists, then compares to latest commit's iteration and adds if different */
    public void add(String filePath) {
        File fileToAdd = Utils.join(CWD, filePath);
        String fileName = fileToAdd.getName();
        if (!fileToAdd.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        byte[] fileContentBytes = Utils.readContents(fileToAdd);
        String fileContentString = Utils.readContentsAsString(fileToAdd);
        File addFile = Utils.join(stageAdd, fileName);
        Blob curr = new Blob(fileName, fileContentBytes, fileContentString);
        Branch currentBranch = Repository.getCurrentBranch();
        Commit latestCommit = currentBranch.getCurrentCommit();
        if (latestCommit.getBlobsMap().containsKey(fileName)) {
            if (!latestCommit.getBlob(fileName).compareBlob(fileContentString)) {
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
            String parent = latestCommit.getParent();
            if (parent != null) {
                latestCommit = commitHashMap.get(parent);
            } else {
                latestCommit = null;
            }
        }
    }

    public void fileCheckout(String dash, String fileName) {
        File fileToCheckout = new File(CWD, fileName); //current version of the file
        Branch currBranch = Repository.getCurrentBranch();
        Commit latestCommit = currBranch.getCurrentCommit();
        ArrayList<Blob> blobsList = Utils.readObject(BLOBS, ArrayList.class);
        HashMap<String,Integer> commitBlobs = latestCommit.getBlobsMap();
        if(commitBlobs.containsKey(fileName)) {
            Blob blobCurrent = blobsList.get(commitBlobs.get(fileName));
            String latestFileContents = blobCurrent.getFileContent();
            Utils.writeContents(fileToCheckout, latestFileContents);
        } else {
            System.out.println("File does not exist in that commit");
            return;
        }
    }

    public void commitCheckout(String commitId, String dash, String fileName){
        File fileToCheckout = new File(CWD, fileName); //current version of the file

        Branch currBranch = Repository.getCurrentBranch();
        HashMap<String,Commit> commitsHashMap = Utils.readObject(COMMITS, HashMap.class);
        if (!commitsHashMap.containsKey(commitId)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit latestCommit = currBranch.getCommit(commitId);
        ArrayList<Blob> blobsList = Utils.readObject(BLOBS, ArrayList.class);
        HashMap<String,Integer> commitBlobs = latestCommit.getBlobsMap();
        if(commitBlobs.containsKey(fileName)) {
            Blob blobCurrent = blobsList.get(commitBlobs.get(fileName)); //old version of the file
            Utils.writeObject(fileToCheckout, blobCurrent);
        } else {
            System.out.println("File does not exist in that commit.");
            return;
        }
        return;
    }

    public void branchCheckout(String branchName) {
        Branch branch = Repository.getBranch(branchName);
        Commit branchCommit = branch.getCurrentCommit();
        HashMap<String, Integer> branchBlobs = branchCommit.getBlobsMap();
        ArrayList<Blob> blobsList = Utils.readObject(BLOBS, ArrayList.class);
        for(File f: CWD.listFiles()) {
            //if(untracked) {
            //  System,out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            //  return;
            // }
            //f.delete();
        }
        for (Object key : branchBlobs.keySet()) {
            Blob blobCurrent = blobsList.get(branchBlobs.get(key));
            File x = Utils.join(CWD, blobCurrent.getName());
            Utils.writeObject(x, blobCurrent.getByteArray());
        }
    }
}
