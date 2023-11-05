package es.cipfpbatoi.modelo;

import java.util.Objects;

public class Cliente {
	private int id;
	private String nombre;
	private String direccion;

	public Cliente() {
	}

	public Cliente(String nombre, String direccion) {
		this.nombre = nombre;
		this.direccion = direccion;
	}

	public Cliente(int id, String nombre, String direccion) {
		this.id = id;
		this.nombre = nombre;
		this.direccion = direccion;
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

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	@Override
	public String toString() {
		return "Cliente [id=" + id + ", nombre=" + nombre + ", direccion=" + direccion + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(direccion, id, nombre);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cliente other = (Cliente) obj;
		return Objects.equals(direccion, other.direccion) && id == other.id && Objects.equals(nombre, other.nombre);
	}

	
}
