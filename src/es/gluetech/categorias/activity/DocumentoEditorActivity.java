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
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import es.gluetech.categorias.R;
import es.gluetech.categorias.bean.Campo;
import es.gluetech.categorias.bean.TipoCampo;
import es.gluetech.categorias.db.dao.CategoriasDao;
import es.gluetech.categorias.db.table.CamposDocumentoCategoriaTable.CampoDocumentoCategoriaColumns;
import es.gluetech.categorias.db.table.DocumentosCategoriaTable.DocumentoCategoriaColumns;

/**
 * Permite editar o dar de alta un documento de una categoria
 *
 */
public class DocumentoEditorActivity extends Activity {
	
    private static final String TAG = "DocumentoEditorActivity";
    
    /**
     * Proyeccion de la columnas de un valor de una categoria
     */
    private static final String[] PROJECTION_VALOR = new String[] {
        DocumentoCategoriaColumns._ID, // 0
        DocumentoCategoriaColumns.NOMBRE, // 1
        DocumentoCategoriaColumns.ID_CATEGORIA, // 2
    };
    
    /**
     * Proyeccion de la columnas de un campo de un documento de una categoria
     */
    private static final String[] PROJECTION_CAMPO_VALOR = new String[] {
        CampoDocumentoCategoriaColumns._ID, // 0
        CampoDocumentoCategoriaColumns.VALOR, // 1
        CampoDocumentoCategoriaColumns.ID_CAMPO_CATEGORIA, // 2
    };
    
    /** El indice de la columna nombre de la categoria */
    private static final int COLUMN_INDEX_NOMBRE = 1;

    // Los diferentes tipos de estados de ejecucion de la actividad
    private static final int STATE_EDIT = 0;
    private static final int STATE_INSERT = 1;

    private int mState;
    private Uri mUri;
    private Cursor mCursor;
    private Cursor mCursorCampos;
    private EditText mNombreDocumento;
    private String mIdDocumento;
    private String mIdCategoria;
    private ArrayList<Campo> mListaCampos;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Se configura el layout de la actividad
        setContentView(R.layout.valor_editor);

        final Intent intent = getIntent();
                
        final String action = intent.getAction();
        if (Intent.ACTION_EDIT.equals(action)) {
        	// EDICION: configurar el estado y los datos a editar
            mState = STATE_EDIT;
            mUri = intent.getData();
            mIdDocumento = getIntent().getData().getPathSegments().get(1);
                       
        } else if (Intent.ACTION_INSERT.equals(action)) {
        	// INSERCCION: configurar el estado y crear una nueva entrada
            mState = STATE_INSERT;

            // Se recupera de la llamada el identificador de categoria padre
            mIdCategoria = getIntent().getData().getPathSegments().get(2);

            ContentValues values = new ContentValues();
            values.put(DocumentoCategoriaColumns.ID_CATEGORIA, mIdCategoria);
           	
           	// Se inserta en la base de datos el valor           	
            mUri = getContentResolver().insert(DocumentoCategoriaColumns.CONTENT_URI, values);
        
            // Si no se puede realizar la inserccion se finaliza la actividad y se devuelve
            // un RESULT_CANCELED
            if (mUri == null) {
                Log.e(TAG, "Failed to insert new note into " + getIntent().getData());
                finish();
                return;
            }

            // Se almacena el identificador de la categoria
            mIdDocumento = mUri.getPathSegments().get(1);

            // Si se ha podido realizar la inserccion del registro configuramos RESULT_OK
            // porque suponems que se va realizar correctamente la operacion
            setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));
         
        } else {
            // Accion desconocida!
            Log.e(TAG, "Unknown action, exiting");
            finish();
            return;
        }
        
        // Se configura el campo para editar el nombre de la categoria
        mNombreDocumento = (EditText) findViewById(R.id.nombreValor);

        // Se recupera el valor
        mCursor = managedQuery(mUri, PROJECTION_VALOR, null, null, null);
        int index_id_categoria = mCursor.getColumnIndex(DocumentoCategoriaColumns.ID_CATEGORIA);
        mCursor.moveToFirst();
        mIdCategoria = mCursor.getString(index_id_categoria);
          
        // Se recuperan los campos del valor
        Uri uriValor = Uri.withAppendedPath(CampoDocumentoCategoriaColumns.CONTENT_URI_CATEGORIA, mIdDocumento);
        mCursorCampos = getContentResolver().query(uriValor, PROJECTION_CAMPO_VALOR, null, null, null);

        // Se obtiene el listado de campos de la categoria y sus categorias antecesoras
        ArrayList<Campo> listaCampos = new CategoriasDao().getCamposByCategoria(this, mIdCategoria);
        
        // Se crean los widget de los campos obtenidos de la categoria
        ViewGroup grupoCampos = (ViewGroup) findViewById(R.id.listaCamposValor);
        int idValorCampo = 1;
        for (Iterator<Campo> i = listaCampos.iterator(); i.hasNext(); ) {
        	Campo campo = (Campo)i.next();
        	TextView tvCampo = new TextView(this);
        	grupoCampos.addView(tvCampo);
        	tvCampo.setText(campo.getNombre());
        	
        	View viewCampo = null;
        	
        	String tipo = campo.getTipoCampo().getNombre();
        	if (TipoCampo.NUMERICO.equals(tipo) || TipoCampo.TEXTO.equals(tipo)) {
        		viewCampo = new EditText(this);
        	} else {
        		viewCampo = new EditText(this);
        	}
        	
        	if (viewCampo != null) {
        		grupoCampos.addView(viewCampo);
        		viewCampo.setId(idValorCampo);
        		campo.setIdView(viewCampo.getId());
        		idValorCampo++;
        		// Se recorren los campos asociados a la categoria para ver si está y si es así se recupera su valor
        		boolean encontrado = false;
        		for(mCursorCampos.moveToFirst();!mCursorCampos.isAfterLast();mCursorCampos.moveToNext()) {
        			int index_nombre_valor = mCursorCampos.getColumnIndex(CampoDocumentoCategoriaColumns.VALOR); 
        			int index_id_campo = mCursorCampos.getColumnIndex(CampoDocumentoCategoriaColumns.ID_CAMPO_CATEGORIA);
        			   
        			String idCampo = mCursorCampos.getString(index_id_campo);
        			String nombreValor = mCursorCampos.getString(index_nombre_valor);
        			
        			if (idCampo.equals(String.valueOf(campo.getId()))) {
        				if (TipoCampo.NUMERICO.equals(tipo) || TipoCampo.TEXTO.equals(tipo)) {
        					((EditText)viewCampo).setText(nombreValor);
        				} else {
        					((EditText)viewCampo).setText(nombreValor);
        				}
        				encontrado = true;
        				break;
        			}
        		}
        		// Si no se encuentra el campo en el valor se crea
        		if (!encontrado) {
        			ContentValues valuesCampo = new ContentValues();
        			valuesCampo.put(CampoDocumentoCategoriaColumns.VALOR, "");
                	valuesCampo.put(CampoDocumentoCategoriaColumns.ID_CAMPO_CATEGORIA, campo.getId());
                	valuesCampo.put(CampoDocumentoCategoriaColumns.ID_DOCUMENTO_CATEGORIA, mIdDocumento);
                	
                	try {
                		Uri uriValorCampo = getContentResolver().insert(CampoDocumentoCategoriaColumns.CONTENT_URI, valuesCampo);
                		campo.setIdCampoDocumento(Integer.valueOf(uriValorCampo.getPathSegments().get(1)));
                	} catch (NullPointerException e) {
                		Log.e(TAG, e.getMessage());
                	}
        		} else {
        			campo.setIdCampoDocumento(mCursorCampos.getInt(mCursorCampos.getColumnIndex(CampoDocumentoCategoriaColumns._ID)));
        		}
        		
        	
        	}
        	
        }
        
        mListaCampos = listaCampos;
        
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
                String nombre = mCursor.getString(COLUMN_INDEX_NOMBRE);
                String texto = String.format(getResources().getString(R.string.titulo_editar_valor), nombre);
                setTitle(texto);
            } else if (mState == STATE_INSERT) {
                setTitle(getText(R.string.titulo_crear_valor));
            }

            // Se guarda en la actividad el nombre de la categoria
            String nombre = mCursor.getString(COLUMN_INDEX_NOMBRE);
            mNombreDocumento.setTextKeepState(nombre);
            
        } else {
            setTitle(getText(R.string.titulo_error));
            mNombreDocumento.setText(getText(R.string.mensaje_error_valor));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        String nombreValor = mNombreDocumento.getText().toString();
        int length = nombreValor.length();

        // Si la actividad esta finalizando y el nombre de la categoria esta vacio, en caso 
        // de estar en modo insercion se borra la categoria de la base de datos
        // En caso contrario se guardan los valores de la categoria
        if (isFinishing() && (length == 0) && mCursor != null && (mState == STATE_INSERT)) {
            setResult(RESULT_CANCELED);
            borrar();
        } else {
            guardar();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Se crea el menu desde el XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.valor_editor_opciones, menu);

        // Append to the
        // menu items for any other activities that can do stuff with it
        // as well.  This does a query on the system for any activities that
        // implement the ALTERNATIVE_ACTION for our data, adding a menu item
        // for each one that is found.
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, DocumentoEditorActivity.class), null, intent, 0, null);

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
        case R.id.menu_save:
            guardar();
            finish();
            break;
        case R.id.menu_delete:
            borrar();
            finish();
            break;
        case R.id.menu_discard:
            borrar();
            setResult(RESULT_CANCELED);
            finish();
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Guarda el documento en la base de datos
     */
    private final void guardar() {
        if (mCursor != null) {
            ContentValues values = new ContentValues();

            values.put(DocumentoCategoriaColumns.NOMBRE, mNombreDocumento.getText().toString());

            // Se almacenan el nombre del valor en la base de datos
            try {
                getContentResolver().update(mUri, values, null, null);
            } catch (NullPointerException e) {
                Log.e(TAG, e.getMessage());
            }
            
            // Se almacena los valores de los campos
            for (Iterator<Campo> i = mListaCampos.iterator(); i.hasNext(); ) {
            	ContentValues valuesCampo = new ContentValues();
            	Campo campo = (Campo) i.next();
            	View viewCampo = findViewById(campo.getIdView());
            	CharSequence valorCampo = null;
            	String tipo = campo.getTipoCampo().getNombre();
            	if (TipoCampo.NUMERICO.equals(tipo) || TipoCampo.TEXTO.equals(tipo)) {
            		valorCampo = ((TextView) viewCampo).getText();
            	} else {
            		valorCampo = ((TextView) viewCampo).getText();
            	}
            	
            	valuesCampo.put(CampoDocumentoCategoriaColumns.VALOR, valorCampo.toString());
            	valuesCampo.put(CampoDocumentoCategoriaColumns.ID_CAMPO_CATEGORIA, campo.getId());
            	valuesCampo.put(CampoDocumentoCategoriaColumns.ID_DOCUMENTO_CATEGORIA, mIdDocumento);
            	
            	Uri uriCampo = Uri.withAppendedPath(CampoDocumentoCategoriaColumns.CONTENT_URI, 
            										String.valueOf(campo.getIdCampoDocumento()));
            	try {
            		getContentResolver().update(uriCampo, valuesCampo, null, null);
            	} catch (NullPointerException e) {
            		Log.e(TAG, e.getMessage());
            	}
            }
                 
        }
    }

    /**
     * Borra el documento de la base de datos
     */
    private final void borrar() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
            getContentResolver().delete(mUri, null, null);
            mNombreDocumento.setText("");
        }
    }
}