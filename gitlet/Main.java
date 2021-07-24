package gitlet;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Abhi Gudimella, Alex Tian
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");

    public static void main(String[] args) {
        //if args is empty
        if(args.length == 0) {
            System.out.println("Please enter a command");
            return;
        }
        //Still need write a case for Incorrect Operands and Wrong Gitlet Directory
        String firstArg = args[0];
        Command c = new Command();
        switch(firstArg) {
            case "init":
                if(args.length != 1) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.init();
                break;
            case "add":
                if(!GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    break;
                }
                if(args.length != 2) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.add(args[1]);
                break;
            case "commit":
                if(!GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    break;
                }
                if(args.length != 2) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.commit(args[1]);
                break;
            case "rm":
                if(!GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    break;
                }
                if(args.length != 2) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.removeFile(args[1]);
                break;
            case "log":
                if(!GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    break;
                }
                if(args.length != 1) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.log();
                break;
            case "global-log":
                if(!GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    break;
                }
                if(args.length != 1) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.logFull();
                break;
            case "find":
                if(!GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    break;
                }
                if(args.length != 2) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.find(args[1]);
                break;
            case "status":
                if(!GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    break;
                }
                if(args.length != 1) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.status();
                break;
            case "checkout":
                if(!GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    break;
                }
                if (args.length == 2) {
                    c.branchCheckout(args[1]);
                } else if (args.length == 3) {
                    if(!args[2].equals("--")) {
                        System.out.println("Incorrect Operands");
                        break;
                    }
                    c.fileCheckout(args[2]);
                } else if (args.length == 4){
                    if(!args[3].equals("--")) {
                        System.out.println("Incorrect Operands");
                        break;
                    }
                    c.commitCheckout(args[1], args[3]);
                } else {
                    System.out.println("Incorrect Operands");
                }
                break;
            case "branch":
                if(!GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    break;
                }
                if(args.length != 2) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.createBranch(args[1]);
                break;
            case "rm-branch":
                if(!GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    break;
                }
                if(args.length != 2) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.removeBranch(args[1]);
                break;
            case "reset":
                if(!GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    break;
                }
                if(args.length != 2) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.reset(args[1]);
                break;
            default:
                System.out.println("No command with that name exists");
        }
    }


}
