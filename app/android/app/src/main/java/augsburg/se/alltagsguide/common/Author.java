package augsburg.se.alltagsguide.common;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonObject;

import java.io.Serializable;

import augsburg.se.alltagsguide.BuildConfig;
import augsburg.se.alltagsguide.persistence.CacheHelper;
import augsburg.se.alltagsguide.utilities.Objects;

/**
 * Created by Daniel-L on 09.10.2015.
 */
public class Author implements Serializable {
    @Nullable private String mLogin;
    @Nullable private String mFirstName;
    @Nullable private String mLastName;

    public Author(@Nullable String login, @Nullable String firstName, @Nullable String lastName) {
        mLogin = login;
        mFirstName = firstName;
        mLastName = lastName;
    }

    @Nullable
    public String getLogin() {
        return mLogin;
    }

    @Nullable
    public String getFirstName() {
        return mFirstName;
    }

    @Nullable
    public String getLastName() {
        return mLastName;
    }

    @NonNull
    public static Author fromJson(@NonNull final JsonObject jsonTag) {
        String login = jsonTag.get("login").getAsString();
        String firstName = jsonTag.get("first_name").getAsString();
        String lastName = jsonTag.get("last_name").getAsString();
        return new Author(login, firstName, lastName);
    }

    @NonNull
    public static Author fromCursor(@NonNull Cursor cursor) {
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

    @NonNull
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
