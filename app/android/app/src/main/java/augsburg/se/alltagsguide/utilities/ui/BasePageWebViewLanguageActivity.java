package augsburg.se.alltagsguide.utilities.ui;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.inject.Inject;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.AvailableLanguage;
import augsburg.se.alltagsguide.common.Page;
import de.hdodenhof.circleimageview.CircleImageView;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

/**
 * Created by Daniel-L on 16.10.2015.
 */
public abstract class BasePageWebViewLanguageActivity<T extends Page> extends BaseActivity implements LoaderManager.LoaderCallbacks<T> {
    protected static final String ARG_LANGUAGE = "language";
    protected static final String PAGE_STATE = "page";
    public static final String ARG_INFO = "info";
    protected T mPage;

    @Inject
    protected Picasso mPicasso;

    @InjectView(R.id.description)
    private WebView descriptionView;

    @InjectView(R.id.other_language_count)
    protected TextView otherLanguageCountTextView;

    @InjectView(R.id.current_language)
    protected CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWebView();
        setPageFromSerializable(getIntent().getSerializableExtra(ARG_INFO));
    }

    @SuppressWarnings("unchecked")
    private void setPageFromSerializable(Serializable serializable) {
        try {
            if (serializable != null) {
                setPage((T) serializable);
            } else {
                throw new IllegalStateException("Serializable Page was null");
            }
        } catch (ClassCastException e) {
            Ln.e(e);
            throw new IllegalStateException("ARG_INFO has to be of type (? extends Page)");
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Serializable savedInstance = savedInstanceState.getSerializable(PAGE_STATE);
        setPageFromSerializable(savedInstance);
    }


    @SuppressLint("SetTextI18n")
    protected void setupLanguagesButton() {
        mPicasso.load(mPage.getLanguage().getIconPath())
                .placeholder(R.drawable.icon_language_loading)
                .error(R.drawable.icon_language_loading_error)
                .fit()
                .into(circleImageView);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mPage.getAvailableLanguages().isEmpty()) {
                    new MaterialDialog.Builder(BasePageWebViewLanguageActivity.this)
                            .title(R.string.dialog_choose_language_title)
                            .adapter(new LanguageItemAdapter(BasePageWebViewLanguageActivity.this, mPage.getAvailableLanguages(), false),
                                    new MaterialDialog.ListCallback() {
                                        @Override
                                        public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                            loadLanguage(mPage.getAvailableLanguages().get(which));
                                            Ln.d("Clicked item %d", which);
                                            dialog.cancel();
                                        }
                                    })
                            .show();
                } else {
                    Snackbar.make(descriptionView, R.string.no_other_languages_available_page, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        otherLanguageCountTextView.setText("+" + mPage.getAvailableLanguages().size());
    }

    protected void setPage(T t) {
        mPage = t;
        setSubTitle(mPage.getTitle());
        loadWebViewData();
        setupLanguagesButton();
        setMorePageDetails(t);
    }

    protected abstract void setMorePageDetails(T t);

    protected void loadWebViewData(String content) {
        if (Build.VERSION.SDK_INT < 18) {
            descriptionView.clearView();
        } else {
            descriptionView.loadUrl("about:blank");
        }
        descriptionView.loadData(content, "text/html; charset=utf-8", "utf-8");
        //descriptionView.loadDataWithBaseURL(null, content, "text/html; charset=utf-8", "utf-8", null);
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void initWebView() {
        descriptionView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.toLowerCase().contains(".pdf")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    try {
                        BasePageWebViewLanguageActivity.this.startActivity(intent);
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
    }

    // wraps a div with table-responsive as class around the table
    private String formatContent(String content) {
        return content.replaceAll("<table", "<div class='table-responsive'><table class='table'")
                .replaceAll("</table>", "</table></div>");
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

    protected void loadWebViewData() {
        try {
            loadWebViewData(convertContent(mPage.getContent()));
        } catch (IOException e) {
            Ln.e(e);
        }
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

    protected boolean setDisplayHomeAsUp() {
        return true;
    }

    protected void loadLanguage(AvailableLanguage language) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_LANGUAGE, language);
        getSupportLoaderManager().restartLoader(0, bundle, this);
        startLoading();
    }

    @Override
    public void onLoadFinished(Loader<T> loader, T data) {
        if (data != null) {
            setPage(data);
        }
        stopLoading();
    }

    @Override
    public void onLoaderReset(Loader<T> loader) {
    }

    private MaterialDialog mLoadingDialog;

    private void startLoading() {
        stopLoading();
        mLoadingDialog = new MaterialDialog.Builder(BasePageWebViewLanguageActivity.this)
                .title(R.string.dialog_title_loading_language)
                .content(R.string.dialog_content_loading_language)
                .progress(true, 0)
                .show();
    }

    private void stopLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
        mLoadingDialog = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(PAGE_STATE, mPage);
        super.onSaveInstanceState(outState);
    }

}
