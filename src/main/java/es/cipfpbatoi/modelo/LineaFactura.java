package es.cipfpbatoi.modelo;

import java.util.Objects;

public class LineaFactura {
	private int linea;
	private int factura;
	private int articulo;
	private int cantidad;
	private float importe;
	
	public LineaFactura() {		
	}
	
	public LineaFactura(int linea, int factura, int articulo, int cantidad, float importe) {
		super();
		this.linea = linea;
		this.factura = factura;
		this.articulo = articulo;
		this.cantidad = cantidad;
		this.importe = importe;
	}
	
	public LineaFactura(int factura, int articulo, int cantidad) {
		super();
		this.factura = factura;
		this.articulo = articulo;
		this.cantidad = cantidad;
	}
	
	public LineaFactura(int linea, int factura, int articulo, int cantidad) {
		super();
		this.linea = linea;
		this.factura = factura;
		this.articulo = articulo;
		this.cantidad = cantidad;
	}

	public int getLinea() {
		return linea;
	}

	public void setLinea(int linea) {
		this.linea = linea;
	}

	public int getFactura() {
		return factura;
	}

	public void setFactura(int factura) {
		this.factura = factura;
	}

	public int getArticulo() {
		return articulo;
	}

	public void setArticulo(int articulo) {
		this.articulo = articulo;
	}

	public int getCantidad() {
		return cantidad;		
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public float getImporte() {
		return importe;
	}

	public void setImporte(float importe) {
		this.importe = importe;
	}

	@Override
	public String toString() {
		return "LineasFactura [linea=" + linea + ", articulo=" + articulo + ", cantidad=" + cantidad + ", importe="
				+ importe + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(articulo, cantidad, factura, importe, linea);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LineaFactura other = (LineaFactura) obj;
		return articulo == other.articulo && cantidad == other.cantidad && factura == other.factura
				&& Float.floatToIntBits(importe) == Float.floatToIntBits(other.importe) && linea == other.linea;
	}

	
	
	
	
}
