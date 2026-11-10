package org.lasalle.connection;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Admin
 */
public class ConnectionMysql {
    
    Connection conn;
    
    public Connection open(){
        // Lee las variables de entorno de Render (si no existen, usa localhost para pruebas locales)
        String host = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "127.0.0.1";
        String port = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
        String bdName = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "healthBlood";
        String user = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
        String password = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "ingenieria2004";
        
        String url = "jdbc:mysql://" + host + ":" + port + "/" + bdName;
        // Importante: useSSL=true y requireSSL=true son obligatorios para TiDB Cloud
        String parametros = "?allowPublicKeyRetrieval=true&useSSL=true&requireSSL=true&useUnicode=true&characterEncoding=utf-8";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url + parametros, user, password);
            return conn;
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public void close(){
        if(conn != null){
            try{
                conn.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}