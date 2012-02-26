package es.gluetech.categorias.activity;

import java.util.Iterator;
import java.util.LinkedList;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import es.gluetech.categorias.R;
import es.gluetech.categorias.bean.TipoCampo;
import es.gluetech.categorias.db.table.CamposCategoriaTable.CampoCategoriaColumns;
import es.gluetech.categorias.db.table.TiposCampoCategoriaTable.TipoCampoCategoriaColumns;

/**
 * Permite editar o dar de alta un campo de una categoria
 */
public class CampoEditorActivity extends Activity implements OnItemSelectedListener {
   
	private static final String TAG = "CampoEditorActivity";

    /**
     * Proyeccion de la columnas de un campo de una categoria
     */
    private static final String[] PROJECTION_CAMPO = new String[] {
        CampoCategoriaColumns._ID, // 0
        CampoCategoriaColumns.NOMBRE, // 1
        CampoCategoriaColumns.ID_TIPO, // 2
        CampoCategoriaColumns.ID_CATEGORIA, // 3
    };
    
    /** El indice de la columna nombre */
    private static final int COLUMN_INDEX_NOMBRE = 1;
   
    // Los diferentes estados en los que puede ser ejecutada la actividad
    private static final int STATE_EDIT = 0;
    private static final int STATE_INSERT = 1;

    private int mState;
    private Uri mUri;
    private Cursor mCursor;
    private EditText mNombreCampo;
    private TipoCampo mTipoCampo;
    private String mIdCategoria;
          
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();

        // Configuracion segun la accion ejecutada
        final String action = intent.getAction();
        if (Intent.ACTION_EDIT.equals(action)) {
            // Si se solicita una edicion: se configura el estado edicion y los datos a editar
        	mState = STATE_EDIT;
            mUri = intent.getData();
        } else if (Intent.ACTION_INSERT.equals(action)) {
            // Si se solicita una inserci�n: se configura el estado y se crea una nueva entrada
            mState = STATE_INSERT;
            // Se guarda con valores por defecto
            ContentValues values = new ContentValues();
            mTipoCampo = getListaTiposCampo().getFirst();
            values.put(CampoCategoriaColumns.ID_TIPO, mTipoCampo.getId());
            values.put(CampoCategoriaColumns.ID_CATEGORIA, getIntent().getData().getPathSegments().get(2));
            mUri = getContentResolver().insert(intent.getData(), values);

            // Si no se puede crear una nueva entrada, se finaliza la actividad.
            // Un RESULT_CANCELED es devuelto para informar a la actividad solicitante
            if (mUri == null) {
                Log.e(TAG, "Failed to insert new note into " + getIntent().getData());
                finish();
                return;
            }

            // Si la nueva entrada es creada, se asume que todo va a finalizar bien y
            // se incluye el resultar a devolver
            setResult(RESULT_OK, new Intent(Intent.ACTION_EDIT, mUri));

        } else {
            // Accion desconocida!
            Log.e(TAG, "Unknown action, exiting");
            finish();
            return;
        }

        // Se configura el layout para la actividad 
        setContentView(R.layout.campo_editor);
        
        // El editor para el nombre del campo
        mNombreCampo = (EditText) findViewById(R.id.nombreCampo);

        // Se recupera el campo de la categoria
        mCursor = managedQuery(mUri, PROJECTION_CAMPO, null, null, null);
        mCursor.moveToFirst();
        mIdCategoria = mCursor.getString(mCursor.getColumnIndex(CampoCategoriaColumns.ID_CATEGORIA));
        mNombreCampo.setText(mCursor.getString(mCursor.getColumnIndex(CampoCategoriaColumns.NOMBRE)));
        
        // Se configura la lista desplegable con los valores de tipo de campo
        Spinner spinner = (Spinner) findViewById(R.id.tipoCampo);
        LinkedList<TipoCampo> listaTiposCampo = getListaTiposCampo();
        ArrayAdapter<TipoCampo> adapter = new ArrayAdapter<TipoCampo>(
        		this, android.R.layout.simple_spinner_item, listaTiposCampo);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        int idTipo = mCursor.getInt(mCursor.getColumnIndex(CampoCategoriaColumns.ID_TIPO));
        spinner.setSelection(getPosition(idTipo, listaTiposCampo));
       
    }
    
    /**
     * Devuelve el listado de tipos de campo
     * @return
     */
    private LinkedList<TipoCampo> getListaTiposCampo() {
    	Cursor cursor = null;
    	LinkedList<TipoCampo> listaTiposCampo =  new LinkedList<TipoCampo>();
    	try {
	    	cursor = managedQuery(TipoCampoCategoriaColumns.CONTENT_URI,
	    			new String[] {
	    			TipoCampoCategoriaColumns._ID,
	    			TipoCampoCategoriaColumns.NOMBRE},
	    			null, null, null);
	  	
	    	cursor.moveToFirst();
	    	int id = cursor.getColumnIndex(TipoCampoCategoriaColumns._ID);
	    	int nombre = cursor.getColumnIndex(TipoCampoCategoriaColumns.NOMBRE); 
	        while (cursor.isAfterLast() == false) {
	        	TipoCampo tipo = new TipoCampo(cursor.getInt(id), cursor.getString(nombre));
	        	listaTiposCampo.add(tipo);
	        	cursor.move(1);
	        }
        }
        finally {
        	 if (cursor != null) {
        		 cursor.close();
        	 }
        }
    	return listaTiposCampo;
    }
    
    /**
     * Devuelve la posicion en el la lista de un tipo de campo por su id
     * @param idTipo
     * @param listaTiposCampo
     * @return
     */
    private int getPosition(int idTipo, LinkedList<TipoCampo> listaTiposCampo) {
    	int posicion = 0;
    	for (Iterator<TipoCampo> i = listaTiposCampo.iterator(); i.hasNext(); ) {
    		TipoCampo tipo = (TipoCampo)i.next();
    		if (tipo.getId()==idTipo) break;
    		posicion++;
    	}
    	return posicion;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCursor != null) {
        	// Se refresca la consulta por si ha habido cambios mientras ha estado pausaa
            mCursor.requery();
            // Se asegura que se est� en la primera fila del cursos
            mCursor.moveToFirst();

            // Se modifica el titulo de la actividad segun el estado de ejecucion
            if (mState == STATE_EDIT) {
                // Se configura el titulo de la actividad con el nombre del campo a editar
                String title = mCursor.getString(COLUMN_INDEX_NOMBRE);
                String text = String.format(getResources().getString(R.string.titulo_editar_campo), title);
                setTitle(text);
            } else if (mState == STATE_INSERT) {
                setTitle(getText(R.string.titulo_crear_campo));
            }

            // Se guarda en la actividad el nombre del campo
            String nombre = mCursor.getString(COLUMN_INDEX_NOMBRE);
            mNombreCampo.setTextKeepState(nombre);
            
        } else {
            setTitle(getText(R.string.titulo_error));
            mNombreCampo.setText(getText(R.string.mensaje_error_campo));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
 
        // Se guardan los cambios realizados

        String nombreCampo = mNombreCampo.getText().toString();
        int length = nombreCampo.length();

        // Si la actividad esta finalizando y no hay texto para el nombre
        // del campo entonces se borra la entrada si se esta en modo inserccion
        if (isFinishing() && (length == 0) && mCursor != null && (mState == STATE_INSERT)) {
            setResult(RESULT_CANCELED);
            borrarCampo();
        } else {
            guardarCampo();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Se crea el menu desde el XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.campo_editor_opciones, menu);

        // Append to the
        // menu items for any other activities that can do stuff with it
        // as well.  This does a query on the system for any activities that
        // implement the ALTERNATIVE_ACTION for our data, adding a menu item
        // for each one that is found.
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, CampoEditorActivity.class), null, intent, 0, null);

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
        // Captura las posibles opciones del menu
        switch (item.getItemId()) {
        case R.id.menu_save:
            guardarCampo();
            finish();
            break;
        case R.id.menu_delete:
            borrarCampo();
            finish();
            break;
        case R.id.menu_discard:
            borrarCampo();
            setResult(RESULT_CANCELED);
            finish();
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private final void guardarCampo() {
        if (mCursor != null) {
            ContentValues values = new ContentValues();

            // Establece la fecha de modificaci�n a ahora
            values.put(CampoCategoriaColumns.FECHA_MODIFICACION, System.currentTimeMillis());

            values.put(CampoCategoriaColumns.NOMBRE, mNombreCampo.getText().toString());
            values.put(CampoCategoriaColumns.ID_TIPO, mTipoCampo.getId());
            values.put(CampoCategoriaColumns.ID_CATEGORIA, mIdCategoria);

            // Se guardan los cambios en la base de datos
            try {
                getContentResolver().update(mUri, values, null, null);
            } catch (NullPointerException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private final void borrarCampo() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
            getContentResolver().delete(mUri, null, null);
            mNombreCampo.setText("");
        }
    }

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    	// Si se ha selecionado un valor en la lista de tipos de campos
		if (parent.getId() == R.id.tipoCampo) {
			mTipoCampo = (TipoCampo) parent.getItemAtPosition(pos);
		}
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}
}