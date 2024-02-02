package com.api.medium_clone.exception;

public class ArticleNotFoundException extends RuntimeException{

    public ArticleNotFoundException(String message){
        super(message);
    }
}
