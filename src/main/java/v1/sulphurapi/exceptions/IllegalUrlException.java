package v1.sulphurapi.exceptions;

public class IllegalUrlException extends Exception{
    public IllegalUrlException(){
        super("Variable Containing URL is Null or Empty.");
    }
}
