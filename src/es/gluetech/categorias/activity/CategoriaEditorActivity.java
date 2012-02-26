package es.gluetech.categorias.activity;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import es.gluetech.categorias.R;
import es.gluetech.categorias.adapter.ExpandableListAdapter;
import es.gluetech.categorias.bean.Campo;
import es.gluetech.categorias.db.dao.CategoriasDao;
import es.gluetech.categorias.db.table.CamposCategoriaTable.CampoCategoriaColumns;
import es.gluetech.categorias.db.table.CategoriasTable.CategoriaColumns;

/**
 * Permite editar o dar de alta una categoria
 */
public class CategoriaEditorActivity extends Activity {
    private static final String TAG = "CategoriaEditorActivity";
    
    /**
     * Proyeccion de la columnas de un campo de una categoria
     */
    private static final String[] PROJECTION_CATEGORIA = new String[] {
        CategoriaColumns._ID, // 0
        CategoriaColumns.NOMBRE, // 1
        CategoriaColumns.ID_CATEGORIA_PADRE, // 2
    };
    
    /** El indice de la columna nombre de la categoria */
    private static final int COLUMN_INDEX_CATEGORIA_NAME = 1;

    // Los diferentes tipos de estados de ejecucion de la actividad
    private static final int STATE_EDIT = 0;
    private static final int STATE_INSERT = 1;

    private int mState;
    private Uri mUri;
    private Cursor mCursor;
    private EditText mNombreCategoria;
    private String mIdCategoria;
    
    @SuppressWarnings("unused")
	private Cursor mCursorCamposCategoria;
    private ExpandableListAdapter mAdapterCamposCategoria;
    private ExpandableListView mExpandibleListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        final String action = intent.getAction();

        if (Intent.ACTION_EDIT.equals(action)) {
        	// EDICION: configurar el estado y los datos a editar
            mState = STATE_EDIT;
            mUri = intent.getData();
        } else if (Intent.ACTION_INSERT.equals(action)) {
        	// INSERCCION: configurar el estado y crear una nueva entrada
            mState = STATE_INSERT;
                        
            // Se recupera de la llamada el identificador de categoria padre
            ContentValues values = new ContentValues();
           	values.put(CategoriaColumns.ID_CATEGORIA_PADRE, getIntent().getData().getPathSegments().get(2));
           	
           	// Se inserta en la base de datos la categoria           	
            mUri = getContentResolver().insert(CategoriaColumns.CONTENT_URI, values);

            // Si no se puede realizar la inserccion se finaliza la actividad y se devuelve
            // un RESULT_CANCELED
            if (mUri == null) {
                Log.e(TAG, "Failed to insert new note into " + getIntent().getData());
                finish();
                return;
            }

            // Si se ha podido realizar la inserccion del registro configuramos RESULT_OK
            // porque suponems que se va realizar correctamente la operacion
            setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));

        } else {
            // Accion desconocida!
            Log.e(TAG, "Unknown action, exiting");
            finish();
            return;
        }
        
        // Se almacena el identificador de la categoria
        mIdCategoria = mUri.getPathSegments().get(1);

        // Se configura el layout de la actividad
        setContentView(R.layout.categoria_editor);
        
        // Se configura el campo para editar el nombre de la categoria
        mNombreCategoria = (EditText) findViewById(R.id.nombreCategoria);

        // Se recupera la categoria
        mCursor = managedQuery(mUri, PROJECTION_CATEGORIA, null, null, null);
        
        // Se recuperan los campos de la categoria
        Uri uriCamposCategoria = Uri.withAppendedPath(CampoCategoriaColumns.CONTENT_URI_CATEGORIA, mIdCategoria);
        mCursorCamposCategoria = managedQuery(uriCamposCategoria, new String[] {
        								CampoCategoriaColumns._ID, // 0
        								CampoCategoriaColumns.NOMBRE, // 1
        								CampoCategoriaColumns.ID_TIPO, // 2
        								CampoCategoriaColumns.NOMBRE_TIPO // 3
        								}, null, null, null);
        
        // Se recupera el componente ExpandableListView del layout 
        mExpandibleListView = (ExpandableListView) findViewById(R.id.listaCampos);
        
        // TODO: llevar a la pantalla de edición del campo
        mExpandibleListView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				Uri uriCampo = Uri.withAppendedPath(CampoCategoriaColumns.CONTENT_URI, String.valueOf(id));
                startActivity(new Intent(Intent.ACTION_EDIT, uriCampo));

                return true;
			}
        });
        
        // TODO: ver para que puede servir
        mExpandibleListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView arg0, View arg1,
					int arg2, long arg3) {
                //Toast.makeText(getBaseContext(), "Group clicked", Toast.LENGTH_LONG).show();
                return false;
			}
        });
        

    }
    
        
    @Override
    protected void onResume() {
        super.onResume();
        if (mCursor != null) {
        	// Se recupera de nuevo la categoria por si ha habido cambios mientras esta en pausa la actividad
        	mCursor.requery();
            // Se asegura que se esta en el primer registro del cursor
            mCursor.moveToFirst();

            // Se modifica el titulo segun el estado de ejecucion de la actividad
            if (mState == STATE_EDIT) {
                // Se configura el titulo
                String nombre = mCursor.getString(COLUMN_INDEX_CATEGORIA_NAME);
                String texto = String.format(getResources().getString(R.string.titulo_editar_categoria), nombre);
                setTitle(texto);
            } else if (mState == STATE_INSERT) {
                setTitle(getText(R.string.titulo_crear_categoria));
            }

            // Se guarda en la actividad el nombre de la categoria
            String nombre = mCursor.getString(COLUMN_INDEX_CATEGORIA_NAME);
            mNombreCategoria.setTextKeepState(nombre);
            
            // Se inicializa el adaptador con grupos y campos
            mAdapterCamposCategoria = new ExpandableListAdapter(this, new ArrayList<String>(),
            													new ArrayList<ArrayList<Campo>>());
            ArrayList<Campo> listaCampos = new CategoriasDao().getCamposByCategoria(this, mIdCategoria);
            for (Iterator<Campo> i = listaCampos.iterator(); i.hasNext(); ) {
            	mAdapterCamposCategoria.addItem(i.next());
            }
            mExpandibleListView.setAdapter(mAdapterCamposCategoria);
            if (listaCampos.size() > 0) mExpandibleListView.expandGroup(0);
             
            
        } else {
            setTitle(getText(R.string.titulo_error));
            mNombreCategoria.setText(getText(R.string.mensaje_error_categoria));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        String nombreCategoria = mNombreCategoria.getText().toString();
        int length = nombreCategoria.length();

        // Si la actividad esta finalizando y el nombre de la categoria esta vacio, en caso 
        // de estar en modo insercion se borra la categoria de la base de datos
        // En caso contrario se guardan los valores de la categoria
        if (isFinishing() && (length == 0) && mCursor != null && (mState == STATE_INSERT)) {
            setResult(RESULT_CANCELED);
            borrarCategoria();
        } else {
            guardarCategoria();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Se crea el menu desde el XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.categoria_editor_opciones, menu);

        // Append to the
        // menu items for any other activities that can do stuff with it
        // as well.  This does a query on the system for any activities that
        // implement the ALTERNATIVE_ACTION for our data, adding a menu item
        // for each one that is found.
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, CategoriaEditorActivity.class), null, intent, 0, null);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mState == STATE_EDIT) {
            menu.setGroupVisible(R.id.menu_group_edit, true);
            menu.setGroupVisible(R.id.menu_group_insert, false);
        } else {
            menu.setGroupVisible(R.id.menu_group_edit, false);
            menu.setGroupVisible(R.id.menu_group_insert, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Se captura todos las posibles accions del menu
        switch (item.getItemId()) {
        case R.id.menu_add_field:
        	String idCategoria = mUri.getPathSegments().get(1);
        	Uri camposCategoria = Uri.withAppendedPath(CampoCategoriaColumns.CONTENT_URI_CATEGORIA, idCategoria);
            startActivity(new Intent(Intent.ACTION_INSERT, camposCategoria));
        case R.id.menu_save:
            guardarCategoria();
            finish();
            break;
        case R.id.menu_delete:
            borrarCategoria();
            finish();
            break;
        case R.id.menu_discard:
            borrarCategoria();
            setResult(RESULT_CANCELED);
            finish();
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private final void guardarCategoria() {
        if (mCursor != null) {
            ContentValues values = new ContentValues();

            // Se configura el valor de la fecha de modificacion a ahora
            values.put(CategoriaColumns.FECHA_MODIFICACION, System.currentTimeMillis());

            values.put(CategoriaColumns.NOMBRE, mNombreCategoria.getText().toString());

            // Se almacenan los valors en la base de datos
            try {
                getContentResolver().update(mUri, values, null, null);
            } catch (NullPointerException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private final void borrarCategoria() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
            getContentResolver().delete(mUri, null, null);
            mNombreCategoria.setText("");
        }
    }
}