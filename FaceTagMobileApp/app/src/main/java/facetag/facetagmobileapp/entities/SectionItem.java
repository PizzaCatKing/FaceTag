package facetag.facetagmobileapp.entities;

/**
 * Created by Chris_2 on 2015-08-02.
 */
public class SectionItem implements ListItem {
    private String title;

    public SectionItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean isSection() {
        return true;
    }
}
