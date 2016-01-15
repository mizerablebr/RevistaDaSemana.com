package com.rds.revistadasemanacom;

/**
 * Created by brunomorais on 12/01/16.
 */
public class PostData {

    private String title;
    private String link;

    //Sample Content
    public static final PostData[] postData = {
            new PostData("miz é o hospedador", "http://www.miz.com.br"),
            new PostData("Google é o buscador", "http://www.google.com.br")
    };

    //Constructor
    public PostData(String title, String link) {
        this.title = title;
        this.link = link;
    }

    //Getter
    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    //toString()
    public String toString() {
        return this.title;
    }


}
