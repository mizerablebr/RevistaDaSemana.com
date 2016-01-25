package com.rds.revistadasemanacom;

/**
 * Created by brunomorais on 12/01/16.
 */
public class PostData {

    private String title;
    private String link;
    private String category;
    private String content;
    private String read;

    //Sample Content
    public static final PostData[] postData = {
            new PostData("miz é o hospedador", "http://www.miz.com.br", "Blog", "blá blá blá", "not"),
            new PostData("Google é o buscador", "http://www.google.com.br", "Nacionais", "Blu, blu, blu", "not")
    };

    //Constructor
    public PostData(String title, String link, String category, String content, String readed) {
        this.title = title;
        this.link = link;
        this.category = category;
        this.content = content;
        this.read = readed;
    }
    public PostData(String title, String link, String category, String content) {
        this.title = title;
        this.link = link;
        this.category = category;
        this.content = content;
        this.read = "not";
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
    public String getRead() {
        return read;
    }



    //toString()
    public String toString() {
        return this.title;
    }

    //Overrides equal() and hashCode()
    public boolean equals(Object aPostData) {
        PostData pD = (PostData) aPostData;
        return getTitle().equals(pD.getTitle());
    }

    public int hashCode() {
        return title.hashCode();
    }


}
