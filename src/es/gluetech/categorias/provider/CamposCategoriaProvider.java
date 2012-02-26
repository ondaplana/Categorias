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
import es.gluetech.categorias.db.table.CamposCategoriaTable;
import es.gluetech.categorias.db.table.CamposCategoriaTable.CampoCategoriaColumns;
import es.gluetech.categorias.db.table.TiposCampoCategoriaTable;
import es.gluetech.categorias.db.table.TiposCampoCategoriaTable.TipoCampoCategoriaColumns;

/**
 * Provee acceso a la tabla de campos de una categoria.
 * 
 */
public class CamposCategoriaProvider extends ContentProvider {

	private static HashMap<String, String> sCamposProjectionMap;

	private static final int CAMPOS = 1;
	private static final int CAMPOS_ID = 2;
	private static final int CAMPOS_CATEGORIA_ID = 3;

	private static final UriMatcher sUriMatcher;

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
		qb.setTables(CamposCategoriaTable.NOMBRE_TABLA
				     + " INNER JOIN " 
				     + TiposCampoCategoriaTable.NOMBRE_TABLA
					 + " ON ("
				     + CamposCategoriaTable.NOMBRE_TABLA + "." + CampoCategoriaColumns.ID_TIPO
				     + "="
				     + TiposCampoCategoriaTable.NOMBRE_TABLA + "." + TipoCampoCategoriaColumns._ID + ")");

		switch (sUriMatcher.match(uri)) {
		case CAMPOS:
			qb.setProjectionMap(sCamposProjectionMap);
			break;

		case CAMPOS_ID:
			qb.setProjectionMap(sCamposProjectionMap);
			qb.appendWhere(CamposCategoriaTable.NOMBRE_TABLA + "." + CampoCategoriaColumns._ID + "="
					+ uri.getPathSegments().get(1));
			break;
			
		case CAMPOS_CATEGORIA_ID:
			qb.setProjectionMap(sCamposProjectionMap);
			qb.appendWhere(CamposCategoriaTable.NOMBRE_TABLA + "." + CampoCategoriaColumns.ID_CATEGORIA + "="
					+ uri.getPathSegments().get(2));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = CamposCategoriaTable.NOMBRE_TABLA + "." + CampoCategoriaColumns.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}

		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {

		case CAMPOS:
			return CampoCategoriaColumns.CONTENT_TYPE;

		case CAMPOS_ID:
			return CampoCategoriaColumns.CONTENT_ITEM_TYPE;

		case CAMPOS_CATEGORIA_ID:
			return CampoCategoriaColumns.CONTENT_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// Validate the requested uri
		if ((sUriMatcher.match(uri) != CAMPOS)
				&& (sUriMatcher.match(uri) != CAMPOS_CATEGORIA_ID)) {
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
		if (values.containsKey(CampoCategoriaColumns.FECHA_CREACION) == false) {
			values.put(CampoCategoriaColumns.FECHA_CREACION, now);
		}

		if (values.containsKey(CampoCategoriaColumns.FECHA_MODIFICACION) == false) {
			values.put(CampoCategoriaColumns.FECHA_MODIFICACION, now);
		}

		if (values.containsKey(CampoCategoriaColumns.ID_CATEGORIA) == false) {
			values.put(CampoCategoriaColumns.ID_CATEGORIA, 0);
		}

		if (values.containsKey(CampoCategoriaColumns.NOMBRE) == false) {
			values.put(CampoCategoriaColumns.NOMBRE, "");
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(CamposCategoriaTable.NOMBRE_TABLA, null, values);
		if (rowId > 0) {
			Uri categoriaUri = ContentUris.withAppendedId(
					CampoCategoriaColumns.CONTENT_URI, rowId);
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
			count = db.delete(CamposCategoriaTable.NOMBRE_TABLA, where, whereArgs);
			break;

		case CAMPOS_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.delete(
					CamposCategoriaTable.NOMBRE_TABLA,
					CampoCategoriaColumns._ID
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
		case CAMPOS:
			count = db.update(CamposCategoriaTable.NOMBRE_TABLA, values, where, whereArgs);
			break;

		case CAMPOS_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.update(
					CamposCategoriaTable.NOMBRE_TABLA,
					values,
					CampoCategoriaColumns._ID
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
		sUriMatcher.addURI(CamposCategoriaTable.AUTHORITY, "campos", CAMPOS);
		sUriMatcher.addURI(CamposCategoriaTable.AUTHORITY, "campos/#", CAMPOS_ID);
		sUriMatcher.addURI(CamposCategoriaTable.AUTHORITY, "campos/categoria/#", CAMPOS_CATEGORIA_ID);

		sCamposProjectionMap = new HashMap<String, String>();
		sCamposProjectionMap.put(CampoCategoriaColumns._ID, CamposCategoriaTable.NOMBRE_TABLA + "." + CampoCategoriaColumns._ID);
		sCamposProjectionMap.put(CampoCategoriaColumns.NOMBRE,	CamposCategoriaTable.NOMBRE_TABLA + "." + CampoCategoriaColumns.NOMBRE);
		sCamposProjectionMap.put(CampoCategoriaColumns.ID_TIPO, CamposCategoriaTable.NOMBRE_TABLA + "." + CampoCategoriaColumns.ID_TIPO);
		sCamposProjectionMap.put(CampoCategoriaColumns.NOMBRE_TIPO, TiposCampoCategoriaTable.NOMBRE_TABLA+ "." + TipoCampoCategoriaColumns.NOMBRE 
																  + " AS " + CampoCategoriaColumns.NOMBRE_TIPO);
		sCamposProjectionMap.put(CampoCategoriaColumns.ID_CATEGORIA, CamposCategoriaTable.NOMBRE_TABLA + "." + CampoCategoriaColumns.ID_CATEGORIA);
		sCamposProjectionMap.put(CampoCategoriaColumns.FECHA_CREACION,	CamposCategoriaTable.NOMBRE_TABLA + "." + CampoCategoriaColumns.FECHA_CREACION);
		sCamposProjectionMap.put(CampoCategoriaColumns.FECHA_MODIFICACION,	CamposCategoriaTable.NOMBRE_TABLA + "." + CampoCategoriaColumns.FECHA_MODIFICACION);
	}
}