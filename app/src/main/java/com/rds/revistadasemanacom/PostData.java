package com.rds.revistadasemanacom;

/**
 * Created by brunomorais on 12/01/16.
 */
public class PostData {

    private String title;
    private String link;
    private String category;
    private String content;


    //Sample Content
    public static final PostData[] postData = {
            new PostData("miz é o hospedador", "http://www.miz.com.br", "Blog", "blá blá blá"),
            new PostData("Google é o buscador", "http://www.google.com.br", "Nacionais", "Blu, blu, blu")
    };

    //Constructor
    public PostData(String title, String link, String category, String content) {
        this.title = title;
        this.link = link;
        this.category = category;
        this.content = content;
    }

    //Getter
    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }
    public String getCategory() {
        return category;
    }

    public String getContent() {
        return content;
    }


    //toString()
    public String toString() {
        return this.title;
    }


}
