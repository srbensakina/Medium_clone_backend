package com.api.medium_clone.exception;

public class CommentAccessDeniedException extends RuntimeException{

    public CommentAccessDeniedException(String message){
        super(message);
    }
}
