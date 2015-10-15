package augsburg.se.alltagsguide.common;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.io.Serializable;

import augsburg.se.alltagsguide.BuildConfig;
import augsburg.se.alltagsguide.persistence.CacheHelper;
import augsburg.se.alltagsguide.utilities.Objects;

/**
 * Created by Daniel-L on 09.10.2015.
 */
public class Author implements Serializable {
    private String mLogin;
    private String mFirstName;
    private String mLastName;

    public Author(String login, String firstName, String lastName) {
        mLogin = login;
        mFirstName = firstName;
        mLastName = lastName;
    }

    public String getLogin() {
        return mLogin;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public static Author fromJson(@NonNull final JsonObject jsonTag) {
        String login = jsonTag.get("login").getAsString();
        String firstName = jsonTag.get("first_name").getAsString();
        String lastName = jsonTag.get("last_name").getAsString();
        return new Author(login, firstName, lastName);
    }

    public static Author fromCursor(Cursor cursor) {
        if (BuildConfig.DEBUG) {
            if (cursor.isClosed()) {
                throw new IllegalStateException("Cursor should not be closed");
            }
        }
        String login = cursor.getString(cursor.getColumnIndex(CacheHelper.AUTHOR_USERNAME));
        String firstName = cursor.getString(cursor.getColumnIndex(CacheHelper.AUTHOR_FIRSTNAME));
        String lastName = cursor.getString(cursor.getColumnIndex(CacheHelper.AUTHOR_LASTNAME));
        return new Author(login, firstName, lastName);
    }

    public String toText() {
        String text = "";
        if (mFirstName != null) {
            text += mFirstName + " ";
        }
        if (mLastName != null) {
            text += mLastName;
        }
        text = text.trim();
        if (Objects.isNullOrEmpty(text)) {
            text += mLogin;
        }
        return text;
    }
}
