
package es.gluetech.categorias.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import es.gluetech.categorias.db.DatabaseHelper;
import es.gluetech.categorias.db.table.CamposDocumentoCategoriaTable;
import es.gluetech.categorias.db.table.CamposDocumentoCategoriaTable.CampoDocumentoCategoriaColumns;

/**
 * Provee acceso a la tabla de campos de un documento de una categoria
 */
public class CamposDocumentoCategoriaProvider extends ContentProvider {

    private static final int CAMPOS = 1;
    private static final int CAMPOS_ID = 2;
    private static final int CAMPOS_ID_DOCUMENTO = 3;

    private static final UriMatcher sUriMatcher;

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CamposDocumentoCategoriaTable.NOMBRE_TABLA);

        switch (sUriMatcher.match(uri)) {
        case CAMPOS:
            break;

        case CAMPOS_ID:
            qb.appendWhere(CampoDocumentoCategoriaColumns._ID + "=" + uri.getPathSegments().get(1));
            break;
            
        case CAMPOS_ID_DOCUMENTO:
    		qb.appendWhere(CampoDocumentoCategoriaColumns.ID_DOCUMENTO_CATEGORIA + "=" + uri.getPathSegments().get(2));
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = CampoDocumentoCategoriaColumns.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), CampoDocumentoCategoriaColumns.CONTENT_URI);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
     
        case CAMPOS:
            return CampoDocumentoCategoriaColumns.CONTENT_TYPE;
            
        case CAMPOS_ID_DOCUMENTO:
            return CampoDocumentoCategoriaColumns.CONTENT_TYPE;

        case CAMPOS_ID:
            return CampoDocumentoCategoriaColumns.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != CAMPOS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        Long now = Long.valueOf(System.currentTimeMillis());

        // Make sure that the fields are all set
        if (values.containsKey(CampoDocumentoCategoriaColumns.FECHA_CREACION) == false) {
            values.put(CampoDocumentoCategoriaColumns.FECHA_CREACION, now);
        }

        if (values.containsKey(CampoDocumentoCategoriaColumns.FECHA_MODIFICACION) == false) {
            values.put(CampoDocumentoCategoriaColumns.FECHA_MODIFICACION, now);
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(CamposDocumentoCategoriaTable.NOMBRE_TABLA, null, values);
        if (rowId > 0) {
            Uri categoriaUri = ContentUris.withAppendedId(CampoDocumentoCategoriaColumns.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(categoriaUri, null);
            return categoriaUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case CAMPOS:
            count = db.delete(CamposDocumentoCategoriaTable.NOMBRE_TABLA, where, whereArgs);
            break;

        case CAMPOS_ID:
            String idValor = uri.getPathSegments().get(1);
            count = db.delete(CamposDocumentoCategoriaTable.NOMBRE_TABLA, CampoDocumentoCategoriaColumns._ID + "=" + idValor
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case CAMPOS:
            count = db.update(CamposDocumentoCategoriaTable.NOMBRE_TABLA, values, where, whereArgs);
            break;

        case CAMPOS_ID:
            String idValor = uri.getPathSegments().get(1);
            count = db.update(CamposDocumentoCategoriaTable.NOMBRE_TABLA, values, CampoDocumentoCategoriaColumns._ID + "=" + idValor
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(CamposDocumentoCategoriaTable.AUTHORITY, "camposdocumento", CAMPOS);
        sUriMatcher.addURI(CamposDocumentoCategoriaTable.AUTHORITY, "camposdocumento/#", CAMPOS_ID);
        sUriMatcher.addURI(CamposDocumentoCategoriaTable.AUTHORITY, "camposdocumento/documento/#", CAMPOS_ID_DOCUMENTO);
     }
}