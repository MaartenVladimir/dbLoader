import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Activity {
    String title;
    long consumption_in_wh;
    String source;
    String image_path;

    public Activity() {
    }

    public void setConsumption_in_wh(long consumption_in_wh) {
        this.consumption_in_wh = consumption_in_wh;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getConsumption_in_wh() {
        return consumption_in_wh;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "title='" + title + '\'' +
                ", consumption_in_wh=" + consumption_in_wh +
                ", source='" + source + '\'' +
                ", imageSource='" + image_path + '\'' +
                '}';
    }
}
