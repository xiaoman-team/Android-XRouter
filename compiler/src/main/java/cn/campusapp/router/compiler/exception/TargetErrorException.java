package cn.campusapp.router.compiler.exception;

/**
 * Created by kris on 16/4/20.
 */
public class TargetErrorException extends Exception {

    public TargetErrorException(){
        super("Annotated target error, it should annotate only class");
    }
}
