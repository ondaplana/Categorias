package es.gluetech.categorias.db.table;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Definiciones para TiposCampoCategoriaProvider
 */
public final class TiposCampoCategoriaTable {
    public static final String AUTHORITY = "es.gluetech.categorias.tiposcampocategoriaprovider";
    
	public static final String NOMBRE_TABLA = "tipos_campo_categoria";


    // Esta clase no puede ser instanciada
    private TiposCampoCategoriaTable() {}
    
    /**
     * Categorias tabla
     */
    public static final class TipoCampoCategoriaColumns implements BaseColumns {
        
    	// Esta clase no puede ser instnaciada
        private TipoCampoCategoriaColumns() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/tiposcampo");
        
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gluetech.tipocampo";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.gluetech.tipocampo";

        /**
         * El nombre del campo
         * <P>Type: STRING</P>
         */
        public static final String NOMBRE = "nombre";
        
        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = TipoCampoCategoriaColumns.NOMBRE + " ASC";
       
    }
}