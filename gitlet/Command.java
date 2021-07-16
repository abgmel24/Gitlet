package gitlet;

import java.io.File;

public class Command {

    public void init() {
        File curr = new File(System.getProperty("user.dir"));
        Commit initial = new Commit("initial commit", null);
    }

    public void commit() {

    }
}
