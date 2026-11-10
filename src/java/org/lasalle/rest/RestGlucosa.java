package org.lasalle.rest;

import com.google.gson.Gson;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import org.lasalle.controller.MedidaController;
import org.lasalle.controller.UsuarioController;
import org.lasalle.model.Medida;
import org.lasalle.model.Usuario;


/**
 *
 * @author Admin
 */
@Path("glucosa")
public class RestGlucosa {
    
    @Path("saludar")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludar(){
        String saludo = """
                    {"saludo": "Hola desde java"}
                    """;
        return Response.ok(saludo).build();
    }
      
    @Path("saludarCumpleanios")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludarCumpleanios(
        @QueryParam("nombre") String nombre
    ){
        String out = """
                   {"response" : "Feliz cumple %s"}
                   """;
        out = String.format(out, nombre);
        return Response.ok(out).build();
    }
      
    @Path("getAllUsuarios")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsuarios(){
        UsuarioController uc = new UsuarioController();
        List<Usuario> lista = new ArrayList<>();
        Gson gson = new Gson();
        String out = "";
        try{
            lista = uc.getUsuarios();
            out = gson.toJson(lista);
        } catch (Exception e) {
            e.printStackTrace();
            out = """
                    {"result": "Empty table"}
                    """;
        }
        return Response.ok(out).build();        
    }
      
    @Path("getAllMedidas")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMedidas(){
        MedidaController mc = new MedidaController();
        List<Medida> lista = new ArrayList<>();
        Gson gson = new Gson();
        String out = "";
        try{
            lista = mc.getMedidas();
            out = gson.toJson(lista);
        } catch (Exception e) {
            e.printStackTrace();
            out = """
                    {"result": "Error al consultar medidas"}
                    """;
        }
        return Response.ok(out).build();        
    }

    @Path("registrarUsuario")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarUsuario(
            @FormParam("nombre") String nombre,
            @FormParam("primerApellido") String primerApellido,
            @FormParam("segundoApellido") String segundoApellido,
            @FormParam("fechaNacimiento") String fechaNacimiento,
            @FormParam("tipoDiabetes") String tipoDiabetes,
            @FormParam("email") String email
    ) {
        String out = "";
        try {
            if (nombre == null || nombre.trim().isEmpty() || primerApellido == null || primerApellido.trim().isEmpty()) {
                out = "{\"error\": \"Nombre y primer apellido son obligatorios.\"}";
                return Response.status(Response.Status.BAD_REQUEST).entity(out).build();
            }

            Usuario u = new Usuario();
            u.setNombre(nombre);
            u.setPrimerApellido(primerApellido);
            u.setSegundoApellido(segundoApellido);
            u.setFechaNacimiento(fechaNacimiento);
            u.setTipoDiabetes(tipoDiabetes);
            u.setEmail(email);

            UsuarioController uc = new UsuarioController();
            int idGenerado = uc.insertarUsuario(u);

            if (idGenerado > 0) {
                out = String.format("{\"result\": \"ok\", \"idUsuario\": %d}", idGenerado);
                return Response.status(Response.Status.CREATED).entity(out).build();
            } else {
                out = "{\"error\": \"No se pudo registrar el usuario.\"}";
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(out).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            out = String.format("{\"error\": \"%s\"}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(out).build();
        }
    }

    @Path("registrarMedida")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarMedida(
            @FormParam("idUsuario") String idUsuarioStr,
            @FormParam("glucosa") String glucosaStr,
            @FormParam("contexto") String contexto,
            @FormParam("notas") String notas,
            @FormParam("fechaHora") String fechaHora
    ) {
        String out = "";
        try {
            if (idUsuarioStr == null || idUsuarioStr.trim().isEmpty() || glucosaStr == null || glucosaStr.trim().isEmpty()) {
                out = "{\"error\": \"idUsuario y glucosa son obligatorios.\"}";
                return Response.status(Response.Status.BAD_REQUEST).entity(out).build();
            }

            int idUsuario = Integer.parseInt(idUsuarioStr);
            int glucosa = Integer.parseInt(glucosaStr);

            Medida m = new Medida();
            m.setIdUsuario(idUsuario);
            m.setGlucosa(glucosa);
            m.setContexto(contexto);
            m.setNotas(notas);
            m.setFechaHora(fechaHora);

            MedidaController mc = new MedidaController();
            int idLectura = mc.insertarMedida(m);

            if (idLectura > 0) {
                out = String.format("{\"result\": \"ok\", \"idLectura\": %d}", idLectura);
                return Response.status(Response.Status.CREATED).entity(out).build();
            } else {
                out = "{\"error\": \"No se pudo registrar la medida.\"}";
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(out).build();
            }
        } catch (NumberFormatException e) {
            out = "{\"error\": \"Formato numérico inválido en idUsuario o glucosa.\"}";
            return Response.status(Response.Status.BAD_REQUEST).entity(out).build();
        } catch (Exception e) {
            e.printStackTrace();
            out = String.format("{\"error\": \"%s\"}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(out).build();
        }
    }
}