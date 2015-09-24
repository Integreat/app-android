package augsburg.se.alltagsguide.persistence.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.List;

import augsburg.se.alltagsguide.common.Article;
import augsburg.se.alltagsguide.common.Category;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.persistence.CacheHelper;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class ArticleResource implements PersistableResource<Article> {

    /**
     * Creation factory
     */
    public interface Factory {
        ArticleResource under(Category cat, Language lang, Location loc);
    }

    private final Category mCategory;
    private final Language mLanguage;
    private final Location mLocation;

    private NetworkService mNetwork;

    @Inject
    public ArticleResource(@Assisted Category category,
                           @Assisted Language language,
                           @Assisted Location location,
                           NetworkService network) {
        mCategory = category;
        mLanguage = language;
        mLocation = location;
        mNetwork = network;
    }

    @Override
    public Cursor getCursor(SQLiteDatabase readableDatabase) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_ARTICLE);
        return builder.query(readableDatabase, new String[]{},
                CacheHelper.ARTICLE_CATEGORY + "=?",
                new String[]{String.valueOf(mCategory.getId())}, null, null,
                null);
    }

    @Override
    public Article loadFrom(Cursor cursor) {
        Article article = new Article();
        article.setId(cursor.getInt(0));
        article.setTitle(cursor.getString(1));
        article.setSummary(cursor.getString(2));
        article.setDescription(cursor.getString(3));
        article.setImage(cursor.getString(4));
        article.setUrl(cursor.getString(5));
        article.setCategory(mCategory);
        article.setLanguage(mLanguage);
        article.setLocation(mLocation);

        return article;
    }

    @Override
    public void store(SQLiteDatabase db, List<Article> articles) {
        if (articles.isEmpty()) {
            return;
        }
        //db.delete(CacheHelper.TABLE_ARTICLE, null, null); //TODO dont remove everything

        ContentValues values = new ContentValues(6);
        for (Article article : articles) {
            values.clear();
            values.put(CacheHelper.ARTICLE_ID, article.getId());
            values.put(CacheHelper.ARTICLE_TITLE, article.getTitle());
            values.put(CacheHelper.ARTICLE_SUMMARY, article.getSummary());
            values.put(CacheHelper.ARTICLE_DESCRIPTION, article.getDescription());
            values.put(CacheHelper.ARTICLE_IMAGE, article.getImage());
            values.put(CacheHelper.ARTICLE_URL, article.getUrl());
            values.put(CacheHelper.ARTICLE_CATEGORY, mCategory.getId());
            values.put(CacheHelper.ARTICLE_LOCATION, mLocation.getName());
            values.put(CacheHelper.ARTICLE_LANGUAGE, mLanguage.getShortName());

            db.replace(CacheHelper.TABLE_ARTICLE, null, values);
        }
    }

    @Override
    public List<Article> request() {
        return mNetwork.getContent(mLanguage, mLocation, "");
    }
}
