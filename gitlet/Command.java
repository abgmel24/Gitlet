package gitlet;

import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.List;

import static gitlet.Utils.*;

public class Command implements Serializable{


    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    public static final File COMMITS = join(GITLET_DIR, "commits");
    public static final File stageAdd = Utils.join(GITLET_DIR, "stage/stageAdd");
    public static final File stageRm = Utils.join(GITLET_DIR, "stage/stageRm");
    public static final File BLOBS = Utils.join(GITLET_DIR, "blobList");
    public static final File BRANCH_DIR = Utils.join(GITLET_DIR, "branches");
    public static final File STATE = Utils.join(GITLET_DIR, "state");


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
        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            return;
        }
        List<String> fileNamesAdd = Utils.plainFilenamesIn(stageAdd);
        List<String> fileNamesRm = Utils.plainFilenamesIn(stageRm);
        if (fileNamesRm.isEmpty() && fileNamesAdd.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }

        Branch currentBranch = Repository.getCurrentBranch();
        Commit latestCommit = currentBranch.getCurrentCommit();
        HashMap<String, Integer> latestCommitBlobs = latestCommit.getBlobsMap();
        Commit newCommit = new Commit(message, latestCommit.getKey(), new Date(), currentBranch.getName());
        File blobsFile = Utils.join(GITLET_DIR, "blobList");
        ArrayList<Blob> blobsList = Utils.readObject(blobsFile, ArrayList.class);
        for (int i = 0; i < fileNamesAdd.size(); i++) {
            File curr = Utils.join(stageAdd, fileNamesAdd.get(i));
            Blob currBlob = Utils.readObject(curr, Blob.class);
            blobsList.add(currBlob);
            int blobIndex = blobsList.size() - 1;
            newCommit.addBlob(currBlob, blobIndex);
        }
        Utils.writeObject(blobsFile, blobsList);
        for (Object key : latestCommitBlobs.keySet()) {
            if (!newCommit.getBlobsMap().containsKey(key)) {
                if (!fileNamesRm.contains(key)) {
                    int index = latestCommitBlobs.get(key);
                    newCommit.addBlob(latestCommit.getBlob(key.toString()), index);
                }
            }
        }
        String newKey = newCommit.generateKey();
        newCommit.addCommit(newKey);
        clearStagingArea();
        currentBranch.setHead(newCommit.getKey());
    }

    /** gitlet add - checks if file exists, then compares to latest commit's iteration and adds if different */
    public void add(String filePath) {
        List<String> fileNamesRm = Utils.plainFilenamesIn(stageRm);
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
            if(fileNamesRm.contains(fileName)) {
                File f = Utils.join(stageRm, fileName);
                f.delete();
            }
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
            String parent = latestCommit.getParent();
            latestCommit.printCommitLog();
            if(parent != null) {
                latestCommit = commitHashMap.get(parent);
            } else {
                latestCommit = null;
            }
        }
    }

    public void logFull() {
        HashMap<String,Commit> commitHashMap = Utils.readObject(COMMITS, HashMap.class);
        for(Map.Entry mapElement: commitHashMap.entrySet()) {
            String key = mapElement.getKey().toString();
            commitHashMap.get(key).printCommitLog();
        }
    }

    public void removeFile(String fileName) {
        File removeFile;
        Branch currentBranch = Repository.getCurrentBranch();
        Commit latestCommit = currentBranch.getCurrentCommit();
        List<String> filesInStageAdd = Utils.plainFilenamesIn(stageAdd);
        /**if file is not tracked by previous commit and not staged for addition*/
        if(!latestCommit.getBlobsMap().containsKey(fileName) && !filesInStageAdd.contains(fileName)) {
            System.out.println("No reason to remove the file.");
            return;
        }
        if (filesInStageAdd.contains(fileName)) {
            removeFile = Utils.join(stageAdd, fileName);
            removeFile.delete();
        }
        if(latestCommit.getBlobsMap().containsKey(fileName)) {
            removeFile = Utils.join(stageRm, fileName);
            Utils.writeContents(removeFile, fileName);
            removeFile = Utils.join(CWD, fileName);
            if(removeFile.exists()) {
                removeFile.delete();
            }
        }
    }

    public void fileCheckout(String fileName) {
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

    public void commitCheckout(String commitId, String fileName){
        commitId = findCommitID(commitId);
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
        System.out.println("=== Branches ===") ;
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
        System.out.println("\n=== Removed Files ===");
        list = plainFilenamesIn(stageRm);
        for(String s: list) {
            System.out.println(s);
        }
        //Modifications Not Staged for Commit
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        //Untracked Files
        System.out.println("\n=== Untracked Files ===\n");
    }

    public void branchCheckout(String branchName) {
        if (!Utils.plainFilenamesIn(BRANCH_DIR).contains(branchName)) {
            System.out.println("No such branch exists.");
            return;
        }
        HashMap<String,String> state = Utils.readObject(STATE, HashMap.class);
        if (state.get("currentBranch") == branchName) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Branch branch = Repository.getBranch(branchName);
        commitFullCheckout(branch.getCurrentCommitId());
        Repository.setCurrentBranch(branchName);
    }

    public void commitFullCheckout(String commitId) {
        HashMap<String,Commit> commitsHashMap = Utils.readObject(COMMITS, HashMap.class);
        commitId = findCommitID(commitId);
        Branch currentBranch = Repository.getCurrentBranch();
        Commit latestCommit = currentBranch.getCurrentCommit();
        HashMap<String,Integer> currentBlobs = latestCommit.getBlobsMap();
        Commit currentCommit = commitsHashMap.get(commitId);
        HashMap<String,Integer> branchBlobs = currentCommit.getBlobsMap();
        ArrayList<Blob> blobsList = Utils.readObject(BLOBS, ArrayList.class);
        List<String> CWDFiles = Utils.plainFilenamesIn(CWD);
        //System.out.println(CWDFiles);
        for(String s: CWDFiles) {
            //System.out.println("file name: " + s);
            if(isUntracked(s) && branchBlobs.containsKey(s)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }
        for (Object key: currentBlobs.keySet()) {
            if (!branchBlobs.containsKey(key)) {
                File f = Utils.join(CWD, (String) key);
                f.delete();
            }
        }
        for (Object key : branchBlobs.keySet()) {
            Blob blobCurrent = blobsList.get(branchBlobs.get(key));
            File x = Utils.join(CWD, blobCurrent.getName());
            Utils.writeContents(x, blobCurrent.getFileContent());
        }
        clearStagingArea();
    }

    public void createBranch(String branchName) {
        if(Utils.plainFilenamesIn(BRANCH_DIR).contains(branchName)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        /** Create a new branch and set its head*/
        Branch branch = new Branch(branchName);
        Branch currentBranch = Repository.getCurrentBranch();
        branch.setCommitId(currentBranch.getCurrentCommitId());
        File branchFile = Utils.join(BRANCH_DIR, branch.getName());
        Utils.writeObject(branchFile, branch);
    }

    public void removeBranch(String branchName) {
        HashMap<String,String> state = Utils.readObject(STATE, HashMap.class);
        if(state.get("currentBranch").equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        File f = Utils.join(BRANCH_DIR, branchName);
        if(!f.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else {
            f.delete();
        }
    }

    public void clearStagingArea() {
        for(File file: stageAdd.listFiles()) {
            file.delete();
        }
        for(File file: stageRm.listFiles()) {
            file.delete();
        }
    }

    public void find(String message) {
        HashMap<String,Commit> commitHashMap = Utils.readObject(COMMITS, HashMap.class);
        boolean condition = false;
        for(Map.Entry mapElement: commitHashMap.entrySet()) {
            Commit commit = (Commit) mapElement.getValue();
            if(commit.getMessage().equals(message)) {
                condition = true;
                System.out.println(commit.getKey());
            }
        }
        if(!condition) {
            System.out.println("Found no commit with that message.");
        }
    }

    public void reset(String commitId) {
        commitId = findCommitID(commitId);
        HashMap<String,Commit> commitHashMap = Utils.readObject(COMMITS, HashMap.class);
        if(!commitHashMap.containsKey(commitId)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        commitFullCheckout(commitId);
        Branch currentBranch = Repository.getCurrentBranch();
        currentBranch.setHead(commitId);
    }

    public boolean isUntracked(String fileName) {
        Branch currentBranch = Repository.getCurrentBranch();
        Commit latestCommit = currentBranch.getCurrentCommit();
        List<String> filesInStageAdd = Utils.plainFilenamesIn(stageAdd);
        File file = Utils.join(CWD, fileName);
        File removalFile = Utils.join(stageRm, fileName);
        if(!latestCommit.getBlobsMap().containsKey(fileName) && !filesInStageAdd.contains(fileName)) { //
            return true;
        }
        if(removalFile.exists() && file.exists()) {
            return true;
        }
        return false;
    }

    public String findCommitID(String commitId) {
        if(commitId.length() == 40) {
            return commitId;
        }
        HashMap<String,Commit> commitsHashMap = Utils.readObject(COMMITS, HashMap.class);
        for(Map.Entry mapElement : commitsHashMap.entrySet())
        {
            String key = mapElement.getKey().toString();
            if (key.startsWith(commitId)) {
                return key;
            }
        }
        return commitId;
    }
}
