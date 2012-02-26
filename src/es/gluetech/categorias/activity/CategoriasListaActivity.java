package es.gluetech.categorias.activity;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import es.gluetech.categorias.R;
import es.gluetech.categorias.db.dao.CategoriasDao;
import es.gluetech.categorias.db.table.CategoriasTable.CategoriaColumns;
import es.gluetech.categorias.db.table.DocumentosCategoriaTable.DocumentoCategoriaColumns;

/**
 * Visualiza el listado categorias descendientes y documentos de una categoria
 * Permite añadir una nueva categoria descendiente o documento a la categoria
 */
public class CategoriasListaActivity extends ListActivity {

    private static final String TAG = "CategoriasListaActivity";

    /** Indice de la columna nombre */
    private static final int COLUMN_INDEX_NOMBRE = 1;
    
    private SimpleCursorAdapter mAdapter;
    private Cursor mCursor;
    private String mIdCategoriaPadre;
    private TextView mNombreCategoria;
    
    /**
     * Se extiende el ViewBinder por defecto del adaptador de la lista para
     * mostrar un icono diferente si el elemento es una categoria o un documento
     */
    private class MyViewBinder implements SimpleCursorAdapter.ViewBinder {

        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            int viewId = view.getId();
            switch(viewId) {
                case R.id.label:
                    TextView etiqueta = (TextView) view;
                    etiqueta.setText(cursor.getString(columnIndex));
                    return true;
                case R.id.icon:
                    ImageView icono = (ImageView) view;
                    String tipo = cursor.getString(columnIndex);
                    if ("CATEGORIA".equals(tipo)) {
                    	icono.setImageResource(R.drawable.ic_menu_categories);
                    } else {
                    	icono.setImageResource(R.drawable.ic_menu_compose);
                    }
                    return true;
            }
            return false;
        }
        
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
      
        setContentView(R.layout.listado);

        mNombreCategoria = (TextView) findViewById(R.id.lista_nombre_categoria);

        // Si se accede por primera (MAIN activity) vez no hay valores en el 'intent'
        // se crea con el valor de idpadre="0"
        Intent intent = getIntent();
        if (intent.getData() == null) {
        	mIdCategoriaPadre = "0";
            intent.setData(Uri.withAppendedPath(CategoriaColumns.CONTENT_URI_PADRE, mIdCategoriaPadre));
            mNombreCategoria.setText("Categorias");
        } else {
        	mIdCategoriaPadre = getIntent().getData().getPathSegments().get(2);
        	Uri uriCategoriaPadre = Uri.withAppendedPath(CategoriaColumns.CONTENT_URI, mIdCategoriaPadre);
        	Cursor cursorCategoriaPadre = getContentResolver().query(uriCategoriaPadre, new String[] { CategoriaColumns.NOMBRE }, null, null, null);
        	cursorCategoriaPadre.moveToFirst();
        	String nombreCategoria = cursorCategoriaPadre.getString(cursorCategoriaPadre.getColumnIndex(CategoriaColumns.NOMBRE));
        	mNombreCategoria.setText(nombreCategoria);
        }

        // Se configura un menu contextual para los elementos de la lista
        getListView().setOnCreateContextMenuListener(this);
        
        // Se obtienen los elementos de la lista para la categoria
        mCursor = new CategoriasDao().getListadoValoresByCategoria(this, mIdCategoriaPadre);
        startManagingCursor(mCursor);

        // Se mapea el nombre e icono de los elementos al adaptador de la categoria
        mAdapter = new SimpleCursorAdapter(this, R.layout.categoria_lista, mCursor,
                						   new String[] { CategoriaColumns.NOMBRE, "tipo" },
                						   new int[] { R.id.label, R.id.icon });
        
        // Se configura como se muestra la información de los elementos en la lista
        mAdapter.setViewBinder(new MyViewBinder());

        setListAdapter(mAdapter);
        
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		// Se actualizan los valores del listado
		if (mCursor!=null) {
			mCursor.requery();
		}
	}	

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Crea el menu desde el fichero XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.categoria_lista_opciones, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case R.id.menu_insertar_categoria:
	            // Llama a la actividad para insertar una nueva categoria
	            startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
	            return true;
	        case R.id.menu_insertar_valor:
	            // Llama a la actividad para insertar un nuevo valor
	        	Uri uri = Uri.withAppendedPath(DocumentoCategoriaColumns.CONTENT_URI_CATEGORIA, mIdCategoriaPadre);
	            startActivity(new Intent(Intent.ACTION_INSERT, uri));
	            return true;            
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return;
        }

        String tipo;
        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        if (cursor == null) {
            // Si el cursor no esta disponible no se hace nada
            return;
        } else {
        	int index_tipo = cursor.getColumnIndex("tipo");
        	tipo = cursor.getString(index_tipo);
        }

        // Se crea el menu desde el fichero XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.categoria_lista_contexto, menu);
        
        // Se configura el grupo de opciones especificas para un elemento categoria
        if ("CATEGORIA".equals(tipo)) {
        	menu.setGroupVisible(R.id.menu_group_categoria, true);
        } else {
        	menu.setGroupVisible(R.id.menu_group_categoria, false);
        }
        
        // Set the context menu header
        menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_NOMBRE));
    }
 
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return false;
        }
        
        // Se configura la uri segun el tipo de elemento seleccionado
        Uri uriElemento= null;
        if (isCategoria(info.position)) {
        	uriElemento = ContentUris.withAppendedId(CategoriaColumns.CONTENT_URI, info.id);
        } else {
        	uriElemento = ContentUris.withAppendedId(DocumentoCategoriaColumns.CONTENT_URI, info.id);
        }
        
        switch (item.getItemId()) {
        case R.id.context_open:
            // Ejecuta la actividad para ver/editar el elemento de la lista
            startActivity(new Intent(Intent.ACTION_EDIT, uriElemento));
            return true;
        case R.id.context_delete:
            // Borra el elemento seleccinado
        	getContentResolver().delete(uriElemento, null, null);
            // Ejecuta la actividad para ver de nuevo el listado
            startActivity(new Intent(Intent.ACTION_VIEW, getIntent().getData()));
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String action = getIntent().getAction();
        
        // Se establece la uri en funcion del tipo de elemento seleccionado
        Uri uriElemento = ContentUris.withAppendedId(CategoriaColumns.CONTENT_URI_PADRE, id);
        if (!isCategoria(position)) {
        	uriElemento = ContentUris.withAppendedId(DocumentoCategoriaColumns.CONTENT_URI, id);
        }
        
        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {
            // The caller is waiting for us to return a note selected by
            // the user. The have clicked on one, so return it now.
            setResult(RESULT_OK, new Intent().setData(uriElemento));
        } else {
            // Ejecuta la actividad par ver el listado de la categoria/valores de esta categoria
        	if (isCategoria(position)) {
        		startActivity(new Intent(Intent.ACTION_VIEW, uriElemento).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        	} else {
        		startActivity(new Intent(Intent.ACTION_EDIT, uriElemento));
        	}
        	
        }
    }
    
    
    @Override
	public void overridePendingTransition(int enterAnim, int exitAnim) {
		// TODO Auto-generated method stub
		super.overridePendingTransition(0, 0);
	}

	/**
     * Comprueba si un elmento del cursor es una categoria
     * @param posicion
     * @return
     */
    private boolean isCategoria(int posicion) {
    	mCursor.moveToPosition(posicion);
    	String tipo = mCursor.getString(mCursor.getColumnIndex("tipo"));
    	if ("CATEGORIA".equals(tipo)) {
    		return true;
    	}
    	else {
    		return false;
    	}
    	
    }
    
    
	
}