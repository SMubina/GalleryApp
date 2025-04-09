package com.example.galleryapp.data.mediaquery

import android.database.Cursor
import kotlinx.coroutines.flow.Flow

/**
 * This class is responsible for fetching data with cursor
 * and actual model type data
 */
abstract class QueryFlow<T>  {

    /** A flow of the data specified by the query */
    abstract fun flowData(): Flow<List<T>>

    /** A flow of the cursor specified by the query */
    abstract fun flowCursor(): Flow<Cursor?>
}