
package es.gluetech.categorias.provider;


import java.util.HashMap;

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
import es.gluetech.categorias.db.table.CategoriasTable;
import es.gluetech.categorias.db.table.CategoriasTable.CategoriaColumns;

/**
 * Provee acceso a la tabla de categorias.
 *
 */
public class CategoriasProvider extends ContentProvider {

    private static HashMap<String, String> sCategoriasProjectionMap;

    private static final int CATEGORIAS = 1;
    private static final int CATEGORIA_ID = 2;
    private static final int CATEGORIAS_ID_PADRE = 3;

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
        qb.setTables(CategoriasTable.NOMBRE_TABLA);

        switch (sUriMatcher.match(uri)) {
        case CATEGORIAS:
            
            qb.setProjectionMap(sCategoriasProjectionMap);
            break;

        case CATEGORIA_ID:
            qb.setTables(CategoriasTable.NOMBRE_TABLA);
            qb.setProjectionMap(sCategoriasProjectionMap);
            qb.appendWhere(CategoriaColumns._ID + "=" + uri.getPathSegments().get(1));
            break;
            
        case CATEGORIAS_ID_PADRE:
            qb.setProjectionMap(sCategoriasProjectionMap);
    		qb.appendWhere(CategoriaColumns.ID_CATEGORIA_PADRE + "=" + uri.getPathSegments().get(2));
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = CategoriaColumns.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), CategoriaColumns.CONTENT_URI);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
     
        case CATEGORIAS:
            return CategoriaColumns.CONTENT_TYPE;
            
        case CATEGORIAS_ID_PADRE:
            return CategoriaColumns.CONTENT_TYPE;

        case CATEGORIA_ID:
            return CategoriaColumns.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != CATEGORIAS) {
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
        if (values.containsKey(CategoriaColumns.FECHA_CREACION) == false) {
            values.put(CategoriaColumns.FECHA_CREACION, now);
        }

        if (values.containsKey(CategoriaColumns.FECHA_MODIFICACION) == false) {
            values.put(CategoriaColumns.FECHA_MODIFICACION, now);
        }

        if (values.containsKey(CategoriaColumns.NOMBRE) == false) {
            values.put(CategoriaColumns.NOMBRE, "");
        }
        
        if (values.containsKey(CategoriaColumns.ID_CATEGORIA_PADRE) == false) {
            values.put(CategoriaColumns.ID_CATEGORIA_PADRE, 0);
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(CategoriasTable.NOMBRE_TABLA, null, values);
        if (rowId > 0) {
            Uri categoriaUri = ContentUris.withAppendedId(CategoriaColumns.CONTENT_URI, rowId);
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
        case CATEGORIAS:
            count = db.delete(CategoriasTable.NOMBRE_TABLA, where, whereArgs);
            break;

        case CATEGORIA_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.delete(CategoriasTable.NOMBRE_TABLA, CategoriaColumns._ID + "=" + noteId
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
        case CATEGORIAS:
            count = db.update(CategoriasTable.NOMBRE_TABLA, values, where, whereArgs);
            break;

        case CATEGORIA_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.update(CategoriasTable.NOMBRE_TABLA, values, CategoriaColumns._ID + "=" + noteId
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
        sUriMatcher.addURI(CategoriasTable.AUTHORITY, "categorias", CATEGORIAS);
        sUriMatcher.addURI(CategoriasTable.AUTHORITY, "categorias/#", CATEGORIA_ID);
        sUriMatcher.addURI(CategoriasTable.AUTHORITY, "categorias/padre/#", CATEGORIAS_ID_PADRE);

        sCategoriasProjectionMap = new HashMap<String, String>();
        sCategoriasProjectionMap.put(CategoriaColumns._ID, CategoriasTable.NOMBRE_TABLA + "." + CategoriaColumns._ID);
        sCategoriasProjectionMap.put(CategoriaColumns.NOMBRE, CategoriaColumns.NOMBRE);
        sCategoriasProjectionMap.put(CategoriaColumns.ID_CATEGORIA_PADRE, CategoriaColumns.ID_CATEGORIA_PADRE);
        sCategoriasProjectionMap.put(CategoriaColumns.FECHA_CREACION, CategoriaColumns.FECHA_CREACION);
        sCategoriasProjectionMap.put(CategoriaColumns.FECHA_MODIFICACION, CategoriaColumns.FECHA_MODIFICACION);

     }
}