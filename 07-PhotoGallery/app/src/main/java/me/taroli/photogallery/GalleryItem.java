package me.taroli.photogallery;

/**
 * Created by Matt on 24/07/15.
 */
public class GalleryItem {

    private String caption;
    private String id;
    private String url;
    private String owner;

    @Override
    public String toString() {
        return caption;
    }

    public String getCaption() {
        return caption;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPhotoPageUrl() {
        return "http://www.flickr.com/photos/" + owner + "/" + id;
    }
}