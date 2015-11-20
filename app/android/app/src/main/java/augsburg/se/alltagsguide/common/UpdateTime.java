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

package augsburg.se.alltagsguide.common;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

import augsburg.se.alltagsguide.utilities.Helper;

/**
 * Created by Daniel-L on 23.09.2015.
 */
public class UpdateTime implements Serializable {
    @NonNull private String mDate;
    private static final int OFFSET = 1000;

    public UpdateTime(long time) {
        mDate = Helper.TO_DATE_FORMAT.format(new Date(time + OFFSET));
    }

    @NonNull
    @Override
    public String toString() {
        return mDate;
    }
}
