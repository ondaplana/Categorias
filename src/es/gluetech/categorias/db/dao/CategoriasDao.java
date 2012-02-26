package es.gluetech.categorias.db.dao;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import es.gluetech.categorias.bean.Campo;
import es.gluetech.categorias.bean.TipoCampo;
import es.gluetech.categorias.db.DatabaseHelper;
import es.gluetech.categorias.db.table.CamposCategoriaTable.CampoCategoriaColumns;
import es.gluetech.categorias.db.table.CategoriasRelDocumentosTable;
import es.gluetech.categorias.db.table.CategoriasRelDocumentosTable.CategoriaRelDocumentoColumns;
import es.gluetech.categorias.db.table.CategoriasTable;
import es.gluetech.categorias.db.table.CategoriasTable.CategoriaColumns;
import es.gluetech.categorias.db.table.DocumentosCategoriaTable;
import es.gluetech.categorias.db.table.DocumentosCategoriaTable.DocumentoCategoriaColumns;

public class CategoriasDao {
	
	DatabaseHelper mOpenHelper = null;
	
    /**
     * Proyeccion de la columnas de un campo de una categoria
     */
    private static final String[] PROJECTION_CATEGORIA = new String[] {
        CategoriaColumns._ID, // 0
        CategoriaColumns.NOMBRE, // 1
        CategoriaColumns.ID_CATEGORIA_PADRE, // 2
    };
	
    /**
     * Devuelve el listado de elementos, categorias y documentos, de la categoria
     * @param contexto
     * @param idCategoria
     * @return
     */
	public Cursor getListadoValoresByCategoria(Context contexto, String idCategoria) {
			
		SQLiteDatabase db = null;
		mOpenHelper = new DatabaseHelper(contexto);
		db =  mOpenHelper.getReadableDatabase();
		
		String consulta = "select " + CategoriaColumns._ID + ","
				+ CategoriaColumns.NOMBRE + ","
				+ CategoriaColumns.FECHA_CREACION + ","
				+ CategoriaColumns.FECHA_MODIFICACION + ","
				+ "'CATEGORIA' as tipo "
				+ "from " + CategoriasTable.NOMBRE_TABLA + " "
				+ "where " + CategoriaColumns.ID_CATEGORIA_PADRE + "=? "
				+ "union "
				+ "select "
				+ DocumentosCategoriaTable.NOMBRE_TABLA + "." + DocumentoCategoriaColumns._ID + ","
				+ DocumentosCategoriaTable.NOMBRE_TABLA + "." + DocumentoCategoriaColumns.NOMBRE + ","
				+ DocumentosCategoriaTable.NOMBRE_TABLA + "." + DocumentoCategoriaColumns.FECHA_CREACION + ","
				+ DocumentosCategoriaTable.NOMBRE_TABLA + "." + DocumentoCategoriaColumns.FECHA_MODIFICACION + ","
				+ "'VALOR' as tipo "
				+ "from " + DocumentosCategoriaTable.NOMBRE_TABLA + " "
				+ "inner join " + CategoriasRelDocumentosTable.NOMBRE_TABLA + " on "
				+ DocumentosCategoriaTable.NOMBRE_TABLA + "." + DocumentoCategoriaColumns._ID
				+ "="
				+ CategoriasRelDocumentosTable.NOMBRE_TABLA + "." + CategoriaRelDocumentoColumns.ID_DOCUMENTO + " "
				+ "where " + CategoriaRelDocumentoColumns.ID_CATEGORIA + "=? "
				+ "order by tipo, nombre;";
		    
		return db.rawQuery(consulta, new String[]{ idCategoria, idCategoria });
		
		// TODO: pendiente ver si hay que cerrar la conexion

	}
	
	/**
	 * Devuelve el listado de campos de la categoria y sus antecesores
	 * @param contexto
	 * @param idCategoria
	 * @return
	 */
	public ArrayList<Campo> getCamposByCategoria(Context contexto, String idCategoria) {

		ArrayList<Campo> listaCampos = new ArrayList<Campo>();
    	Cursor cursorCategoria = null;
    	Cursor cursorCampos = null;
    	boolean fin = false;
    	
    	try {
    		
    		String idCategoriaGrupo = idCategoria;
    		
    		while (!fin) {
	    	    			
    			// Primero se recupera el grupo: el nombre de la categoria
	    		Uri uriCategoria = Uri.withAppendedPath(CategoriaColumns.CONTENT_URI, idCategoriaGrupo);
	    		cursorCategoria = contexto.getContentResolver().query(uriCategoria, PROJECTION_CATEGORIA, null, null, null);
	    		cursorCategoria.moveToFirst();
	    		int index_nombre_categoria = cursorCategoria.getColumnIndex(CategoriaColumns.NOMBRE);
	    		String grupo = cursorCategoria.getString(index_nombre_categoria);
	    		
	            // Se recuperan los campos de la categoria
	            Uri uri = Uri.withAppendedPath(CampoCategoriaColumns.CONTENT_URI_CATEGORIA, idCategoriaGrupo);
	            cursorCampos = contexto.getContentResolver().query(uri, new String[] { CampoCategoriaColumns._ID, // 0
	            											CampoCategoriaColumns.NOMBRE, // 1
	            											CampoCategoriaColumns.ID_TIPO, // 2
	            											CampoCategoriaColumns.NOMBRE_TIPO, // 3
	            										  }, null, null, null);
	            cursorCampos.moveToFirst();
	            int index_id = cursorCampos.getColumnIndex(CampoCategoriaColumns._ID); 
	            int index_nombre = cursorCampos.getColumnIndex(CampoCategoriaColumns.NOMBRE); 
	            int index_nombre_tipo = cursorCampos.getColumnIndex(CampoCategoriaColumns.NOMBRE_TIPO);
	            int index_id_tipo = cursorCampos.getColumnIndex(CampoCategoriaColumns.ID_TIPO);
		        while (cursorCampos.isAfterLast() == false) {
		        	TipoCampo tipoCampo = new TipoCampo(cursorCampos.getInt(index_id_tipo), cursorCampos.getString(index_nombre_tipo));
		        	listaCampos.add(new Campo(grupo,
		        							  cursorCampos.getInt(index_id),
		        							  cursorCampos.getString(index_nombre),
		        							  tipoCampo));
		        	cursorCampos.move(1);
		        }
		        
		        // Se actualiza el identificador de categoria al de su padre, salvo que este sea cero
		        // y no tener una categoria padre
	    		int index_categoria_padre = cursorCategoria.getColumnIndex(CategoriaColumns.ID_CATEGORIA_PADRE);
		        String idCategoriaPadre = cursorCategoria.getString(index_categoria_padre);
		        if ((idCategoriaPadre != null) && (!"0".equals(idCategoriaPadre))) {
		        	idCategoriaGrupo = idCategoriaPadre;
		        } else {
		        	fin = true;
		        }
    		
    		}
   		
    	}
    	finally {
    		if (cursorCategoria!=null) {
    			cursorCategoria.close();
    		}
    		if (cursorCampos!=null) {
    			cursorCampos.close();
    		}
    	}

		return listaCampos;
	}

}
