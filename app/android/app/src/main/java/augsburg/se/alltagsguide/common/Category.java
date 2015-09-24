package augsburg.se.alltagsguide.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class Category implements Serializable, Comparable {
    List<Category> mSubCategories;
    List<Article> mArticles;

    String mTitle;
    String mDescription;
    Location mLocation;
    Language mLanguage;
    Category parent;
    int id;
    private int mDepth = 0;

    public List<Category> getSubCategories() {
        return mSubCategories;
    }

    public List<Category> getSubCategoriesRecursive() {
        List<Category> categories = new ArrayList<>();
        if (mSubCategories != null) {
            categories.addAll(mSubCategories);
            for (Category cat : mSubCategories) {
                categories.addAll(cat.getSubCategoriesRecursive());
            }
        }
        return categories;
    }

    public void setSubCategories(List<Category> subCategories) {
        mSubCategories = subCategories;
    }

    public List<Article> getArticle() {
        return mArticles;
    }

    public void setArticle(List<Article> mArticle) {
        this.mArticles = mArticle;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location mLocation) {
        this.mLocation = mLocation;
    }

    public Language getLanguage() {
        return mLanguage;
    }

    public void setLanguage(Language mLanguage) {
        this.mLanguage = mLanguage;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Category() {
    }

    public Category(int id, String title, String description, List<Article> article, List<Category> subCategories, int depth) {
        mTitle = title;
        mDescription = description;
        mArticles = article;
        mSubCategories = subCategories;
        mDepth = depth;
        this.id = id;
    }

    public Category(int id, String title, String description, int depth) {
        this(id, title, description, null, null, depth);
    }

    public List<Article> getArticles() {
        if (mArticles == null) {
            mArticles = new ArrayList<>();
        }
        return mArticles;
    }

    public List<Article> getArticlesRecursive() {
        List<Article> articles = getArticles();
        if (getSubCategories() != null) {
            for (Category subCategory : getSubCategories()) {
                articles.addAll(subCategory.getArticlesRecursive());
            }
        }
        return articles;
    }

    public int countItems() {
        int count = getArticles().size();
        if (mSubCategories != null) {
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

    public Category getCategoryByArticle(Article article) {
        if (mArticles.indexOf(article) > -1) {
            return this;
        }
        if (mSubCategories != null) {
            for (Category category : mSubCategories) {
                if (category.getCategoryByArticle(article) != null) {
                    return category;
                }
            }
        }
        return null;
    }

    public int getDepth() {
        return mDepth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        return !(mTitle != null ? !mTitle.equals(category.mTitle) : category.mTitle != null) && !(mDescription != null ? !mDescription.equals(category.mDescription) : category.mDescription != null);

    }

    @Override
    public int hashCode() {
        int result = mTitle != null ? mTitle.hashCode() : 0;
        result = 31 * result + (mDescription != null ? mDescription.hashCode() : 0);
        return result;
    }
}

