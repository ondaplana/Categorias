package es.gluetech.categorias.db.table;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Definiciones para CamposDocumentoCategoriaProvider
 */
public final class CamposDocumentoCategoriaTable {
    public static final String AUTHORITY = "es.gluetech.categorias.camposdocumentocategoriaprovider";
    
	public static final String NOMBRE_TABLA = "campos_documento_categoria";


    // Esta clase no puede ser instanciada
    private CamposDocumentoCategoriaTable() {}
    
    /**
     * Tabla de valores de una categoria
     */
    public static final class CampoDocumentoCategoriaColumns implements BaseColumns {
        // Esta clase no puede ser instnaciada
        private CampoDocumentoCategoriaColumns() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/camposdocumento");

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI_CATEGORIA = Uri.parse("content://" + AUTHORITY + "/camposdocumento/documento");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gluetech.categoria.documento.campo";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.gluetech.categoria.documento.campo";

        /**
         * El valor del campo
         * <P>Type: TEXT</P>
         */
        public static final String VALOR = "valor";
        
        /**
         * El nombre de la categoria
         * <P>Type: INTEGER</P>
         */
        public static final String ID_CAMPO_CATEGORIA = "id_campo_categoria";

        /**
         * El nombre de la categoria
         * <P>Type: INTEGER</P>
         */
        public static final String ID_DOCUMENTO_CATEGORIA = "id_documento_categoria";
        
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
        public static final String DEFAULT_SORT_ORDER = ID_CAMPO_CATEGORIA + " ASC";
    }
}
