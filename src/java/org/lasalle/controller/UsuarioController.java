package org.lasalle.controller;

import java.util.ArrayList;
import java.util.List;
import org.lasalle.connection.ConnectionMysql;
import org.lasalle.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Admin
 */
public class UsuarioController {
    
    public List<Usuario> getUsuarios() throws SQLException{
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM users;";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = (Connection) connMysql.open();
        PreparedStatement pstm = conn.prepareStatement(sql);
        ResultSet rs = pstm.executeQuery();
        
        while(rs.next()){
            Usuario user = new Usuario();
            user.setIdUsuario(rs.getInt("id_usuario"));
            user.setNombre(rs.getString("nombre"));
            user.setPrimerApellido(rs.getString("primer_apellido"));
            user.setSegundoApellido(rs.getString("segundo_apellido"));
            user.setFechaNacimiento(rs.getString("fecha_nacimiento")); 
            user.setTipoDiabetes(rs.getString("tipo_diabetes"));      
            user.setFechaRegistro(rs.getString("fecha_registro"));    
            user.setEmail(rs.getString("email"));                     
            
            lista.add(user);
        }
        rs.close();
        pstm.close();
        conn.close();
        connMysql.close();
        return lista;
    }
    
    public int insertarUsuario(Usuario user) throws SQLException {
        String sql = "INSERT INTO users (nombre, primer_apellido, segundo_apellido, fecha_nacimiento, tipo_diabetes, fecha_registro, email) "
                   + "VALUES (?, ?, ?, ?, ?, CURDATE(), ?);";
        
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = (Connection) connMysql.open();
        PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        
        pstm.setString(1, user.getNombre());
        pstm.setString(2, user.getPrimerApellido());
        pstm.setString(3, user.getSegundoApellido());
        
        if (user.getFechaNacimiento() != null && !user.getFechaNacimiento().trim().isEmpty()) {
            pstm.setString(4, user.getFechaNacimiento());
        } else {
            pstm.setNull(4, java.sql.Types.DATE);
        }
        
        pstm.setString(5, user.getTipoDiabetes());
        pstm.setString(6, user.getEmail());
        
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