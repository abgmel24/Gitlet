package gitlet;

import java.io.File;

import static gitlet.Utils.join;

public class Command {

    public void init() {
        File curr = new File(System.getProperty("user.dir"));
        File repo = Utils.join(curr, ".gitlet/gitletRepo");
        if (repo.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        Commit initial = new Commit("initial commit", null);
        Repository newRepo = new Repository();
    }

    public void commit() {

    }

    public void add() {

    }
}
