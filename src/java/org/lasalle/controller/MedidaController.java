package org.lasalle.controller;

import java.util.ArrayList;
import java.util.List;
import org.lasalle.connection.ConnectionMysql;
import org.lasalle.model.Medida;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Admin
 */
public class MedidaController {
    
    public List<Medida> getMedidas() throws SQLException {
        List<Medida> lista = new ArrayList<>();
        String sql = "SELECT * FROM lectura_glucosa;";
        
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = (Connection) connMysql.open();
        PreparedStatement pstm = conn.prepareStatement(sql);
        ResultSet rs = pstm.executeQuery();
        
        while(rs.next()){
            Medida medida = new Medida();
            medida.setIdMedida(rs.getInt("id_lectura"));
            medida.setIdUsuario(rs.getInt("id_usuario"));
            medida.setGlucosa(rs.getInt("valor_mg_dl"));
            medida.setFechaHora(rs.getString("fecha_registro")); 
            medida.setContexto(rs.getString("tipo_lectura"));     
            medida.setNotas(rs.getString("notas"));
            
            lista.add(medida);
        }
        
        rs.close();
        pstm.close();
        conn.close();
        connMysql.close();
        
        return lista;
    }

    public boolean existeUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE id_usuario = ?;";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = (Connection) connMysql.open();
        PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.setInt(1, idUsuario);
        ResultSet rs = pstm.executeQuery();
        boolean existe = rs.next();
        rs.close();
        pstm.close();
        conn.close();
        connMysql.close();
        return existe;
    }

    public int insertarMedida(Medida medida) throws SQLException {
        if (!existeUsuario(medida.getIdUsuario())) {
            throw new SQLException("El usuario con ID " + medida.getIdUsuario() + " no existe.");
        }

        String sql = "INSERT INTO lectura_glucosa (id_usuario, valor_mg_dl, tipo_lectura, notas, fecha_registro) "
                   + "VALUES (?, ?, ?, ?, COALESCE(?, CURDATE()));";
        
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = (Connection) connMysql.open();
        PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        
        pstm.setInt(1, medida.getIdUsuario());
        pstm.setInt(2, medida.getGlucosa());
        pstm.setString(3, medida.getContexto());
        pstm.setString(4, medida.getNotas());
        
        if (medida.getFechaHora() != null && !medida.getFechaHora().trim().isEmpty()) {
            pstm.setString(5, medida.getFechaHora());
        } else {
            pstm.setNull(5, java.sql.Types.DATE);
        }
        
        pstm.executeUpdate();
        
        int idGenerado = -1;
        try (ResultSet rs = pstm.getGeneratedKeys()) {
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }
        }
        
        pstm.close();
        conn.close();
        connMysql.close();
        
        return idGenerado;
    }
}