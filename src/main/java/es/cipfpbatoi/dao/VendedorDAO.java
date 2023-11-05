package es.cipfpbatoi.dao;

import es.cipfpbatoi.modelo.Cliente;
import es.cipfpbatoi.modelo.Vendedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VendedorDAO implements GenericDAO<Vendedor> {
    final String SQLSELECTALL = "SELECT * FROM vendedores";
    final String SQLSELECTPK = "SELECT * FROM vendedores WHERE id = ?";
    final String SQLINSERT = "INSERT INTO vendedores (nombre, fecha_ingreso, salario) VALUES (?, ?, ?)";
    final String SQLUPDATE = "UPDATE vendedores SET nombre = ?, fecha_ingreso = ?, salario = ? WHERE id = ?";
    final String SQLDELETE = "DELETE FROM vendedores WHERE id = ?";
    final String SQLCOUNT = "SELECT COUNT(*) AS total FROM vendedores";
    private final PreparedStatement pstSelectPK;
    private final PreparedStatement pstSelectAll;
    private final PreparedStatement pstInsert;
    private final PreparedStatement pstUpdate;
    private final PreparedStatement pstDelete;
    private final PreparedStatement pstCount;

    public VendedorDAO() throws SQLException {
        Connection con = ConexionBD.getConexion();
        pstSelectPK = con.prepareStatement(SQLSELECTPK);
        pstSelectAll = con.prepareStatement(SQLSELECTALL);
        pstInsert = con.prepareStatement(SQLINSERT, PreparedStatement.RETURN_GENERATED_KEYS);
        pstUpdate = con.prepareStatement(SQLUPDATE);
        pstDelete = con.prepareStatement(SQLDELETE);
        pstCount = con.prepareStatement(SQLCOUNT);
    }

    public void cerrar() throws SQLException {
        pstSelectPK.close();
        pstSelectAll.close();
        pstInsert.close();
        pstUpdate.close();
        pstDelete.close();
        pstCount.close();
    }

    private Vendedor build(int id, String nombre, LocalDate fechaIngreso, float salario) {
        return new Vendedor(id, nombre, fechaIngreso, salario);
    }

    public Vendedor find(int id) throws SQLException {
        Vendedor v = null;
        pstSelectPK.setInt(1, id);
        ResultSet rs = pstSelectPK.executeQuery();
        if (rs.next()) {
            v = build(id, rs.getString("nombre"), rs.getDate("fecha_ingreso").toLocalDate(), rs.getFloat("salario"));
        }
        return v;
    }

    public List<Vendedor> findAll() throws SQLException {
        List<Vendedor> listaVendedores = new ArrayList<Vendedor>();
        ResultSet rs = pstSelectAll.executeQuery();
        while (rs.next()) {
            listaVendedores.add(build(rs.getInt("id"), rs.getString("nombre"), rs.getDate("fecha_ingreso").toLocalDate(), rs.getFloat("salario")));
        }
        return listaVendedores;
    }

    public Vendedor insert(Vendedor vendedorInsertar) throws SQLException {
        pstInsert.setString(1, vendedorInsertar.getNombre());
        pstInsert.setDate(2, java.sql.Date.valueOf(vendedorInsertar.getFechaIngreso()));
        pstInsert.setFloat(3, vendedorInsertar.getSalario());

        int insertados = pstInsert.executeUpdate();
        if (insertados == 1) {
            ResultSet rsClave = pstInsert.getGeneratedKeys();
            rsClave.next();
            int idAsignada = rsClave.getInt(1);
            vendedorInsertar.setId(idAsignada);
            return vendedorInsertar;
        }
        return null;
    }

    public boolean update(Vendedor vendedorActualizar) throws SQLException {
        pstUpdate.setString(1, vendedorActualizar.getNombre());
        pstUpdate.setDate(2, java.sql.Date.valueOf(vendedorActualizar.getFechaIngreso()));
        pstUpdate.setFloat(3, vendedorActualizar.getSalario());
        pstUpdate.setInt(4, vendedorActualizar.getId());

        int actualizados = pstUpdate.executeUpdate();
        return (actualizados == 1);
    }

    @Override
    public boolean save(Vendedor vendedor) throws SQLException {
        if (exists(vendedor.getId())) {
            update(vendedor);
            return true;
        } else {
            insert(vendedor);
            return true;
        }
    }

    public boolean delete(int id) throws SQLException {
        pstDelete.setInt(1, id);
        int borrados = pstDelete.executeUpdate();
        return (borrados == 1);
    }

    public boolean delete(Vendedor vendedorEliminar) throws SQLException {
        return this.delete(vendedorEliminar.getId());
    }

    @Override
    public long size() throws SQLException {
        try (ResultSet rs = pstCount.executeQuery()) {
            if (rs.next()) {
                return rs.getLong("total");
            }
        }
        return 0;
    }

    public List<Vendedor> findByExample(Vendedor exampleVendedor) throws SQLException {
        List<Vendedor> matchingVendedores = new ArrayList<>();

        return matchingVendedores;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        Vendedor vendedor = find(id);
        return vendedor != null;
    }
}