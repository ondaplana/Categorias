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
import es.gluetech.categorias.db.table.TiposCampoCategoriaTable;
import es.gluetech.categorias.db.table.TiposCampoCategoriaTable.TipoCampoCategoriaColumns;


/**
 * Provee acceso a la tabla de tipos de campos de las categoriass.
 * 
 */
public class TiposCampoCategoriaProvider extends ContentProvider {

	private static final UriMatcher sUriMatcher;

	private static final int TIPOS_CAMPO = 1;
	private static final int TIPOS_CAMPO_ID = 2;

	private DatabaseHelper mOpenHelper;

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(TiposCampoCategoriaTable.NOMBRE_TABLA);

		switch (sUriMatcher.match(uri)) {
		case TIPOS_CAMPO:
			break;

		case TIPOS_CAMPO_ID:
			qb.appendWhere(TipoCampoCategoriaColumns._ID + "=" + uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = TipoCampoCategoriaColumns.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}

		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,	null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {

		case TIPOS_CAMPO:
			return TipoCampoCategoriaColumns.CONTENT_TYPE;

		case TIPOS_CAMPO_ID:
			return TipoCampoCategoriaColumns.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// Validate the requested uri
		if (sUriMatcher.match(uri) != TIPOS_CAMPO) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(TiposCampoCategoriaTable.NOMBRE_TABLA, null, values);
		if (rowId > 0) {
			Uri categoriaUri = ContentUris.withAppendedId(
					TipoCampoCategoriaColumns.CONTENT_URI, rowId);
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
		case TIPOS_CAMPO:
			count = db.delete(TiposCampoCategoriaTable.NOMBRE_TABLA, where, whereArgs);
			break;

		case TIPOS_CAMPO_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.delete(
					TiposCampoCategoriaTable.NOMBRE_TABLA,
					TipoCampoCategoriaColumns._ID
							+ "="
							+ noteId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case TIPOS_CAMPO:
			count = db.update(TiposCampoCategoriaTable.NOMBRE_TABLA, values, where, whereArgs);
			break;

		case TIPOS_CAMPO_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.update(
					TiposCampoCategoriaTable.NOMBRE_TABLA,
					values,
					TipoCampoCategoriaColumns._ID
							+ "="
							+ noteId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(TiposCampoCategoriaTable.AUTHORITY, "tiposcampo", TIPOS_CAMPO);
		sUriMatcher.addURI(TiposCampoCategoriaTable.AUTHORITY, "tiposcampo/#", TIPOS_CAMPO_ID);
	}

}