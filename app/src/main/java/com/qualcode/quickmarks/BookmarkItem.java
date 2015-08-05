package com.qualcode.quickmarks;

public class BookmarkItem implements Comparable<BookmarkItem> {

    private String title, url;
    private byte[] favicon;

    public String getTitle() {
        return title;
    }
    public byte[] getFavicon() {
        return favicon;
    }
    public String getUrl() { return url; }

    public void setTitle(final String title) {
        this.title = title.trim();
    }
    public void setFavicon(final byte[] favicon) {
        this.favicon = favicon;
    }
    public void setUrl(final String url)  {
        this.url = url.trim();
    }

    public BookmarkItem copy(){
        final BookmarkItem copy = new BookmarkItem();
        copy.title = title;
        copy.url = url;
        copy.favicon = favicon;
        return copy;
    }

    public int compareTo(final BookmarkItem another) {
        if (another == null) return 1;
        // sort descending, most recent first
        return another.title.compareTo(title);
    }
}
