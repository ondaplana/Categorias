
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
import es.gluetech.categorias.db.table.CategoriasRelDocumentosTable;
import es.gluetech.categorias.db.table.CategoriasTable;
import es.gluetech.categorias.db.table.CategoriasRelDocumentosTable.CategoriaRelDocumentoColumns;
import es.gluetech.categorias.db.table.CategoriasTable.CategoriaColumns;
import es.gluetech.categorias.db.table.DocumentosCategoriaTable;
import es.gluetech.categorias.db.table.TiposCampoCategoriaTable;
import es.gluetech.categorias.db.table.CamposCategoriaTable.CampoCategoriaColumns;
import es.gluetech.categorias.db.table.DocumentosCategoriaTable.DocumentoCategoriaColumns;
import es.gluetech.categorias.db.table.TiposCampoCategoriaTable.TipoCampoCategoriaColumns;

/**
 * Provee acceso a la tabla de documentos de una categoria
 */
public class DocumentosCategoriaProvider extends ContentProvider {
	
    private static HashMap<String, String> sProjectionMap;

    private static final int VALORES = 1;
    private static final int VALORES_ID = 2;
    private static final int VALORES_ID_CATEGORIA = 3;

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
        qb.setProjectionMap(sProjectionMap);
    	qb.setTables(DocumentosCategoriaTable.NOMBRE_TABLA
      		     + " INNER JOIN " 
			     + CategoriasRelDocumentosTable.NOMBRE_TABLA
				 + " ON ("
			     + DocumentosCategoriaTable.NOMBRE_TABLA + "." + DocumentoCategoriaColumns._ID
			     + "="
			     + CategoriasRelDocumentosTable.NOMBRE_TABLA + "." + CategoriaRelDocumentoColumns.ID_DOCUMENTO + ")");

        switch (sUriMatcher.match(uri)) {
        case VALORES:
            break;

        case VALORES_ID:
            qb.appendWhere(DocumentosCategoriaTable.NOMBRE_TABLA + "." + DocumentoCategoriaColumns._ID + "=" + uri.getPathSegments().get(1));
            break;
            
        case VALORES_ID_CATEGORIA:
    		qb.appendWhere(CategoriaRelDocumentoColumns.ID_CATEGORIA + "=" + uri.getPathSegments().get(2));
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = DocumentoCategoriaColumns.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), DocumentoCategoriaColumns.CONTENT_URI);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
     
        case VALORES:
            return DocumentoCategoriaColumns.CONTENT_TYPE;
            
        case VALORES_ID_CATEGORIA:
            return DocumentoCategoriaColumns.CONTENT_TYPE;

        case VALORES_ID:
            return DocumentoCategoriaColumns.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != VALORES) {
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
        if (values.containsKey(DocumentoCategoriaColumns.FECHA_CREACION) == false) {
            values.put(DocumentoCategoriaColumns.FECHA_CREACION, now);
        }

        if (values.containsKey(DocumentoCategoriaColumns.FECHA_MODIFICACION) == false) {
            values.put(DocumentoCategoriaColumns.FECHA_MODIFICACION, now);
        }

        if (values.containsKey(DocumentoCategoriaColumns.NOMBRE) == false) {
            values.put(DocumentoCategoriaColumns.NOMBRE, "");
        }
        
        String idCategoria = null;
        if (values.containsKey(DocumentoCategoriaColumns.ID_CATEGORIA) == true) {
        	idCategoria = values.getAsString(DocumentoCategoriaColumns.ID_CATEGORIA);
        	values.remove(DocumentoCategoriaColumns.ID_CATEGORIA);
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(DocumentosCategoriaTable.NOMBRE_TABLA, null, values);
        
        long rowId2 = 0;
        if (idCategoria != null) {
        	values.clear();
        	values.put(CategoriaRelDocumentoColumns.ID_CATEGORIA, idCategoria);
        	values.put(CategoriaRelDocumentoColumns.ID_DOCUMENTO, String.valueOf(rowId));
        	rowId2 = db.insert(CategoriasRelDocumentosTable.NOMBRE_TABLA, null, values);
        }
                
        if (rowId > 0 && rowId2 > 0) {
            Uri categoriaUri = ContentUris.withAppendedId(DocumentoCategoriaColumns.CONTENT_URI, rowId);
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
        case VALORES:
            count = db.delete(DocumentosCategoriaTable.NOMBRE_TABLA, where, whereArgs);
            break;

        case VALORES_ID:
            String idValor = uri.getPathSegments().get(1);
            count = db.delete(DocumentosCategoriaTable.NOMBRE_TABLA, DocumentoCategoriaColumns._ID + "=" + idValor
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
        case VALORES:
            count = db.update(DocumentosCategoriaTable.NOMBRE_TABLA, values, where, whereArgs);
            break;

        case VALORES_ID:
            String idValor = uri.getPathSegments().get(1);
            count = db.update(DocumentosCategoriaTable.NOMBRE_TABLA, values, DocumentoCategoriaColumns._ID + "=" + idValor
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
        sUriMatcher.addURI(DocumentosCategoriaTable.AUTHORITY, "documentos", VALORES);
        sUriMatcher.addURI(DocumentosCategoriaTable.AUTHORITY, "documentos/#", VALORES_ID);
        sUriMatcher.addURI(DocumentosCategoriaTable.AUTHORITY, "documentos/categoria/#", VALORES_ID_CATEGORIA);
        
        sProjectionMap = new HashMap<String, String>();
        sProjectionMap.put(DocumentoCategoriaColumns._ID, DocumentosCategoriaTable.NOMBRE_TABLA + "." + DocumentoCategoriaColumns._ID);
        sProjectionMap.put(DocumentoCategoriaColumns.NOMBRE, DocumentosCategoriaTable.NOMBRE_TABLA + "." + DocumentoCategoriaColumns.NOMBRE);
        sProjectionMap.put(CategoriaRelDocumentoColumns.ID_CATEGORIA, CategoriasRelDocumentosTable.NOMBRE_TABLA + "." + CategoriaRelDocumentoColumns.ID_CATEGORIA);
        sProjectionMap.put(DocumentoCategoriaColumns.FECHA_CREACION, DocumentosCategoriaTable.NOMBRE_TABLA + "." + DocumentoCategoriaColumns.FECHA_CREACION);
        sProjectionMap.put(DocumentoCategoriaColumns.FECHA_MODIFICACION, DocumentosCategoriaTable.NOMBRE_TABLA + "." + DocumentoCategoriaColumns.FECHA_MODIFICACION);
     }
}