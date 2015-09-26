package augsburg.se.alltagsguide.page;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.utilities.BaseActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_page)
public class PageActivity extends BaseActivity {
    public static final String ARG_INFO = "info";


    @InjectView(R.id.description)
    private WebView descriptionView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Page mPage = (Page) getIntent().getSerializableExtra(ARG_INFO);
        setTitle(mPage.getTitle());
        descriptionView.getSettings().setDefaultTextEncodingName("utf-8");
        descriptionView.loadDataWithBaseURL("file:///android_asset", convertContent(mPage.getContent()), "text/html", "charset=utf-8", null);
    }

    private String convertContent(String content) {
        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("<head>");
        builder.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0\">");
        builder.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />");
        builder.append("</head>");
        builder.append("<body>");
        builder.append("<div class=\"container-fluid\">");
        builder.append(formatContent(content));
        builder.append("</div>");
        builder.append("</body>");
        builder.append("</html>");
        return builder.toString();
    }

    private String formatContent(String content) {
        return content.replaceAll("<table", "<table class=\"table\"");
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
