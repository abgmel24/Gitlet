package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static gitlet.Utils.*;

public class StagingArea {

    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");

    public StagingArea() {
        File STAGE = Utils.join(GITLET_DIR, "stage");
        STAGE.mkdir();
        File STAGE_ADD = Utils.join(STAGE, "stageAdd");
        File STAGE_RM = Utils.join(STAGE, "stageRm");
        STAGE_ADD.mkdir();
        STAGE_RM.mkdir();
    }

}
