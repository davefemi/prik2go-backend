package nl.davefemi.prik2go.controller.exceptions;

@SuppressWarnings("serial")
public class BranchException extends Exception {
    public BranchException(){
        super();
    }
    public BranchException(String e){
        super(e);
    }
}
