package augsburg.se.alltagsguide.article;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Article;
import augsburg.se.alltagsguide.utilities.BaseActivity;

public class ArticleActivity extends BaseActivity {
    public static final String ARG_INFO = "info";

    private Article mArticle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        mArticle = (Article) getIntent().getSerializableExtra(ARG_INFO);
        setTitle(mArticle.getTitle());

        TextView desriptionView = (TextView) findViewById(R.id.description);
        desriptionView.setText(mArticle.getDescription());

        ImageView imageView = (ImageView) findViewById(R.id.image);
        //TODO
    }

    protected boolean setDisplayHomeAsUp() {
        return true;
    }
}
