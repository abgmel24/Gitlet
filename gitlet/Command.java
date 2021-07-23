package gitlet;

import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.List;

import static gitlet.Utils.*;

public class Command implements Serializable{

    //rm [file name]
    //checkout [branch name]
    //reset [commit id]

    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    public static final File COMMITS = join(GITLET_DIR, "commits.txt");
    public static final File stageAdd = Utils.join(GITLET_DIR, "stage/stageAdd");
    public static final File stageRm = Utils.join(GITLET_DIR, "stage/stageRm");
    public static final File BLOBS = Utils.join(GITLET_DIR, "blobList.txt");
    public static final File BRANCH_DIR = Utils.join(GITLET_DIR, "branches");
    public static final File STATE = Utils.join(GITLET_DIR, "state.txt");


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
        List<String> fileNamesAdd = Utils.plainFilenamesIn(stageAdd);
        List<String> fileNamesRm = Utils.plainFilenamesIn(stageRm);
        Branch currentBranch = Repository.getCurrentBranch();
        Commit latestCommit = currentBranch.getCurrentCommit();
        HashMap<String, Integer> latestCommitBlobs = latestCommit.getBlobsMap();
        Commit newCommit = new Commit(message, latestCommit.getKey(), new Date(), currentBranch.getName());
        File blobsFile = Utils.join(GITLET_DIR, "blobList.txt");
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
        List<String> filesInStageAdd = Utils.plainFilenamesIn(stageAdd);
        if (filesInStageAdd.contains(fileName)) {
            removeFile = Utils.join(stageAdd, fileName);
            removeFile.delete();
        }
        Branch currentBranch = Repository.getCurrentBranch();
        Commit latestCommit = currentBranch.getCurrentCommit();
        if (latestCommit.getBlobsMap().containsKey(fileName) && !filesInStageAdd.contains(fileName)) {
            removeFile = Utils.join(stageRm, fileName);
            Utils.writeContents(removeFile, fileName);
            removeFile = Utils.join(CWD, fileName);
            removeFile.delete();
        } else {
            System.out.println("No reason to remove the file.");
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
        if (!Utils.plainFilenamesIn(BRANCH_DIR).contains(branchName + ".txt")) {
            System.out.println("No such branch exists.");
            return;
        }
        HashMap<String,String> state = Utils.readObject(STATE, HashMap.class);
        if (state.get("currentBranch") == branchName + ".txt") {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Branch branch = Repository.getBranch(branchName);
        commitFullCheckout(branch.getCurrentCommitId());
        Repository.setCurrentBranch(branchName + ".txt");
    }

    public void commitFullCheckout(String commitId) {
        Branch currentBranch = Repository.getCurrentBranch();
        Commit latestCommit = currentBranch.getCurrentCommit();
        HashMap<String,Integer> currentBlobs = latestCommit.getBlobsMap();
        HashMap<String,Commit> commitsHashMap = Utils.readObject(COMMITS, HashMap.class);
        Commit currentCommit = commitsHashMap.get(commitId);
        HashMap<String,Integer> branchBlobs = currentCommit.getBlobsMap();
        ArrayList<Blob> blobsList = Utils.readObject(BLOBS, ArrayList.class);
        List<String> filesInStageAdd = Utils.plainFilenamesIn(stageAdd);
        for(File f: CWD.listFiles()) {
            if(!branchBlobs.containsKey(f.getName()) && !filesInStageAdd.contains(f.getName())) {
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
        if(Utils.plainFilenamesIn(BRANCH_DIR).contains(branchName + ".txt")) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        /** Create initial commit */
        File commit = Utils.join(GITLET_DIR, "commits.txt");
        HashMap<String,Commit> CommitsMap = Utils.readObject(commit, HashMap.class);
        Utils.writeObject(commit, CommitsMap);
        Commit init = new Commit("initial commit", null, new Date(0), branchName);
        init.addCommit(init.generateKey());
//        state.put("currentCommit", init.getKey());
        /** Create a new branch and set its head*/
        Branch branch = new Branch(branchName +".txt");
        branch.setCommitId(init.getKey());
        File branchFile = Utils.join(BRANCH_DIR, branch.getName());
        Utils.writeObject(branchFile, branch);
    }

    public void removeBranch(String branchName) {
        HashMap<String,String> state = Utils.readObject(STATE, HashMap.class);
        if(state.get("currentBranch").equals(branchName + ".txt")) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        File f = Utils.join(BRANCH_DIR, branchName + ".txt");
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
            String key = mapElement.getKey().toString();
            Commit commit = (Commit) mapElement.getValue();
            if(commit.getMessage().equals(message)) {
                condition = true;
            }
        }
        if(!condition) {
            System.out.println("Found no commit with that message.");
        }
    }

    public void reset(String commitId) {
        HashMap<String,Commit> commitHashMap = Utils.readObject(COMMITS, HashMap.class);
        if(!commitHashMap.containsKey(commitId)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        commitFullCheckout(commitId);
        Branch currentBranch = Repository.getCurrentBranch();
        currentBranch.setHead(commitId);
    }
}
