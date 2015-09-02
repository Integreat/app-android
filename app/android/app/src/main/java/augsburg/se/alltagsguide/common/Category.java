package augsburg.se.alltagsguide.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class Category implements Serializable, Comparable {
    List<Category> mSubCategories;
    List<Article> mArticle;

    String mTitle;
    String mDescription;

    public Category(String title, String description, List<Article> article, List<Category> subCategories) {
        mTitle = title;
        mDescription = description;
        mArticle = article;
        mSubCategories = subCategories;
    }

    public Category(String title, String description) {
        this(title, description, null, null);
    }

    public List<Category> getSubContent() {
        return mSubCategories;
    }

    public List<Article> getArticles() {
        if (mArticle == null) {
            mArticle = new ArrayList<>();
        }
        return mArticle;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public int countItems() {
        int count = getArticles().size();
        if (mSubCategories != null && !mSubCategories.isEmpty()) {
            for (Category category : mSubCategories) {
                count += category.countItems();
            }
        }
        return count;
    }

    @Override
    public int compareTo(Object another) {
        return 0;
    }
}

