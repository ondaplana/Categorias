package es.gluetech.categorias.bean;


/**
 * Campo de una categoria
 */
public class Campo {
	
	private int id;
	private String nombre;
	private TipoCampo tipoCampo;
	private String grupo;
	private int idView;
	int idCampoDocumento;
	
	public Campo(String grupo, int id, String nombre, TipoCampo tipoCampo) {
		this.grupo = grupo;
		this.id = id;
		this.nombre = nombre;
		this.tipoCampo = tipoCampo;
	}
	
	public String getGrupo() {
		return grupo;
	}

	public String getNombre() {
		return nombre;
	}

	public TipoCampo getTipoCampo() {
		return tipoCampo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdView() {
		return idView;
	}

	public void setIdView(int idView) {
		this.idView = idView;
	}

	public int getIdCampoDocumento() {
		return idCampoDocumento;
	}

	public void setIdCampoDocumento(int id) {
		this.idCampoDocumento = id;
	}

}
