package es.cipfpbatoi.dao;

import es.cipfpbatoi.modelo.Articulo;
import es.cipfpbatoi.modelo.Grupo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GrupoDAO implements GenericDAO<Grupo> {

    final String SQLSELECTALL = "SELECT * FROM grupos";
    final String SQLSELECTPK = "SELECT * FROM grupos WHERE id = ?";
    final String SQLINSERT = "INSERT INTO grupos (descripcion) VALUES (?)";
    final String SQLUPDATE = "UPDATE grupos SET descripcion = ? WHERE id = ?";
    final String SQLDELETE = "DELETE FROM grupos WHERE id = ?";
    final String SQLCOUNT = "SELECT COUNT(*) AS total FROM grupos";
    private final PreparedStatement pstSelectPK;
    private final PreparedStatement pstSelectAll;
    private final PreparedStatement pstInsert;
    private final PreparedStatement pstUpdate;
    private final PreparedStatement pstDelete;
    private final PreparedStatement pstCount;

    public GrupoDAO() throws SQLException {
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

    private Grupo build(int id, String descripcion) {
        return new Grupo(id, descripcion);
    }

    public Grupo find(int id) throws SQLException {
        Grupo grupo = null;
        pstSelectPK.setInt(1, id);
        ResultSet rs = pstSelectPK.executeQuery();
        if (rs.next()) {
            grupo = build(id, rs.getString("descripcion"));
        }
        return grupo;
    }

    public List<Grupo> findAll() throws SQLException {
        List<Grupo> listaGrupos = new ArrayList<>();
        ResultSet rs = pstSelectAll.executeQuery();
        while (rs.next()) {
            listaGrupos.add(build(rs.getInt("id"), rs.getString("descripcion")));
        }
        return listaGrupos;
    }

    public Grupo insert(Grupo grupoInsertar) throws SQLException {
        pstInsert.setString(1, grupoInsertar.getDescripcion());
        int insertados = pstInsert.executeUpdate();
        if (insertados == 1) {
            ResultSet rsClave = pstInsert.getGeneratedKeys();
            rsClave.next();
            int idAsignada = rsClave.getInt(1);
            grupoInsertar.setId(idAsignada);
            return grupoInsertar;
        }
        return null;
    }

    public boolean update(Grupo grupoActualizar) throws SQLException {
        pstUpdate.setString(1, grupoActualizar.getDescripcion());
        pstUpdate.setInt(2, grupoActualizar.getId());
        int actualizados = pstUpdate.executeUpdate();
        return (actualizados == 1);
    }

    @Override
    public boolean save(Grupo grupo) throws SQLException {
        if (exists(grupo.getId())) {
             update (grupo);
             return true;
        } else {
            insert(grupo);
            return true;
        }
    }



    @Override
    public boolean delete(Grupo grupo) throws SQLException {
        pstDelete.setInt(1, grupo.getId());
        int borrados = pstDelete.executeUpdate();
        return (borrados == 1);
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

    @Override
    public List<Grupo> findByExample(Grupo grupo) throws Exception {
        return null;
    }


    @Override
    public boolean exists(int id) throws SQLException {
        Grupo grupo = find(id);
        if (grupo == null) {
            return false;
        } else {
            return true;
        }
    }
}