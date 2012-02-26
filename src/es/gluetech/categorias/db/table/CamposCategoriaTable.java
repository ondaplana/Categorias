package es.gluetech.categorias.db.table;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Definiciones para CamposCategoriaProvider
 */
public final class CamposCategoriaTable {
    public static final String AUTHORITY = "es.gluetech.categorias.camposcategoriaprovider";
    
	public static final String NOMBRE_TABLA = "campos_categoria";

    // Esta clase no puede ser instanciada
    private CamposCategoriaTable() {}
    
    /**
     * Categorias tabla
     */
    public static final class CampoCategoriaColumns implements BaseColumns {
        
    	// Esta clase no puede ser instnaciada
        private CampoCategoriaColumns() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/campos");
        
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI_CATEGORIA = Uri.parse("content://" + AUTHORITY + "/campos/categoria");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gluetech.categoria.campo";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.gluetech.categoria.campo";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = CampoCategoriaColumns.NOMBRE + " ASC";

        /**
         * El nombre del campo
         * <P>Type: STRING</P>
         */
        public static final String NOMBRE = "nombre";
        
        /**
         * El identificador del tipo de campo
         * <P>Type: INTEGER</P>
         */
        public static final String ID_TIPO = "id_tipo";
        
        /**
         * El nombre del tipo de campo
         * <P>Type: STRING</P>
         */
        public static final String NOMBRE_TIPO = "nombre_tipo";
        
        /**
         * El identificador de la categoria asociada al campo
         * <P>Type: INTEGER</P>
         */
        public static final String ID_CATEGORIA = "id_categoria";
        
        /**
         * La fecha cuando el campo fue creado
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String FECHA_CREACION = "fecha_creacion";

        /**
         * La fecha cuando el campo fue modificado
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String FECHA_MODIFICACION = "fecha_modificacion";
        
    }
}