package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.join;
import static gitlet.Utils.plainFilenamesIn;

public class Command implements Serializable{

    //rm [file name]
    //global-log
    //find [commit message]
    //status
    //checkout [branch name]
    //branch [branch name]
    //rm-branch [branch name]
    //reset [commit id]

    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    public static final File COMMITS = join(GITLET_DIR, "commits.txt");
    public static final File stageAdd = Utils.join(GITLET_DIR, "stage/stageAdd");
    public static final File stageRm = Utils.join(GITLET_DIR, "stage/stageRm");
    public static final File BLOBS = Utils.join(GITLET_DIR, "blobList.txt");
    public static final File BRANCH_DIR = Utils.join(GITLET_DIR, "branches");


    public void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        Repository newRepo = new Repository();
        System.out.println(plainFilenamesIn(CWD).toString());
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
            Utils.writeContents(fileToCheckout, blobCurrent.getFileContent());
        } else {
            System.out.println("File does not exist in that commit.");
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
            Blob blobCurrent = blobsList.get(commitBlobs.get(fileName));
            Utils.writeContents(fileToCheckout, blobCurrent.getFileContent());
        } else {
            System.out.println("File does not exist in that commit.");
            return;
        }
        return;
    }

    public void status() {
        //Branches
        System.out.println("=== Branches ====") ;
        List<String> list = Utils.plainFilenamesIn(BRANCH_DIR);
        for(String s: list) {
            if(s.equals(Repository.getCurrentBranch().getName())) {
                System.out.println("*" + s);
            } else {
                System.out.println(s);
            }
        }
        //Staged Files
        System.out.println("\n=== Staged Files ===");
        list = Utils.plainFilenamesIn(stageAdd);
        for(String s: list) {
            System.out.println(s);
        }
        //Removed Files
        System.out.println("\n===Removed Files===");
        list = plainFilenamesIn(stageRm);
        for(String s: list) {
            System.out.println(s);
        }
        //Modifications Not Staged for Commit

        //Untracked Files

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
