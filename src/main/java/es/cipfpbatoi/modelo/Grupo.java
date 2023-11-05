package es.cipfpbatoi.modelo;

import java.util.Objects;

public class Grupo {
	private int id;
	private String descripcion;

	public Grupo() {
	}

	public Grupo(String descrip) {
		this.descripcion = descrip;
	}

	public Grupo(int id, String descrip) {
		this.id = id;
		this.descripcion = descrip;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descrip) {
		this.descripcion = descrip;
	}

	@Override
	public String toString() {
		return "Grupo [id=" + id + ", descripcion=" + descripcion  + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(descripcion, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Grupo other = (Grupo) obj;
		return Objects.equals(descripcion, other.descripcion) && id == other.id;
	}

	
}
