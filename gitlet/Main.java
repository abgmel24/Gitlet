package gitlet;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
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
                // TODO: handle the `init` command
                c.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                c.add(args[1]);
                break;
            case "commit":
                c.commit(args[1]);
                break;
            case "rm":

                break;
            case "log":
                c.log();
                break;
            case "global-log":

                break;
            case "find":

                break;
            case "status":

                break;
            case "checkout":
                if (args.length == 2) {
                    c.branchCheckout(args[1]);
                } else if (args.length == 3) {
                    c.fileCheckout(args[1], args[2]);
                } else if (args.length == 4){
                    c.commitCheckout(args[1], args[2], args[3]);
                }
                break;
            case "branch":

                break;
            case "rm-branch":

                break;
            case "reset":

                break;

            default:
                System.out.println("No command with that name exists");
            // TODO: FILL THE REST IN
        }
    }


}
