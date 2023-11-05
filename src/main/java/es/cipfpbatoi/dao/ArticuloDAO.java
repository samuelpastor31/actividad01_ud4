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

    @Override
    public List<Articulo> findByExample(Articulo articulo) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM articulos WHERE 1=1");
        List<Object> propiedades = new ArrayList<>();

        if (articulo.getId() != 0) {
            sql.append(" AND id = ?");
            propiedades.add(articulo.getId());
        }
        if (articulo.getNombre() != null && !articulo.getNombre().isEmpty()) {
            sql.append(" AND nombre LIKE ?");
            propiedades.add("%" + articulo.getNombre() + "%");
        }
        if (articulo.getPrecio() != 0.0) {
            sql.append(" AND precio <= ?");
            propiedades.add(articulo.getPrecio());
        }
        if (articulo.getCodigo() != null && !articulo.getCodigo().isEmpty()) {
            sql.append(" AND codigo LIKE ?");
            propiedades.add("%" + articulo.getCodigo() + "%");
        }
        if (articulo.getGrupo() != 0) {
            sql.append(" AND grupo = ?");
            propiedades.add(articulo.getGrupo());
        }

        Connection con = ConexionBD.getConexion();
        PreparedStatement pst = con.prepareStatement(sql.toString());
        int parameterIndex = 1;
        for (Object propiedad : propiedades) {
            pst.setObject(parameterIndex, propiedad);
            parameterIndex++;
        }

        ResultSet rs = pst.executeQuery();
        List<Articulo> articulos = new ArrayList<>();
        while (rs.next()) {
            articulos.add(new Articulo(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getFloat("precio"),
                    rs.getString("codigo"),
                    rs.getInt("grupo")
            ));
        }
        rs.close();
        return articulos;
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
