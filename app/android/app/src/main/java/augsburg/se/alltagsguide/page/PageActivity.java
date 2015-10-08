package augsburg.se.alltagsguide.page;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.utilities.BaseActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

@ContentView(R.layout.activity_page)
public class PageActivity extends BaseActivity {
    public static final String ARG_INFO = "info";
    private Page mPage;

    @InjectView(R.id.description)
    private WebView descriptionView;

    @InjectView(R.id.current_language)
    private CircleImageView circleImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPage = (Page) getIntent().getSerializableExtra(ARG_INFO);
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

    }

    private void setupLanguagesButton() {
        //TODO ausgrauen von nicht verfügbaren sprachen
        Language english = new Language(0, "en", "English", "https://upload.wikimedia.org/wikipedia/en/thumb/a/ae/Flag_of_the_United_Kingdom.svg/100px-Flag_of_the_United_Kingdom.svg.png");
        Language german = new Language(1, "de", "Deutsch", "https://upload.wikimedia.org/wikipedia/en/thumb/b/ba/Flag_of_Germany.svg/100px-Flag_of_Germany.svg.png");
        Language spanish = new Language(2, "es", "Espanol", "https://upload.wikimedia.org/wikipedia/en/thumb/9/9a/Flag_of_Spain.svg/100px-Flag_of_Spain.svg.png");
        Language frensh = new Language(3, "fr", "Francais", "https://upload.wikimedia.org/wikipedia/en/thumb/c/c3/Flag_of_France.svg/100px-Flag_of_France.svg.png");

        final List<Language> languages = new ArrayList<>();
        languages.add(german);
        languages.add(frensh);
        languages.add(english);
        languages.add(spanish);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(PageActivity.this)
                        .title(R.string.dialog_choose_language_title)
                        .adapter(new LanguageItemAdapter(PageActivity.this, languages),
                                new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        Ln.d("Clicked item %d", which);
                                    }
                                })
                        .show();
            }
        });
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

}
