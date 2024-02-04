package com.api.medium_clone.exception;

public class ArticleAlreadyFavoritedException extends RuntimeException{

    public ArticleAlreadyFavoritedException(String message){
        super(message);
    }
}
