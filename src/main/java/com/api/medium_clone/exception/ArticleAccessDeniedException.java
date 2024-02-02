package com.api.medium_clone.exception;

public class ArticleAccessDeniedException extends RuntimeException{

    public ArticleAccessDeniedException(String message){
        super(message);
    }
}
