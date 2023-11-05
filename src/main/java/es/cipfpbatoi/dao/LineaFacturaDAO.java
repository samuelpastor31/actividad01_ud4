package es.cipfpbatoi.dao;

import es.cipfpbatoi.modelo.Articulo;
import es.cipfpbatoi.modelo.LineaFactura;
import es.cipfpbatoi.modelo.Vendedor;

import javax.sound.sampled.Line;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LineaFacturaDAO implements GenericDAO<LineaFactura> {

    private static final String SQLSELECTPK = "SELECT * FROM lineas_factura WHERE linea = ? AND factura = ?";
    private static final String SQLINSERT = "INSERT INTO lineas_factura (linea, factura, articulo, cantidad, importe) VALUES (?, ?, ?, ?, ?)";
    private static final String SQLUPDATE = "UPDATE lineas_factura SET cantidad = ?, importe = ? WHERE linea = ? AND factura = ?";
    private static final String SQLDELETE = "DELETE FROM lineas_factura WHERE linea = ? AND factura = ?";
    private final PreparedStatement pstSelectPK;
    private final PreparedStatement pstInsert;
    private final PreparedStatement pstUpdate;
    private final PreparedStatement pstDelete;
    private FacturaDAO facturaDAO;

    public LineaFacturaDAO() throws SQLException {
        Connection con = ConexionBD.getConexion();
        pstSelectPK = con.prepareStatement(SQLSELECTPK);
        pstInsert = con.prepareStatement(SQLINSERT);
        pstUpdate = con.prepareStatement(SQLUPDATE);
        pstDelete = con.prepareStatement(SQLDELETE);
    }

    public void cerrar() throws SQLException {
        pstSelectPK.close();
        pstInsert.close();
        pstUpdate.close();
        pstDelete.close();
    }

    private LineaFactura build(int linea, int factura, int articulo, int cantidad, float importe) {
        return new LineaFactura(linea, factura, articulo, cantidad, importe);
    }

    public LineaFactura find(int linea, int factura) throws SQLException {
        LineaFactura lineaFactura = null;
        pstSelectPK.setInt(1, linea);
        pstSelectPK.setInt(2, factura);
        ResultSet rs = pstSelectPK.executeQuery();
        if (rs.next()) {
            lineaFactura = build(linea, factura, rs.getInt("articulo"), rs.getInt("cantidad"), rs.getFloat("importe"));
        }
        return lineaFactura;
    }

    public List<LineaFactura> findByFactura(int factura) throws SQLException {
        List<LineaFactura> listaLineas = new ArrayList<>();
        // Construye la consulta SQL para encontrar todas las líneas de una factura específica
        String SQLFindByFactura = "SELECT * FROM lineas_factura WHERE factura = ?";
        try (PreparedStatement pstFindByFactura = pstSelectPK.getConnection().prepareStatement(SQLFindByFactura)) {
            pstFindByFactura.setInt(1, factura);
            ResultSet rs = pstFindByFactura.executeQuery();
            while (rs.next()) {
                listaLineas.add(build(rs.getInt("linea"), factura, rs.getInt("articulo"), rs.getInt("cantidad"), rs.getFloat("importe")));
            }
        }
        return listaLineas;
    }

    @Override
    public LineaFactura find(int id) throws SQLException {
        return null;
    }

    @Override
    public List<LineaFactura> findAll() throws SQLException {
        return null;
    }

    public LineaFactura insert(LineaFactura lineaFacturaInsertar) throws SQLException {
        // Obtén el próximo número de línea disponible para la factura
        int proximaLinea = facturaDAO.getNextLine(lineaFacturaInsertar.getFactura());

        // Asigna el número de línea a lineaFacturaInsertar
        lineaFacturaInsertar.setLinea(proximaLinea);

        // Luego, realiza la inserción en la base de datos
        pstInsert.setInt(1, lineaFacturaInsertar.getFactura());
        pstInsert.setInt(2, lineaFacturaInsertar.getArticulo());
        pstInsert.setInt(3, lineaFacturaInsertar.getCantidad());
        pstInsert.setFloat(4, lineaFacturaInsertar.getImporte());

        int insertados = pstInsert.executeUpdate();

        // Después de la inserción, devuelve el objeto actualizado
        if (insertados == 1) {
            return lineaFacturaInsertar;
        }
        return null;
    }

    public boolean update(LineaFactura lineaFacturaActualizar) throws SQLException {
        pstUpdate.setInt(1, lineaFacturaActualizar.getCantidad());
        pstUpdate.setFloat(2, lineaFacturaActualizar.getImporte());
        pstUpdate.setInt(3, lineaFacturaActualizar.getLinea());
        pstUpdate.setInt(4, lineaFacturaActualizar.getFactura());

        int actualizados = pstUpdate.executeUpdate();
        return (actualizados == 1);
    }

    @Override
    public boolean save(LineaFactura lineaFactura) throws SQLException {
        if (find(lineaFactura.getLinea(), lineaFactura.getFactura()) != null) {
             update(lineaFactura);
             return true;
        } else {
             insert(lineaFactura);
             return false;
        }
    }

    @Override
    public boolean delete(LineaFactura lineaFactura) throws SQLException {
        pstDelete.setInt(1, lineaFactura.getLinea());
        pstDelete.setInt(2, lineaFactura.getFactura());
        int borrados = pstDelete.executeUpdate();
        return (borrados == 1);
    }

    private int getNextLine(int factura) throws SQLException {
        // Implementa la lógica para obtener la próxima línea disponible de la factura
        // Puedes utilizar una consulta SQL para encontrar el valor máximo de la columna 'linea' para la factura dada y luego incrementarlo en 1.
        // Aquí se muestra un ejemplo:
        String SQLMaxLine = "SELECT MAX(linea) FROM lineas_factura WHERE factura = ?";
        try (PreparedStatement pstMaxLine = pstSelectPK.getConnection().prepareStatement(SQLMaxLine)) {
            pstMaxLine.setInt(1, factura);
            ResultSet rs = pstMaxLine.executeQuery();
            if (rs.next()) {
                int maxLine = rs.getInt(1);
                return maxLine + 1;
            } else {
                return 1; // Si no hay líneas existentes para esta factura, inicia en 1.
            }
        }
    }

    @Override
    public long size() throws SQLException {
        // En este caso, size no tiene mucho sentido ya que se manejan las líneas por factura, no globalmente.
        // Devolvemos -1 para indicar que no se puede determinar el tamaño global.
        return -1;
    }

    @Override
    public List<LineaFactura> findByExample(LineaFactura lineaFactura) throws SQLException {
        return null;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        return false;
    }

    public boolean exists(int linea, int factura) throws SQLException {
        LineaFactura lineaFactura = find(linea,factura);
        if (lineaFactura == null) {
            return false;
        }else {
            return true;
        }

    }

    // Otros métodos (findAll, exists, etc.) pueden quedar sin implementar ya que no tienen sentido para esta tabla.
}

