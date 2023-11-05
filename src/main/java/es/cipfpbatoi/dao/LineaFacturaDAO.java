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

    public boolean update(LineaFactura lineaFacturaActualizar) throws SQLException {
        String SQLUpdate = "UPDATE lineas_factura SET cantidad = ?, importe = ? WHERE linea = ? AND factura = ?";
        try (PreparedStatement pstUpdate = pstSelectPK.getConnection().prepareStatement(SQLUpdate)) {
            pstUpdate.setInt(1, lineaFacturaActualizar.getCantidad());
            pstUpdate.setFloat(2, lineaFacturaActualizar.getImporte());
            pstUpdate.setInt(3, lineaFacturaActualizar.getLinea());
            pstUpdate.setInt(4, lineaFacturaActualizar.getFactura());

            int actualizados = pstUpdate.executeUpdate();
            return actualizados > 0; // Devuelve true si se actualizaron uno o más registros
        }
    }

    public LineaFactura insert(LineaFactura lineaFacturaInsertar) throws SQLException {
        String SQLInsert = "INSERT INTO lineas_factura (linea, factura, articulo, cantidad, importe) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstInsert = pstSelectPK.getConnection().prepareStatement(SQLInsert)) {
            int proximaLinea = facturaDAO.getNextLine(lineaFacturaInsertar.getFactura());
            lineaFacturaInsertar.setLinea(proximaLinea);

            pstInsert.setInt(1, lineaFacturaInsertar.getLinea());
            pstInsert.setInt(2, lineaFacturaInsertar.getFactura());
            pstInsert.setInt(3, lineaFacturaInsertar.getArticulo());
            pstInsert.setInt(4, lineaFacturaInsertar.getCantidad());
            pstInsert.setFloat(5, lineaFacturaInsertar.getImporte());

            int insertados = pstInsert.executeUpdate();
            if (insertados == 1) {
                return lineaFacturaInsertar;
            }
            return null;
        }
    }


    @Override
    public boolean save(LineaFactura lineaFactura) throws SQLException {
        if (exists(lineaFactura.getLinea(), lineaFactura.getFactura())) {
             update(lineaFactura);
             return true;
        } else {
             insert(lineaFactura);
             return true;
        }
    }

    @Override
    public boolean delete(LineaFactura lineaFactura) throws SQLException {
        pstDelete.setInt(1, lineaFactura.getLinea());
        pstDelete.setInt(2, lineaFactura.getFactura());
        int borrados = pstDelete.executeUpdate();
        return (borrados == 1);
    }

    public List<LineaFactura> getByLine(int linea) throws SQLException {
        List<LineaFactura> lineasFactura = new ArrayList<>();
        Connection connection = ConexionBD.getConexion();

        String SQLFindByLine = "SELECT * FROM lineas_factura WHERE linea = ?";
        try (PreparedStatement pstFindByLine = connection.prepareStatement(SQLFindByLine)) {
            pstFindByLine.setInt(1, linea);
            ResultSet rs = pstFindByLine.executeQuery();
            while (rs.next()) {
                lineasFactura.add(build(rs.getInt("linea"), rs.getInt("factura"), rs.getInt("articulo"), rs.getInt("cantidad"), rs.getFloat("importe")));
            }
        }

        connection.close();
        return lineasFactura;
    }

    @Override
    public long size() throws SQLException {

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

}

