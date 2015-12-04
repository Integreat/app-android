/*
 * This file is part of Integreat.
 *
 * Integreat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Integreat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Integreat.  If not, see <http://www.gnu.org/licenses/>.
 */

package augsburg.se.alltagsguide.utilities.ui;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.inject.Inject;
import com.squareup.picasso.Picasso;

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
    protected static final String TRANSLATED_DISMISSED = "snackbar_dismissed";
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

    private MyWebViewClient myWebViewClient;

    private boolean mTranslatedDismissed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWebView();
        if (savedInstanceState == null) {
            setPageFromSerializable(getIntent().getSerializableExtra(ARG_INFO));
        }
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

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mTranslatedDismissed = savedInstanceState.getBoolean(TRANSLATED_DISMISSED);
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
        
        if (mPage.isAutoTranslated() && !mTranslatedDismissed) {
            final Snackbar snackBar = Snackbar.make(mToolbar, R.string.auto_translated, Snackbar.LENGTH_INDEFINITE);
            snackBar.getView().setBackgroundColor(mPrefUtilities.getCurrentColor());
            snackBar.setAction(R.string.auto_translated_snackbar_close, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackBar.dismiss();
                    mTranslatedDismissed = true;
                }
            });
            snackBar.show();
        }
    }

    protected abstract void setMorePageDetails(T t);

    protected void loadWebViewData(String content) {
        if (Build.VERSION.SDK_INT < 18) {
            descriptionView.clearView();
        } else {
            descriptionView.loadUrl("about:blank");
        }

        // webview bug android versions 2.3.X
        // http://stackoverflow.com/a/8162828/1484047
        if ("2.3".equals(Build.VERSION.RELEASE)) {
            descriptionView.loadDataWithBaseURL(null, content, "text/html; charset=utf-8", "utf-8", null);
        } else {
            myWebViewClient.setContent(content);
            descriptionView.loadUrl("file:///android_asset/integreat.html");
            //descriptionView.loadData(content, "text/html; charset=utf-8", "utf-8");
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void initWebView() {
        myWebViewClient = new MyWebViewClient(this);
        descriptionView.setWebViewClient(myWebViewClient);
        // javascript broken bug android versions 2.3.X
        if (!"2.3".equals(Build.VERSION.RELEASE)) {
            descriptionView.getSettings().setJavaScriptEnabled(true);
            descriptionView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        }
        descriptionView.getSettings().setDefaultTextEncodingName("utf-8");

        setFontSize();
    }

    private void setFontSize() {
        switch (mPrefUtilities.getFontStyle().getResId()) {
            case R.style.FontStyle_XSmall:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    descriptionView.getSettings().setTextZoom(75);
                } else {
                    descriptionView.getSettings().setTextSize(WebSettings.TextSize.SMALLEST);
                }
                break;
            case R.style.FontStyle_Small:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    descriptionView.getSettings().setTextZoom(100);
                } else {
                    descriptionView.getSettings().setTextSize(WebSettings.TextSize.SMALLER);
                }
                break;
            case R.style.FontStyle_Medium:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    descriptionView.getSettings().setTextZoom(125);
                } else {
                    descriptionView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
                }
                break;
            case R.style.FontStyle_Large:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    descriptionView.getSettings().setTextZoom(150);
                } else {
                    descriptionView.getSettings().setTextSize(WebSettings.TextSize.LARGER);
                }
                break;
            case R.style.FontStyle_XLarge:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    descriptionView.getSettings().setTextZoom(175);
                } else {
                    descriptionView.getSettings().setTextSize(WebSettings.TextSize.LARGEST);
                }
                break;
        }
    }

    protected void loadWebViewData() {
        loadWebViewData(mPage.getContent());
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

    protected boolean shouldSetDisplayHomeAsUp() {
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
        outState.putBoolean(TRANSLATED_DISMISSED, mTranslatedDismissed);
        super.onSaveInstanceState(outState);
    }
}