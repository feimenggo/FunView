/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feimeng.imagepicker.filter

import android.content.Context
import com.feimeng.imagepicker.MimeType
import com.feimeng.imagepicker.entity.IncapableCause
import com.feimeng.imagepicker.entity.AlbumMedia


/**
 * Filter for choosing a [AlbumMedia]. You can add multiple Filters through
 * [SelectionCreator.addFilter].
 */
abstract class Filter {

    /**
     * Against what mime types this filter applies.
     */
    protected abstract fun constraintTypes(): Set<MimeType>

    /**
     * Invoked for filtering each item.
     *
     * @return null if selectable, [IncapableCause] if not selectable.
     */
    abstract fun filter(context: Context, albumMedia: AlbumMedia): IncapableCause

    /**
     * Whether an [AlbumMedia] need filtering.
     */
    protected fun needFiltering(context: Context, albumMedia: AlbumMedia): Boolean {
        for (type in constraintTypes()) {
            if (type.checkType(context.contentResolver, albumMedia.contentUri)) {
                return true
            }
        }
        return false
    }

    companion object {
        /**
         * Convenient constant for a minimum value.
         */
        const val MIN = 0
        /**
         * Convenient constant for a maximum value.
         */
        const val MAX = Integer.MAX_VALUE
        /**
         * Convenient constant for 1024.
         */
        const val K = 1024
    }
}
