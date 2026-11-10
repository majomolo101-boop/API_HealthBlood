/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
    String user = "root";
    String password = "ingenieria2004";
    String bdName = "healthBlood";
    String url = "jdbc:mysql://127.0.0.1:3306/"+bdName;
    String parametros = "?allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&characterEncoding=utf-8";
    try{
    Class.forName("com.mysql.cj.jdbc.Driver");
    conn = DriverManager.getConnection(url+parametros, user, password);
    return conn;
    }catch(Exception e){
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