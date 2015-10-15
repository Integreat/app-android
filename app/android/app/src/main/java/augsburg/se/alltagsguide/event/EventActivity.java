package augsburg.se.alltagsguide.event;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.inject.Inject;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.AvailableLanguage;
import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.network.EventPageLoader;
import augsburg.se.alltagsguide.utilities.LanguageItemAdapter;
import augsburg.se.alltagsguide.utilities.BaseActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

@ContentView(R.layout.activity_event)
public class EventActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<EventPage> {

    private static final String ARG_LANGUAGE = "language";
    public static final String ARG_INFO = "info";
    private EventPage mPage;

    @Inject
    private Picasso mPicasso;

    @InjectView(R.id.description)
    private WebView descriptionView;

    @InjectView(R.id.to_date)
    private TextView toDateTextView;

    @InjectView(R.id.from_date)
    private TextView fromDateTextView;

    @InjectView(R.id.author)
    private TextView authorTextView;

    @InjectView(R.id.location)
    private TextView locationTextView;

    @InjectView(R.id.other_language_count)
    private TextView otherLanguageCountTextView;

    //TODO categories, tags

    @InjectView(R.id.current_language)
    private CircleImageView circleImageView;

    private SimpleDateFormat dateFormatFrom = new SimpleDateFormat("HH:mm dd.MM.yy", Locale.GERMANY);
    private SimpleDateFormat allDayDateFormat = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEventPage((EventPage) getIntent().getSerializableExtra(ARG_INFO));
    }

    private void setEventPage(EventPage page) {
        mPage = page;
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

        if (mPage.getAuthor() != null) {
            authorTextView.setText(mPage.getAuthor().toText());
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
        otherLanguageCountTextView.setText("+" + mPage.getAvailableLanguages().size());
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
        mPicasso.load(mPage.getLanguage().getIconPath())
                .placeholder(R.drawable.ic_location_not_found_black)
                .error(R.drawable.ic_location_not_found_black)
                .fit()
                .into(circleImageView);
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

    private void loadLanguage(AvailableLanguage language) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_LANGUAGE, language);
        getSupportLoaderManager().restartLoader(0, bundle, this);
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

    @Override
    public Loader<EventPage> onCreateLoader(int id, Bundle args) {
        AvailableLanguage language = (AvailableLanguage) args.getSerializable(ARG_LANGUAGE);
        if (language == null || language.getLoadedLanguage() == null) {
            Ln.d("AvailableLanguage is null or has no language.");
            return null;
        }
        return new EventPageLoader(this, mPrefUtilities.getLocation(), language.getLoadedLanguage(), language.getOtherPageId());
    }

    @Override
    public void onLoadFinished(Loader<EventPage> loader, EventPage data) {
        setEventPage(data);
    }

    @Override
    public void onLoaderReset(Loader<EventPage> loader) {
    }
}
