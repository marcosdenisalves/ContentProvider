package com.myproject.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.media.UnsupportedSchemeException
import android.net.Uri
import android.provider.BaseColumns._ID
import com.myproject.contentprovider.services.NotesDataBaseHelper
import com.myproject.contentprovider.services.NotesDataBaseHelper.Companion.TABLE_NOTES

class NotesProvider : ContentProvider() {

    private lateinit var mainUriMatcher: UriMatcher
    private lateinit var dbHelper: NotesDataBaseHelper

    override fun onCreate(): Boolean {
        mainUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        mainUriMatcher.addURI(AUTHORITY, "notes", NOTES)
        mainUriMatcher.addURI(AUTHORITY, "notes/#", NOTE_BY_ID)
        if (context != null) {
            dbHelper = NotesDataBaseHelper(context as Context)
        }
        return true
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        if (mainUriMatcher.match(uri) == NOTE_BY_ID) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val linesAffect: Int = db.delete(TABLE_NOTES, "$_ID = ?", arrayOf(uri.lastPathSegment))
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return linesAffect
        } else {
            throw UnsupportedSchemeException("Uri inválida para exclusão")
        }
    }

    override fun getType(uri: Uri): String? = throw UnsupportedSchemeException("Não implementado")

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (mainUriMatcher.match(uri) == NOTES) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val id: Long = db.insert(TABLE_NOTES, null, values)
            val insertUri = Uri.withAppendedPath(BASE_URI, id.toString())
            db.close()

            context?.contentResolver?.notifyChange(uri, null)
            return insertUri
        } else {
            throw UnsupportedSchemeException("Uri inválida para inserção")
        }
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return when {
            mainUriMatcher.match(uri) == NOTES -> {
                val db: SQLiteDatabase = dbHelper.writableDatabase
                val cursor: Cursor = db.query(
                    TABLE_NOTES,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
                cursor.setNotificationUri(context?.contentResolver, uri)
                cursor
            }
            mainUriMatcher.match(uri) == NOTE_BY_ID -> {
                val db: SQLiteDatabase = dbHelper.writableDatabase
                val cursor = db.query(
                    TABLE_NOTES,
                    projection,
                    "$_ID = ?",
                    arrayOf(uri.lastPathSegment),
                    null,
                    null,
                    sortOrder
                )
                cursor.setNotificationUri((context as Context).contentResolver, uri)
                cursor
            }
            else -> {
                throw UnsupportedSchemeException("Uri não implementada")
            }
        }
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        if (mainUriMatcher.match(uri) == NOTE_BY_ID) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val linesAffect =
                db.update(TABLE_NOTES, values, "$_ID = ?", arrayOf(uri.lastPathSegment))
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return linesAffect
        } else {
            throw UnsupportedSchemeException("Uri não implementada")
        }
    }

    companion object {
        const val AUTHORITY = "com.courses.applicationcontentprovider.provider"
        val BASE_URI = Uri.parse("content://$AUTHORITY")
        val URI_NOTES = Uri.withAppendedPath(BASE_URI, "notes")

        const val NOTES = 1
        const val NOTE_BY_ID = 2
    }
}