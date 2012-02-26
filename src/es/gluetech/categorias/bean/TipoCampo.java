package es.gluetech.categorias.bean;

/**
 * Tipo de campo de una categoria
 */
public class TipoCampo {

	int id;
	String nombre;
	
	public static final String NUMERICO = "Numérico";
	public static final String TEXTO = "Texto";
	
	//Constructor
	public TipoCampo(int id, String nombre) {
		this.id = id;
		this.nombre = nombre;
	}
	
	@Override
	public String toString() {
		return nombre;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public int getId() {
		return id;
	}

	public int getIdDocumento(Campo campo) {
		return campo.idCampoDocumento;
	}
	
}
