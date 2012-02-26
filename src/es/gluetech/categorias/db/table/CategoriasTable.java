package es.gluetech.categorias.db.table;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Definiciones para CategoriasProvider
 */
public final class CategoriasTable {
    public static final String AUTHORITY = "es.gluetech.categorias.categoriasprovider";
    
    public static final String NOMBRE_TABLA = "categorias";

    // Esta clase no puede ser instanciada
    private CategoriasTable() {}
    
    /**
     * Tabla de categorias
     */
    public static final class CategoriaColumns implements BaseColumns {
        // Esta clase no puede ser instnaciada
        private CategoriaColumns() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/categorias");

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI_PADRE = Uri.parse("content://" + AUTHORITY + "/categorias/padre");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gluetech.categoria";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.gluetech.categoria";

        /**
         * El nombre de la categoria
         * <P>Type: TEXT</P>
         */
        public static final String NOMBRE = "nombre";
        
        /**
         * El nombre de la categoria
         * <P>Type: TEXT</P>
         */
        public static final String ID_CATEGORIA_PADRE = "id_categoria_padre";

        /**
         * La fecha cuando la categoria fue creada
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String FECHA_CREACION = "fecha_creacion";

        /**
         * La fecha cuando la categoria fue modificada
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String FECHA_MODIFICACION = "fecha_modificacion";
        
        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = NOMBRE + " ASC";
    }
    
}
