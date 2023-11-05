package es.cipfpbatoi.dao;

import es.cipfpbatoi.modelo.Articulo;

import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArticuloDAO implements GenericDAO<Articulo> {

    private static final String SQLSELECTALL = "SELECT * FROM articulos";
    private static final String SQLSELECTPK = "SELECT * FROM articulos WHERE id = ?";
    private static final String SQLINSERT = "INSERT INTO articulos (nombre, precio, codigo, grupo) VALUES (?, ?, ?, ?)";
    private static final String SQLUPDATE = "UPDATE articulos SET nombre = ?, precio = ?, codigo = ?, grupo = ? WHERE id = ?";
    private static final String SQLDELETE = "DELETE FROM articulos WHERE id = ?";
    private static final String SQLCOUNT = "SELECT COUNT(*) AS total FROM articulos";
    private static final String SQLGRUPO = "SELECT * FROM articulos WHERE grupo = ?";
    private final PreparedStatement pstSelectPK;
    private final PreparedStatement pstSelectAll;
    private final PreparedStatement pstInsert;
    private final PreparedStatement pstUpdate;
    private final PreparedStatement pstDelete;
    private final PreparedStatement pstCount;
    private final PreparedStatement pstGroup;

    public ArticuloDAO() throws SQLException {
        Connection con = ConexionBD.getConexion();
        pstSelectPK = con.prepareStatement(SQLSELECTPK);
        pstSelectAll = con.prepareStatement(SQLSELECTALL);
        pstInsert = con.prepareStatement(SQLINSERT, PreparedStatement.RETURN_GENERATED_KEYS);
        pstUpdate = con.prepareStatement(SQLUPDATE);
        pstDelete = con.prepareStatement(SQLDELETE);
        pstCount = con.prepareStatement(SQLCOUNT);
        pstGroup = con.prepareStatement(SQLGRUPO);
    }

    public void cerrar() throws SQLException {
        pstSelectPK.close();
        pstSelectAll.close();
        pstInsert.close();
        pstUpdate.close();
        pstDelete.close();
        pstCount.close();
        pstGroup.close();
    }

    private Articulo build(int id, String nombre, float precio, String codigo, int grupo) {
        return new Articulo(id, nombre, precio, codigo, grupo);
    }

    public Articulo find(int id) throws SQLException {
        Articulo c = null;
        pstSelectPK.setInt(1, id);
        ResultSet rs = pstSelectPK.executeQuery();
        if (rs.next()) {
            c = build(id, rs.getString("nombre"), rs.getFloat("precio"), rs.getString("codigo"), rs.getInt("grupo"));
        }
        return c;
    }

    public List<Articulo> findAll() throws SQLException {
        List<Articulo> listaArticulos = new ArrayList<Articulo>();
        ResultSet rs = pstSelectAll.executeQuery();
        while (rs.next()) {
            listaArticulos.add(build(rs.getInt("id"), rs.getString("nombre"), rs.getFloat("precio"), rs.getString("codigo"), rs.getInt("grupo")));
        }
        return listaArticulos;
    }

    public List<Articulo> findByGrupo(int grupo) throws SQLException {
        List<Articulo> listaArticulos = new ArrayList<>();
        pstGroup.setInt(1, grupo);
        ResultSet rs = pstGroup.executeQuery();
        while (rs.next()) {
            listaArticulos.add(build(rs.getInt("id"), rs.getString("nombre"), rs.getFloat("precio"), rs.getString("codigo"), rs.getInt("grupo")));
        }
        return listaArticulos;
    }

    public Articulo insert(Articulo articuloInsertar) throws SQLException {
        pstInsert.setString(1, articuloInsertar.getNombre());
        pstInsert.setFloat(2, articuloInsertar.getPrecio());
        pstInsert.setString(3, articuloInsertar.getCodigo());
        pstInsert.setInt(4, articuloInsertar.getGrupo());

        int insertados = pstInsert.executeUpdate();
        if (insertados == 1) {
            ResultSet rsClave = pstInsert.getGeneratedKeys();
            rsClave.next();
            int idAsignada = rsClave.getInt(1);
            articuloInsertar.setId(idAsignada);
            return articuloInsertar;
        }
        return null;
    }

    public boolean update(Articulo articuloActualizar) throws SQLException {
        pstUpdate.setString(1, articuloActualizar.getNombre());
        pstUpdate.setFloat(2, articuloActualizar.getPrecio());
        pstUpdate.setString(3, articuloActualizar.getCodigo());
        pstUpdate.setInt(4, articuloActualizar.getGrupo());
        pstUpdate.setInt(5, articuloActualizar.getId());

        int actualizados = pstUpdate.executeUpdate();
        return (actualizados == 1);
    }

    @Override
    public boolean save(Articulo articulo) throws SQLException {
        if (exists(articulo.getId())) {
             update(articulo);
             return true;
        } else {
             insert(articulo);
             return true;
        }
    }

    public boolean delete(int id) throws SQLException {
        pstDelete.setInt(1, id);
        int borrados = pstDelete.executeUpdate();
        return (borrados == 1);
    }

    public boolean delete(Articulo cliEliminar) throws SQLException {
        return this.delete(cliEliminar.getId());
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

    public List<Articulo> findByExample(Articulo exampleArticulo) throws SQLException {
        List<Articulo> matchingArticulos = new ArrayList<>();

        // Agrega la lógica para buscar por ejemplo en este método.

        return matchingArticulos;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        Articulo articulo = find(id);
        if (articulo == null) {
            return false;
        } else {
            return true;
        }
    }
}
