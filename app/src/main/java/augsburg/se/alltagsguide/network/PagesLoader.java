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

package augsburg.se.alltagsguide.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.inject.Inject;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import augsburg.se.alltagsguide.common.AvailableLanguage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.persistence.resources.AvailableLanguageResource;
import augsburg.se.alltagsguide.persistence.resources.PageResource;
import augsburg.se.alltagsguide.utilities.BasicLoader;
import augsburg.se.alltagsguide.utilities.FileHelper;
import augsburg.se.alltagsguide.utilities.LoadingType;
import roboguice.util.Ln;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class PagesLoader extends BasicLoader<List<Page>> {

    @NonNull private Location mLocation;
    @NonNull private Language mLanguage;

    @Inject
    private PageResource.Factory pagesFactory;

    @Inject
    private AvailableLanguageResource.Factory availableLanguageFactory;

    /**
     * Create loader for context
     *
     * @param context
     */
    public PagesLoader(Context context, @NonNull Location location, @NonNull Language language, LoadingType loadingType) {
        super(context, loadingType);
        mLocation = location;
        mLanguage = language;
    }



    @NonNull
    @Override
    public List<Page> load() {
        try {
            List<Page> pages = get(pagesFactory.under(mLanguage, mLocation));
            FileHelper.downloadPDfs(pages, new FileDownloadListener() {
                @Override
                protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                }

                @Override
                protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                }

                @Override
                protected void blockComplete(BaseDownloadTask task) {

                }

                @Override
                protected void completed(BaseDownloadTask task) {
                    Log.i("PagesLoader", "Downloaded: " + task.getUrl() + " to: " + task.getPath());
                }

                @Override
                protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                }

                @Override
                protected void error(BaseDownloadTask task, Throwable e) {
                }

                @Override
                protected void warn(BaseDownloadTask task) {

                }
            }, getContext());
            List<AvailableLanguage> languages = dbCache.load(availableLanguageFactory.under(mLanguage, mLocation));
            Page.recreateRelations(pages, languages, mLanguage);
            return pages;
        } catch (IOException e) {
            Ln.e(e);
            return Collections.emptyList();
        }
    }
}
