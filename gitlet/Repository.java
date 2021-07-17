package gitlet;

import java.io.File;
import java.util.ArrayList;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** -gitlet
     *      -other files
     *
     *      -Master
     *          -commits
     */
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    private static Branch currentBranch;

    /* TODO: fill in the rest of this class. */
    public Repository() {
        File repo = Utils.join(GITLET_DIR, "gitletRepo");
        if (!repo.exists()) {
            repo.mkdir();
            Branch master = new Branch("master");
        }
    }
}
