package augsburg.se.alltagsguide.page;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.utilities.BaseActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_article)
public class PageActivity extends BaseActivity {
    public static final String ARG_INFO = "info";


    @InjectView(R.id.description)
    private HtmlTextView descriptionView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Page mPage = (Page) getIntent().getSerializableExtra(ARG_INFO);
        setTitle(mPage.getTitle());
        descriptionView.setHtmlFromString(mPage.getContent(), new HtmlTextView.RemoteImageGetter());
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
