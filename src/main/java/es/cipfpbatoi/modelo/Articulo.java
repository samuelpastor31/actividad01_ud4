package es.cipfpbatoi.modelo;

import java.util.Objects;

public class Articulo {
	private int id;
	private String nombre;
	private float precio;
	private String codigo;
	private int grupo;
	
	public Articulo() {		
	}

	public Articulo(String nombre, float precio, String codigo, int grupo) {
		super();
		this.nombre = nombre;
		this.precio = precio;
		this.codigo = codigo;
		this.grupo = grupo;
	}
	
	public Articulo(int id, String nombre, float precio, String codigo, int grupo) {
		this.id = id;
		this.nombre = nombre;
		this.precio = precio;
		this.codigo = codigo;
		this.grupo = grupo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public int getGrupo() {
		return grupo;
	}

	public void setGrupo(int grupo) {
		this.grupo = grupo;
	}

	@Override
	public String toString() {
		return "Articulo [id=" + id + ", nombre=" + nombre + ", precio=" + precio + ", codigo=" + codigo + ", grupo="
				+ grupo + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(codigo, grupo, id, nombre, precio);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Articulo other = (Articulo) obj;
		return Objects.equals(codigo, other.codigo) && grupo == other.grupo && id == other.id
				&& Objects.equals(nombre, other.nombre)
				&& Float.floatToIntBits(precio) == Float.floatToIntBits(other.precio);
	}

	
	
	
	
}
