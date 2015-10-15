package augsburg.se.alltagsguide.page;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.inject.Inject;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.AvailableLanguage;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.network.PageLoader;
import augsburg.se.alltagsguide.utilities.BaseActivity;
import augsburg.se.alltagsguide.utilities.LanguageItemAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

@ContentView(R.layout.activity_page)
public class PageActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Page> {
    private static final String ARG_LANGUAGE = "language";
    public static final String ARG_INFO = "info";
    private Page mPage;

    @InjectView(R.id.description)
    private WebView descriptionView;

    @InjectView(R.id.current_language)
    private CircleImageView circleImageView;

    @Inject
    private Picasso mPicasso;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPage((Page) getIntent().getSerializableExtra(ARG_INFO));
    }

    private void setPage(Page page) {
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
        descriptionView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.toLowerCase().contains(".pdf")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    try {
                        PageActivity.this.startActivity(intent);
                        return true;
                    } catch (ActivityNotFoundException e) {
                        //user does not have a pdf viewer installed
                        Ln.e(e);
                        view.loadUrl("https://docs.google.com/viewer?" + url);
                        return true;
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        descriptionView.getSettings().setJavaScriptEnabled(true);
        descriptionView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        descriptionView.getSettings().setDefaultTextEncodingName("utf-8");
        try {
            descriptionView.loadData(convertContent(mPage.getContent()), "text/html; charset=utf-8", "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        setupLanguagesButton();
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
                new MaterialDialog.Builder(PageActivity.this)
                        .title(R.string.dialog_choose_language_title)
                        .adapter(new LanguageItemAdapter(PageActivity.this, mPage.getAvailableLanguages()),
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

    // wraps a div with table-responsive as class around the table
    private String formatContent(String content) {
        return content.replaceAll("<table", "<div class='table-responsive'><table class='table'")
                .replaceAll("</table>", "</table></div>");
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

    @Override
    public Loader<Page> onCreateLoader(int id, Bundle args) {
        AvailableLanguage language = (AvailableLanguage) args.getSerializable(ARG_LANGUAGE);
        if (language == null || language.getLoadedLanguage() == null) {
            Ln.d("AvailableLanguage is null or has no language.");
            return null;
        }
        return new PageLoader(this, mPrefUtilities.getLocation(), language.getLoadedLanguage(), language.getOtherPageId());
    }

    @Override
    public void onLoadFinished(Loader<Page> loader, Page data) {
        setPage(data);
    }

    @Override
    public void onLoaderReset(Loader<Page> loader) {
    }

}
