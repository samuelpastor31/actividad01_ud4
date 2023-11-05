package es.cipfpbatoi.dao;

import es.cipfpbatoi.modelo.Articulo;
import es.cipfpbatoi.modelo.Factura;
import es.cipfpbatoi.modelo.LineaFactura;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FacturaDAO implements GenericDAO<Factura> {

    private static final String SQLSELECTALL = "SELECT * FROM facturas";
    private static final String SQLSELECTPK = "SELECT * FROM facturas WHERE id = ?";
    private static final String SQLINSERT = "INSERT INTO facturas (fecha, cliente, vendedor, formapago) VALUES (?, ?, ?, ?)";
    private static final String SQLUPDATE = "UPDATE facturas SET fecha = ?, cliente = ?, vendedor = ?, formapago = ? WHERE id = ?";
    private static final String SQLDELETE = "DELETE FROM facturas WHERE id = ?";
    private static final String SQLCOUNT = "SELECT COUNT(*) AS total FROM facturas";
    private static final String SQLVENDEDOR = "SELECT * FROM facturas WHERE vendedor = ?";
    private static final String SQLIMPORTETOTAL = "SELECT SUM(importe * cantidad) AS total FROM lineas_factura WHERE factura = ?";
    private static final String SQLNEXTLINE = "SELECT IFNULL(MAX(linea) + 1, -1) AS next_line FROM lineas_factura WHERE factura = ?";
    private static final String SQLFINDBYFACTURA = "SELECT * FROM lineas_factura WHERE factura = ?";
    private static final String SQLFINDBYCLIENTE = "SELECT * FROM facturas WHERE cliente = ?";

    private final PreparedStatement pstSelectPK;
    private final PreparedStatement pstSelectAll;
    private final PreparedStatement pstInsert;
    private final PreparedStatement pstUpdate;
    private final PreparedStatement pstDelete;
    private final PreparedStatement pstCount;
    private final PreparedStatement pstVendedor;
    private final PreparedStatement pstImporteTotal;
    private final PreparedStatement pstNextLine;
    private final PreparedStatement pstFindByFactura;
    private final PreparedStatement pstFindByCliente;

    public FacturaDAO() throws SQLException {
        Connection con = ConexionBD.getConexion();
        pstSelectPK = con.prepareStatement(SQLSELECTPK);
        pstSelectAll = con.prepareStatement(SQLSELECTALL);
        pstInsert = con.prepareStatement(SQLINSERT, PreparedStatement.RETURN_GENERATED_KEYS);
        pstUpdate = con.prepareStatement(SQLUPDATE);
        pstDelete = con.prepareStatement(SQLDELETE);
        pstCount = con.prepareStatement(SQLCOUNT);
        pstVendedor = con.prepareStatement(SQLVENDEDOR);
        pstImporteTotal = con.prepareStatement(SQLIMPORTETOTAL);
        pstNextLine = con.prepareStatement(SQLNEXTLINE);
        pstFindByFactura = con.prepareStatement(SQLFINDBYFACTURA);
        pstFindByCliente = con.prepareStatement(SQLFINDBYCLIENTE);
    }

    public void cerrar() throws SQLException {
        pstSelectPK.close();
        pstSelectAll.close();
        pstInsert.close();
        pstUpdate.close();
        pstDelete.close();
        pstCount.close();
        pstVendedor.close();
        pstImporteTotal.close();
        pstNextLine.close();
        pstFindByFactura.close();
        pstFindByCliente.close();
    }

    private Factura build(int id, LocalDate fecha, int cliente, int vendedor, String formaPago) {
        return new Factura(id, fecha, cliente, vendedor, formaPago);
    }

    public Factura find(int id) throws SQLException {
        Factura factura = null;
        pstSelectPK.setInt(1, id);
        ResultSet rs = pstSelectPK.executeQuery();
        if (rs.next()) {
            factura = build(id, rs.getDate("fecha").toLocalDate(), rs.getInt("cliente"), rs.getInt("vendedor"), rs.getString("formapago"));
        }
        return factura;
    }

    public List<Factura> findAll() throws SQLException {
        List<Factura> listaFacturas = new ArrayList<Factura>();
        ResultSet rs = pstSelectAll.executeQuery();
        while (rs.next()) {
            listaFacturas.add(build(rs.getInt("id"), rs.getDate("fecha").toLocalDate(), rs.getInt("cliente"), rs.getInt("vendedor"), rs.getString("formapago")));
        }
        return listaFacturas;
    }

    public Factura insert(Factura facturaInsertar) throws SQLException {
        pstInsert.setDate(1, Date.valueOf(facturaInsertar.getFecha()));
        pstInsert.setInt(2, facturaInsertar.getCliente());
        pstInsert.setInt(3, facturaInsertar.getVendedor());
        pstInsert.setString(4, facturaInsertar.getFormaPago());
        int insertados = pstInsert.executeUpdate();
        if (insertados == 1) {
            ResultSet rsClave = pstInsert.getGeneratedKeys();
            rsClave.next();
            int idAsignada = rsClave.getInt(1);
            facturaInsertar.setId(idAsignada);
            return facturaInsertar;
        }
        return null;
    }

    public boolean update(Factura facturaActualizar) throws SQLException {
        pstUpdate.setDate(1, Date.valueOf(facturaActualizar.getFecha()));
        pstUpdate.setInt(2, facturaActualizar.getCliente());
        pstUpdate.setInt(3, facturaActualizar.getVendedor());
        pstUpdate.setString(4, facturaActualizar.getFormaPago());
        pstUpdate.setInt(5, facturaActualizar.getId());

        int actualizados = pstUpdate.executeUpdate();
        return (actualizados == 1);
    }

    @Override
    public boolean save(Factura factura) throws Exception {
        if (exists(factura.getId())) {
             update(factura);
             return true;
        } else {
             insert(factura);
             return true;
        }
    }

    public boolean delete(Factura facturaEliminar) throws SQLException {
        return delete(facturaEliminar.getId());
    }

    @Override
    public long size() throws Exception {
        try (ResultSet rs = pstCount.executeQuery()) {
            if (rs.next()) {
                return rs.getLong("total");
            }
        }
        return 0;
    }

    public List<Factura> findByExample(Factura exampleFactura) throws SQLException {
        List<Factura> matchingFacturas = new ArrayList<>();


        return matchingFacturas;
    }

    public float getImporteTotal(int facturaId) throws SQLException {
        float total = 0;
        pstImporteTotal.setInt(1, facturaId);
        ResultSet rs = pstImporteTotal.executeQuery();
        if (rs.next()) {
            total = rs.getFloat("total");
        }
        return total;
    }

    public int getNextLine(int facturaId) throws SQLException {
        pstNextLine.setInt(1, facturaId);
        ResultSet rs = pstNextLine.executeQuery();
        if (rs.next()) {
            int maxLinea = rs.getInt(facturaId);
            return maxLinea;
        }
        return 0;
    }
    public List<LineaFactura> findByFactura(int facturaId) throws SQLException {
        List<LineaFactura> lineasFactura = new ArrayList<>();
        pstFindByFactura.setInt(1, facturaId);
        ResultSet rs = pstFindByFactura.executeQuery();
        while (rs.next()) {
            lineasFactura.add(buildLineaFactura(rs));
        }
        return lineasFactura;
    }

    public List<Factura> findByCliente(int cliente) throws SQLException {
        List<Factura> matchingFacturas = new ArrayList<>();
        pstFindByCliente.setInt(1, cliente);
        ResultSet rs = pstFindByCliente.executeQuery();
        while (rs.next()) {
            matchingFacturas.add(build(rs.getInt("id"), rs.getDate("fecha").toLocalDate(), rs.getInt("cliente"), rs.getInt("vendedor"), rs.getString("formapago")));
        }
        return matchingFacturas;
    }

    public boolean delete(int id) throws SQLException {
        pstDelete.setInt(1, id);
        int borrados = pstDelete.executeUpdate();
        return (borrados == 1);
    }

    public List<Factura> findByVendedor(int vendedor) throws SQLException {
        List<Factura> matchingFacturas = new ArrayList<>();
        pstVendedor.setInt(1, vendedor);
        ResultSet rs = pstVendedor.executeQuery();
        while (rs.next()) {
            matchingFacturas.add(build(rs.getInt("id"), rs.getDate("fecha").toLocalDate(), rs.getInt("cliente"), rs.getInt("vendedor"), rs.getString("formapago")));
        }
        return matchingFacturas;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        Factura factura = find(id);
        if (factura == null) {
            return false;
        } else {
            return true;
        }
    }

    private LineaFactura buildLineaFactura(ResultSet rs) throws SQLException {
        int linea = rs.getInt("linea");
        int factura = rs.getInt("factura");
        int articulo = rs.getInt("articulo");
        int cantidad = rs.getInt("cantidad");
        float importe = rs.getFloat("importe");
        return new LineaFactura(linea, factura, articulo, cantidad, importe);
    }
}

