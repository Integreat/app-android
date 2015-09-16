package augsburg.se.alltagsguide.article;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.category.OverviewActivity;
import augsburg.se.alltagsguide.common.Article;
import augsburg.se.alltagsguide.utilities.BaseActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_article)
public class ArticleActivity extends BaseActivity {
    public static final String ARG_INFO = "info";


    @InjectView(R.id.description)
    private TextView descriptionView;

    @InjectView(R.id.image)
    private ImageView imageView;

    private Article mArticle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mArticle = (Article) getIntent().getSerializableExtra(ARG_INFO);
        setTitle(mArticle.getTitle());

        descriptionView.setText(mArticle.getDescription());
        //TODO
    }

    protected boolean setDisplayHomeAsUp() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
