package augsburg.se.alltagsguide.event;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.AvailableLanguage;
import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.page.LanguageItemAdapter;
import augsburg.se.alltagsguide.utilities.BaseActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

@ContentView(R.layout.activity_event)
public class EventActivity extends BaseActivity {

    public static final String ARG_INFO = "info";
    private EventPage mPage;

    @InjectView(R.id.description)
    private WebView descriptionView;

    @InjectView(R.id.to_date)
    private TextView toDateTextView;

    @InjectView(R.id.from_date)
    private TextView fromDateTextView;


    @InjectView(R.id.location)
    private TextView locationTextView;

    //TODO categories, tags

    @InjectView(R.id.current_language)
    private CircleImageView circleImageView;

    private SimpleDateFormat dateFormatFrom = new SimpleDateFormat("HH:mm dd.MM.yy", Locale.GERMANY);
    private SimpleDateFormat allDayDateFormat = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = (EventPage) getIntent().getSerializableExtra(ARG_INFO);
        setSubTitle(mPage.getTitle());
        descriptionView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                //Required functionality here
                Ln.d(message);
                return super.onJsAlert(view, url, message, result);
            }
        });
        descriptionView.setWebViewClient(new WebViewClient());
        descriptionView.getSettings().setJavaScriptEnabled(true);
        descriptionView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        descriptionView.getSettings().setDefaultTextEncodingName("utf-8");
        try {
            descriptionView.loadData(convertContent(mPage.getContent()), "text/html; charset=utf-8", "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        setupLanguagesButton();

        if (mPage.getEvent().isAllDay()) {
            fromDateTextView.setText(allDayDateFormat.format(mPage.getEvent().getStartTime()));
            toDateTextView.setVisibility(View.GONE);
        } else {
            fromDateTextView.setText(dateFormatFrom.format(mPage.getEvent().getStartTime()));
            toDateTextView.setText(dateFormatFrom.format(mPage.getEvent().getEndTime()));
        }

        if (mPage.getLocation() != null) {
            String location = "";
            String name = mPage.getLocation().getName();
            String address = mPage.getLocation().getAddress();
            if (name != null) {
                location += name + " - ";
            }
            if (address != null) {
                location += address;
            }
            locationTextView.setText(location);
        }
    }

    //TODO redundant
    // wraps a div with table-responsive as class around the table
    private String formatContent(String content) {
        return content.replaceAll("<table", "<div class='table-responsive'><table class='table'")
                .replaceAll("</table>", "</table></div>");
    }

    protected boolean setDisplayHomeAsUp() {
        return true;
    }

    //TODO redundant!!
    private String convertContent(String content) throws IOException {
        InputStream htmlStream = getResources().openRawResource(R.raw.index);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = htmlStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            htmlStream.close();
        } catch (IOException e) {
            Ln.e(e);
        }
        String htmlBase = outputStream.toString();
        htmlBase = htmlBase.replace("{content}", formatContent(content));
        Ln.d(htmlBase);
        return htmlBase;
    }

    private void setupLanguagesButton() {
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(EventActivity.this)
                        .title(R.string.dialog_choose_language_title)
                        .adapter(new LanguageItemAdapter(EventActivity.this, mPage.getAvailableLanguages()),
                                new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        loadLanguage(mPage.getAvailableLanguages().get(which));
                                        Ln.d("Clicked item %d", which);
                                        dialog.cancel();
                                    }
                                })
                        .show();
            }
        });
    }

    //TODO maybe BASE-Activity for Event and Page!
    private void loadLanguage(AvailableLanguage language) {
        //load page from db
        //if fail, load language for city from network
        // load page from db

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
