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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.MailTo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.inject.Inject;

import java.io.File;
import java.lang.ref.WeakReference;

import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.page.PageActivity;
import augsburg.se.alltagsguide.persistence.CacheHelper;
import augsburg.se.alltagsguide.persistence.DatabaseCache;
import augsburg.se.alltagsguide.utilities.FileHelper;
import augsburg.se.alltagsguide.utilities.Helper;
import augsburg.se.alltagsguide.utilities.Objects;
import roboguice.RoboGuice;
import roboguice.util.Ln;

/**
 * Created by Mats on 06/11/15.
 */
public class MyWebViewClient extends WebViewClient {
    private final WeakReference<Activity> mActivityRef;
    private String mContent = "";

    @Inject
    private DatabaseCache mDbCache;

    public MyWebViewClient(Activity activity) {
        mActivityRef = new WeakReference<>(activity);
        RoboGuice.injectMembers(activity, this);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        final Activity activity = mActivityRef.get();
        if (url.toLowerCase().contains(".pdf")) {
            Log.i("MyWebViewClient", "Clicked: " + url);
            Uri uri;
            File file = FileHelper.getPDFFileLink(mActivityRef.get(), url);
            if (file.exists()) {
                Log.i("MyWebViewClient", "Open file: " + file.getAbsolutePath());
                uri = Uri.fromFile(file);
            } else {
                uri = Uri.parse(url);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            try {
                activity.startActivity(intent);
                return true;
            } catch (ActivityNotFoundException e) {
                //user does not have a pdf viewer installed
                Ln.e(e);
                view.loadUrl("https://docs.google.com/viewer?" + url);
                return true;
            }
        }

        if (url.startsWith("mailto:")) {
            if (activity != null) {
                MailTo mt = MailTo.parse(url);
                Intent i = newEmailIntent(mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                activity.startActivity(i);
                view.reload();
                return true;
            }
        } else if (url.startsWith("http://") || url.startsWith("https://")) {
            Page page = findByPermalink(url);
            if (page != null) {
                Intent intent = new Intent(activity, PageActivity.class);
                intent.putExtra(PageActivity.ARG_INFO, page);
                activity.startActivity(intent);
            } else {
                Intent extBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                extBrowserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(extBrowserIntent);
            }
            return true;
        } else {
            view.loadUrl(url);
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    private Intent newEmailIntent(String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        String replaceJavascriptString = "javascript:replace('replaceContent','" + escapeString(mContent) + "');";
        view.loadUrl(replaceJavascriptString);
        view.loadUrl("javascript:reorderTables();");
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String escapeString(String string) {
        if (Objects.isNullOrEmpty(string)) {
            return "";
        }

        return string.replace("'", "\\'");
    }

    public SQLiteQueryBuilder getPermaLinkQuery(String url) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Page.TABLES);
        builder.appendWhere(CacheHelper.PAGE_PERMALINK +  " = " + Helper.quote(url));
        return builder;
    }

    public Page findByPermalink(String url) {
        SQLiteQueryBuilder queryBuilder = getPermaLinkQuery(url);
        Cursor cursor = mDbCache.executeRawQuery(queryBuilder);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        return Page.loadFrom(cursor);
    }

}
