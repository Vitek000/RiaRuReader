package newscrawler.model;

public class RiaArticle {
    public String articleLink;
    private String title;
    private String date; // in format dd.MM.yyyy HH:mm
    public String imageLink;
    private long viewsCount;
    private boolean isNew = true;
    private boolean isSelected = false;

    private boolean needToRecheck = true;


    @Override public String toString() {
        return "RiaArticle{" +
                "articleLink='" + articleLink + '\'' +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", viewsCount=" + viewsCount +
                ", isNew=" + isNew +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        if(date.contains("Вчера, ")) {
            date = date.replace("Вчера, ", "");
        }
        this.date = date;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
    }


    public boolean isNeedToRecheck() {
        return needToRecheck;
    }

    public void setNeedToRecheck(boolean needToRecheck) {
        this.needToRecheck = needToRecheck;
    }
}