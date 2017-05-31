package com.nafeo.www.barivara.Classes;

/**
 * Created by Friends on 5/29/2017.
 */

public class ImageUpload {

    public String name;
    public String url;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public ImageUpload(String name, String url){
        this.name = name;
        this.url = url;
    }
}
