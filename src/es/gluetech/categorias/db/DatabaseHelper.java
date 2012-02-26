package es.gluetech.categorias.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import es.gluetech.categorias.bean.TipoCampo;
import es.gluetech.categorias.db.table.CamposCategoriaTable;
import es.gluetech.categorias.db.table.CamposCategoriaTable.CampoCategoriaColumns;
import es.gluetech.categorias.db.table.CamposDocumentoCategoriaTable;
import es.gluetech.categorias.db.table.CamposDocumentoCategoriaTable.CampoDocumentoCategoriaColumns;
import es.gluetech.categorias.db.table.CategoriasRelDocumentosTable;
import es.gluetech.categorias.db.table.CategoriasRelDocumentosTable.CategoriaRelDocumentoColumns;
import es.gluetech.categorias.db.table.CategoriasTable;
import es.gluetech.categorias.db.table.CategoriasTable.CategoriaColumns;
import es.gluetech.categorias.db.table.TiposCampoCategoriaTable;
import es.gluetech.categorias.db.table.TiposCampoCategoriaTable.TipoCampoCategoriaColumns;
import es.gluetech.categorias.db.table.DocumentosCategoriaTable;
import es.gluetech.categorias.db.table.DocumentosCategoriaTable.DocumentoCategoriaColumns;

/**
 * Esta clase ayuda a crear, abrir y actualizar la base de datos.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
    private static final String TAG = "DatabaseHelper";
	
    private static final String DATABASE_NAME = "categorias.db";
    private static final int DATABASE_VERSION = 2;
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	
       	db.execSQL("CREATE TABLE " + CategoriasTable.NOMBRE_TABLA + " ("
                + CategoriaColumns._ID + " INTEGER PRIMARY KEY,"
                + CategoriaColumns.NOMBRE + " TEXT,"
                + CategoriaColumns.ID_CATEGORIA_PADRE + " INTEGER,"
                + CategoriaColumns.FECHA_CREACION + " INTEGER,"
                + CategoriaColumns.FECHA_MODIFICACION + " INTEGER,"
				+ "FOREIGN KEY (" + CategoriaColumns.ID_CATEGORIA_PADRE + ") " 
        		+ "REFERENCES " + CategoriasTable.NOMBRE_TABLA + "(" + CategoriaColumns._ID + ") "
        		+ "ON DELETE CASCADE"
        		+ ");");
		db.execSQL("INSERT INTO " + CategoriasTable.NOMBRE_TABLA + " (" 
				+ CategoriaColumns._ID + "," 
				+ CategoriaColumns.NOMBRE + "," 
				+ CategoriaColumns.ID_CATEGORIA_PADRE + ") "
				+ "VALUES (0, 'Categorias', NULL);");
		db.execSQL("INSERT INTO " + CategoriasTable.NOMBRE_TABLA + " (" 
				+ CategoriaColumns._ID + "," 
				+ CategoriaColumns.NOMBRE + "," 
				+ CategoriaColumns.ID_CATEGORIA_PADRE + ") "
				+ "VALUES (1, 'Finanzas', 0);");
		db.execSQL("INSERT INTO " + CategoriasTable.NOMBRE_TABLA + " (" 
				+ CategoriaColumns._ID + "," 
				+ CategoriaColumns.NOMBRE + "," 
				+ CategoriaColumns.ID_CATEGORIA_PADRE + ") "
				+ "VALUES (2, 'Gastos', 1);");
		db.execSQL("INSERT INTO " + CategoriasTable.NOMBRE_TABLA + " (" 
				+ CategoriaColumns._ID + "," 
				+ CategoriaColumns.NOMBRE + "," 
				+ CategoriaColumns.ID_CATEGORIA_PADRE + ") "
				+ "VALUES (3, 'Ingresos', 1);");
		db.execSQL("INSERT INTO " + CategoriasTable.NOMBRE_TABLA + " (" 
				+ CategoriaColumns._ID + "," 
				+ CategoriaColumns.NOMBRE + "," 
				+ CategoriaColumns.ID_CATEGORIA_PADRE + ") "
				+ "VALUES (4, 'Vinos', 0);");
		db.execSQL("INSERT INTO " + CategoriasTable.NOMBRE_TABLA + " (" 
				+ CategoriaColumns._ID + "," 
				+ CategoriaColumns.NOMBRE + "," 
				+ CategoriaColumns.ID_CATEGORIA_PADRE + ") "
				+ "VALUES (5, 'Personas', 0);");	
		db.execSQL("INSERT INTO " + CategoriasTable.NOMBRE_TABLA + " (" 
				+ CategoriaColumns._ID + "," 
				+ CategoriaColumns.NOMBRE + "," 
				+ CategoriaColumns.ID_CATEGORIA_PADRE + ") "
				+ "VALUES (6, 'José', 5);");	
		db.execSQL("INSERT INTO " + CategoriasTable.NOMBRE_TABLA + " (" 
				+ CategoriaColumns._ID + "," 
				+ CategoriaColumns.NOMBRE + "," 
				+ CategoriaColumns.ID_CATEGORIA_PADRE + ") "
				+ "VALUES (7, 'Ana', 5);");	
		db.execSQL("INSERT INTO " + CategoriasTable.NOMBRE_TABLA + " (" 
				+ CategoriaColumns._ID + "," 
				+ CategoriaColumns.NOMBRE + "," 
				+ CategoriaColumns.ID_CATEGORIA_PADRE + ") "
				+ "VALUES (8, 'Supermercado', 0);");	
		
        db.execSQL("CREATE TABLE " + CamposCategoriaTable.NOMBRE_TABLA + " ("
				+ CampoCategoriaColumns._ID + " INTEGER PRIMARY KEY,"
				+ CampoCategoriaColumns.NOMBRE + " TEXT,"
				+ CampoCategoriaColumns.ID_TIPO + " INTEGER,"
				+ CampoCategoriaColumns.ID_CATEGORIA + " INTEGER,"
				+ CampoCategoriaColumns.FECHA_CREACION + " INTEGER,"
				+ CampoCategoriaColumns.FECHA_MODIFICACION + " INTEGER,"
				+ "FOREIGN KEY (" + CampoCategoriaColumns.ID_CATEGORIA + ") " 
        		+ "REFERENCES " + CategoriasTable.NOMBRE_TABLA + "(" + CategoriaColumns._ID + ") "
        		+ "ON DELETE CASCADE"
        		+ ");");
		db.execSQL("INSERT INTO " + CamposCategoriaTable.NOMBRE_TABLA + " (" 
				+ CampoCategoriaColumns._ID + "," 
				+ CampoCategoriaColumns.NOMBRE + ","
				+ CampoCategoriaColumns.ID_TIPO + "," 
				+ CampoCategoriaColumns.ID_CATEGORIA + ") "
				+ "VALUES (1, 'Importe', 2, 1);");
		db.execSQL("INSERT INTO " + CamposCategoriaTable.NOMBRE_TABLA + " (" 
				+ CampoCategoriaColumns._ID + "," 
				+ CampoCategoriaColumns.NOMBRE + ","
				+ CampoCategoriaColumns.ID_TIPO + "," 
				+ CampoCategoriaColumns.ID_CATEGORIA + ") "
				+ "VALUES (2, 'Comentario', 1, 4);");
		db.execSQL("INSERT INTO " + CamposCategoriaTable.NOMBRE_TABLA + " (" 
				+ CampoCategoriaColumns._ID + "," 
				+ CampoCategoriaColumns.NOMBRE + ","
				+ CampoCategoriaColumns.ID_TIPO + "," 
				+ CampoCategoriaColumns.ID_CATEGORIA + ") "
				+ "VALUES (3, 'Precio Mercadona', 2, 8);");	
		db.execSQL("INSERT INTO " + CamposCategoriaTable.NOMBRE_TABLA + " (" 
				+ CampoCategoriaColumns._ID + "," 
				+ CampoCategoriaColumns.NOMBRE + ","
				+ CampoCategoriaColumns.ID_TIPO + "," 
				+ CampoCategoriaColumns.ID_CATEGORIA + ") "
				+ "VALUES (4, 'Precio Carrefour', 2, 8);");	
        		        
        db.execSQL("CREATE TABLE " + DocumentosCategoriaTable.NOMBRE_TABLA + " ("
                + DocumentoCategoriaColumns._ID + " INTEGER PRIMARY KEY,"
                + DocumentoCategoriaColumns.NOMBRE + " TEXT,"
                //+ DocumentoCategoriaColumns.ID_CATEGORIA + " INTEGER,"
                + DocumentoCategoriaColumns.FECHA_CREACION + " INTEGER,"
                + DocumentoCategoriaColumns.FECHA_MODIFICACION + " INTEGER"
                + ");");
				//+ "FOREIGN KEY (" + DocumentoCategoriaColumns.ID_CATEGORIA + ") " 
        		//+ "REFERENCES " + CategoriasTable.NOMBRE_TABLA + "(" + CategoriaColumns._ID + ") "
        		//+ "ON DELETE CASCADE"
        		//+ ");");
		db.execSQL("INSERT INTO " + DocumentosCategoriaTable.NOMBRE_TABLA + " (" 
				+ DocumentoCategoriaColumns._ID + "," 
				+ DocumentoCategoriaColumns.NOMBRE + ") " 
				+ "VALUES (1, 'Gasto1');");
		db.execSQL("INSERT INTO " + DocumentosCategoriaTable.NOMBRE_TABLA + " (" 
				+ DocumentoCategoriaColumns._ID + "," 
				+ DocumentoCategoriaColumns.NOMBRE + ") " 
				+ "VALUES (2, 'Ingreso1');");
		db.execSQL("INSERT INTO " + DocumentosCategoriaTable.NOMBRE_TABLA + " (" 
				+ DocumentoCategoriaColumns._ID + "," 
				+ DocumentoCategoriaColumns.NOMBRE + ") " 
				+ "VALUES (3, 'Barra de pan');");
		
		db.execSQL("CREATE TABLE " + TiposCampoCategoriaTable.NOMBRE_TABLA + " ("
				+ TipoCampoCategoriaColumns._ID + " INTEGER PRIMARY KEY,"
				+ TipoCampoCategoriaColumns.NOMBRE + " TEXT" + ");");
		db.execSQL("INSERT INTO " + TiposCampoCategoriaTable.NOMBRE_TABLA + " (" 
				+ TipoCampoCategoriaColumns._ID + "," 
				+ TipoCampoCategoriaColumns.NOMBRE + ") "
				+ "VALUES (1, '"+ TipoCampo.TEXTO +"');");
		db.execSQL("INSERT INTO " + TiposCampoCategoriaTable.NOMBRE_TABLA + " (" 
				+ TipoCampoCategoriaColumns._ID + "," 
				+ TipoCampoCategoriaColumns.NOMBRE + ") "
				+ "VALUES (2, '"+ TipoCampo.NUMERICO +"');");
		
		db.execSQL("CREATE TABLE " + CamposDocumentoCategoriaTable.NOMBRE_TABLA + " ("
				+ CampoDocumentoCategoriaColumns._ID + " INTEGER PRIMARY KEY,"
				+ CampoDocumentoCategoriaColumns.VALOR + " TEXT,"
				+ CampoDocumentoCategoriaColumns.ID_DOCUMENTO_CATEGORIA + " INTEGER,"
				+ CampoDocumentoCategoriaColumns.ID_CAMPO_CATEGORIA + " INTEGER,"
				+ CampoDocumentoCategoriaColumns.FECHA_CREACION + " INTEGER,"
				+ CampoDocumentoCategoriaColumns.FECHA_MODIFICACION + " INTEGER,"
				+ "FOREIGN KEY (" + CampoDocumentoCategoriaColumns.ID_DOCUMENTO_CATEGORIA + ") " 
				+ "REFERENCES " + DocumentosCategoriaTable.NOMBRE_TABLA + "(" + DocumentoCategoriaColumns._ID + ") "
				+ "ON DELETE CASCADE,"
				+ "FOREIGN KEY (" + CampoDocumentoCategoriaColumns.ID_CAMPO_CATEGORIA + ") " 
				+ "REFERENCES " + CamposCategoriaTable.NOMBRE_TABLA + "(" + CampoCategoriaColumns._ID + ") "
				+ "ON DELETE CASCADE"
				+ ");");
		db.execSQL("INSERT INTO " + CamposDocumentoCategoriaTable.NOMBRE_TABLA + " (" 
				+ CampoDocumentoCategoriaColumns._ID + "," 
				+ CampoDocumentoCategoriaColumns.VALOR + "," 
				+ CampoDocumentoCategoriaColumns.ID_DOCUMENTO_CATEGORIA + "," 
				+ CampoDocumentoCategoriaColumns.ID_CAMPO_CATEGORIA + ") "
				+ "VALUES (1, '0,60', 3, 3);");
		db.execSQL("INSERT INTO " + CamposDocumentoCategoriaTable.NOMBRE_TABLA + " (" 
				+ CampoDocumentoCategoriaColumns._ID + "," 
				+ CampoDocumentoCategoriaColumns.VALOR + "," 
				+ CampoDocumentoCategoriaColumns.ID_DOCUMENTO_CATEGORIA + "," 
				+ CampoDocumentoCategoriaColumns.ID_CAMPO_CATEGORIA + ") "
				+ "VALUES (2, '0,45', 3, 4);");
		db.execSQL("INSERT INTO " + CamposDocumentoCategoriaTable.NOMBRE_TABLA + " (" 
				+ CampoDocumentoCategoriaColumns._ID + "," 
				+ CampoDocumentoCategoriaColumns.VALOR + "," 
				+ CampoDocumentoCategoriaColumns.ID_DOCUMENTO_CATEGORIA + "," 
				+ CampoDocumentoCategoriaColumns.ID_CAMPO_CATEGORIA + ") "
				+ "VALUES (3, '45', 1, 1);");
		db.execSQL("INSERT INTO " + CamposDocumentoCategoriaTable.NOMBRE_TABLA + " (" 
				+ CampoDocumentoCategoriaColumns._ID + "," 
				+ CampoDocumentoCategoriaColumns.VALOR + "," 
				+ CampoDocumentoCategoriaColumns.ID_DOCUMENTO_CATEGORIA + "," 
				+ CampoDocumentoCategoriaColumns.ID_CAMPO_CATEGORIA + ") "
				+ "VALUES (4, '1200', 2, 1);");
		
		db.execSQL("CREATE TABLE " + CategoriasRelDocumentosTable.NOMBRE_TABLA + " ("
				+ CategoriaRelDocumentoColumns._ID + " INTEGER PRIMARY KEY,"
				+ CategoriaRelDocumentoColumns.ID_CATEGORIA + " TEXT,"
				+ CategoriaRelDocumentoColumns.ID_DOCUMENTO + " INTEGER,"
				+ CategoriaRelDocumentoColumns.FECHA_CREACION + " INTEGER,"
				+ CategoriaRelDocumentoColumns.FECHA_MODIFICACION + " INTEGER,"
				+ "FOREIGN KEY (" + CategoriaRelDocumentoColumns.ID_CATEGORIA + ") " 
				+ "REFERENCES " + CategoriasTable.NOMBRE_TABLA + "(" + CategoriaColumns._ID + ") "
				+ "ON DELETE CASCADE,"
				+ "FOREIGN KEY (" + CategoriaRelDocumentoColumns.ID_DOCUMENTO + ") " 
				+ "REFERENCES " + DocumentosCategoriaTable.NOMBRE_TABLA + "(" + DocumentoCategoriaColumns._ID + ") "
				+ "ON DELETE CASCADE"
				+ ");");
		db.execSQL("INSERT INTO " + CategoriasRelDocumentosTable.NOMBRE_TABLA + " (" 
				+ CategoriaRelDocumentoColumns._ID + "," 
				+ CategoriaRelDocumentoColumns.ID_CATEGORIA + "," 
				+ CategoriaRelDocumentoColumns.ID_DOCUMENTO + ") " 
				+ "VALUES (1, 2, 1);");
		db.execSQL("INSERT INTO " + CategoriasRelDocumentosTable.NOMBRE_TABLA + " (" 
				+ CategoriaRelDocumentoColumns._ID + "," 
				+ CategoriaRelDocumentoColumns.ID_CATEGORIA + "," 
				+ CategoriaRelDocumentoColumns.ID_DOCUMENTO + ") " 
				+ "VALUES (2, 3, 2);");
		db.execSQL("INSERT INTO " + CategoriasRelDocumentosTable.NOMBRE_TABLA + " (" 
				+ CategoriaRelDocumentoColumns._ID + "," 
				+ CategoriaRelDocumentoColumns.ID_CATEGORIA + "," 
				+ CategoriaRelDocumentoColumns.ID_DOCUMENTO + ") " 
				+ "VALUES (3, 8, 3);");
		
    }

    @Override
	public void onOpen(SQLiteDatabase db) {
    	db.execSQL("PRAGMA foreign_keys=ON;");
		// TODO Auto-generated method stub
		super.onOpen(db);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + CategoriasRelDocumentosTable.NOMBRE_TABLA);
		db.execSQL("DROP TABLE IF EXISTS " + CamposCategoriaTable.NOMBRE_TABLA);
		db.execSQL("DROP TABLE IF EXISTS " + CamposDocumentoCategoriaTable.NOMBRE_TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + DocumentosCategoriaTable.NOMBRE_TABLA);
		db.execSQL("DROP TABLE IF EXISTS " + CategoriasTable.NOMBRE_TABLA);
		db.execSQL("DROP TABLE IF EXISTS " + TiposCampoCategoriaTable.NOMBRE_TABLA);
		onCreate(db);
    }
}