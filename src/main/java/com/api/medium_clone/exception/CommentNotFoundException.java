package com.api.medium_clone.exception;

public class CommentNotFoundException extends RuntimeException{

    public CommentNotFoundException(String message){
        super(message);
    }
}
