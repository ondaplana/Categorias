package es.gluetech.categorias.db.table;

import android.net.Uri;
import android.provider.BaseColumns;

public final class CategoriasRelDocumentosTable {
    public static final String AUTHORITY = "es.gluetech.categorias.categoriasreldocumentosprovider";
    
    public static final String NOMBRE_TABLA = "categorias_r_documentos";

    // Esta clase no puede ser instanciada
    private CategoriasRelDocumentosTable() {}
    
    /**
     * Tabla de categorias
     */
    public static final class CategoriaRelDocumentoColumns implements BaseColumns {
        // Esta clase no puede ser instnaciada
        private CategoriaRelDocumentoColumns() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/categoriasreldocumentos");


        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gluetech.categoriareldocumentos";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.gluetech.categoriareldocumentos";

        /**
         * El nombre de la categoria
         * <P>Type: INTEGER</P>
         */
        public static final String ID_CATEGORIA = "id_categoria";
        
        /**
         * El nombre de la categoria
         * <P>Type: INTEGER</P>
         */
        public static final String ID_DOCUMENTO = "id_documento";

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
        public static final String DEFAULT_SORT_ORDER = _ID + " ASC";
    }
    
}
