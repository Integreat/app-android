package augsburg.se.alltagsguide.information;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Information;
import augsburg.se.alltagsguide.utilities.BaseActivity;

public class InformationActivity extends BaseActivity {
    public static final String ARG_INFO = "info";

    private Information mInformation;
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        mInformation = (Information) getIntent().getSerializableExtra(ARG_INFO);
        setTitle(mInformation.getTitle());

        TextView desriptionView = (TextView) findViewById(R.id.description);
        desriptionView.setText(mInformation.getDescription());

        ImageView imageView = (ImageView) findViewById(R.id.image);
        //TODO
    }

}
