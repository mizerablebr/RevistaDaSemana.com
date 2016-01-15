package com.rds.revistadasemanacom;

/**
 * Created by brunomorais on 12/01/16.
 */
public class PostData {

    private String title;
    private String link;
    private String category;


    //Sample Content
    public static final PostData[] postData = {
            new PostData("miz é o hospedador", "http://www.miz.com.br", "Blog"),
            new PostData("Google é o buscador", "http://www.google.com.br", "Nacionais")
    };

    //Constructor
    public PostData(String title, String link, String category) {
        this.title = title;
        this.link = link;
        this.category = category;
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


    //toString()
    public String toString() {
        return this.title;
    }


}
