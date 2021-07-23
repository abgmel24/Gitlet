package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Abhi Gudimella, Alex Tian
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */

    public static void main(String[] args) {
        // TODO: what if args is empty?
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
                if(args.length != 2) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.add(args[1]);
                break;
            case "commit":
                if(args.length != 2) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.commit(args[1]);
                break;
            case "rm":
                if(args.length != 2) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.removeFile(args[1]);
                break;
            case "log":
                if(args.length != 1) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.log();
                break;
            case "global-log":
                if(args.length != 1) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.logFull();
                break;
            case "find":
                if(args.length != 2) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.find(args[1]);
                break;
            case "status":
                if(args.length != 1) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.status();
                break;
            case "checkout":
                if (args.length == 2) {
                    c.branchCheckout(args[1]);
                } else if (args.length == 3) {
                    c.fileCheckout(args[1], args[2]);
                } else if (args.length == 4){
                    c.commitCheckout(args[1], args[2], args[3]);
                } else {
                    System.out.println("Incorrect Operands");
                }
                break;
            case "branch":
                if(args.length != 2) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.createBranch(args[1]);
                break;
            case "rm-branch":
                if(args.length != 2) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.removeBranch(args[1]);
                break;
            case "reset":
                if(args.length != 2) {
                    System.out.println("Incorrect Operands");
                    break;
                }
                c.reset(args[1]);
                break;
            case "":
                System.out.println("Please enter a command");
            default:
                System.out.println("No command with that name exists");
            // TODO: FILL THE REST IN
        }
    }


}
