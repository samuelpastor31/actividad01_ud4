package es.cipfpbatoi.modelo;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Factura {
	private int id;
	private LocalDate fecha;
	private int cliente;
	private int vendedor;
	private String formaPago;
	// private List<LineaFactura> lineas;

	public Factura() {

	}

	public Factura(LocalDate fecha, int cliente, int vendedor, String formaPago) {
		this.fecha = fecha;
		this.cliente = cliente;
		this.vendedor = vendedor;
		this.formaPago = formaPago;
	}

	public Factura(int id, LocalDate fecha, int cliente, int vendedor, String formaPago) {
		this.id = id;
		this.fecha = fecha;
		this.cliente = cliente;
		this.vendedor = vendedor;
		this.formaPago = formaPago;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public int getCliente() {
		return cliente;
	}

	public void setCliente(int cliente) {
		this.cliente = cliente;
	}

	public int getVendedor() {
		return vendedor;
	}

	public void setVendedor(int vendedor) {
		this.vendedor = vendedor;
	}

	public String getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}

	@Override
	public String toString() {
		return "Factura [id=" + id + ", fecha=" + fecha + ", cliente=" + cliente + ", vendedor=" + vendedor
				+ ", formaPago=" + formaPago + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(cliente, fecha, formaPago, id, vendedor);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Factura other = (Factura) obj;
		return cliente == other.cliente && Objects.equals(fecha, other.fecha)
				&& Objects.equals(formaPago, other.formaPago) && id == other.id && vendedor == other.vendedor;
	}

	
}
