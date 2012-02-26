package es.gluetech.categorias.db.table;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Definiciones para DocumentosCategoriaProvider
 */
public final class DocumentosCategoriaTable {
    public static final String AUTHORITY = "es.gluetech.categorias.documentoscategoriaprovider";
    
	public static final String NOMBRE_TABLA = "documentos_categoria";


    // Esta clase no puede ser instanciada
    private DocumentosCategoriaTable() {}
    
    /**
     * Tabla de documentos de una categoria
     */
    public static final class DocumentoCategoriaColumns implements BaseColumns {
        // Esta clase no puede ser instnaciada
        private DocumentoCategoriaColumns() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/documentos");

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI_CATEGORIA = Uri.parse("content://" + AUTHORITY + "/documentos/categoria");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gluetech.categoria.documento";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.gluetech.categoria.documento";

        /**
         * El nombre de la categoria
         * <P>Type: TEXT</P>
         */
        public static final String NOMBRE = "nombre";
        
        /**
         * El nombre de la categoria
         * <P>Type: TEXT</P>
         */
        public static final String ID_CATEGORIA = "id_categoria";

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
