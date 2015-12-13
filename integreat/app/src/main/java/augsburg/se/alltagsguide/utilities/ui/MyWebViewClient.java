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
import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.commons.lang.StringEscapeUtils;

import java.lang.ref.WeakReference;

import augsburg.se.alltagsguide.utilities.Objects;
import roboguice.util.Ln;

/**
 * Created by Mats on 06/11/15.
 */
public class MyWebViewClient extends WebViewClient {
    private final WeakReference<Activity> mActivityRef;

    private String mContent = "";

    public MyWebViewClient(Activity activity) {
        mActivityRef = new WeakReference<>(activity);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        final Activity activity = mActivityRef.get();
        if (url.toLowerCase().contains(".pdf")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
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
                Intent i = newEmailIntent(activity, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                activity.startActivity(i);
                view.reload();
                return true;
            }
        } else if (url.startsWith("http://") || url.startsWith("https://")) {
            Intent extBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            extBrowserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivityRef.get().startActivity(extBrowserIntent);
            return true;
        } else {
            view.loadUrl(url);
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    private Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
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
}
