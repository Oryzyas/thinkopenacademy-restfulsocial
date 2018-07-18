package com.thinkopen.restful.api;

import com.sun.org.apache.regexp.internal.RE;
import com.thinkopen.restful.api.dtos.User;
import com.thinkopen.restful.api.factories.responses.Response;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.sql.SQLException;

import static com.thinkopen.restful.api.Utils.*;
import static com.thinkopen.restful.api.factories.ResponseFactory.ResponseType;

@Path("/users")
public final class UserServices {
    private static User loggedUser = null;
    private static ResponseType responseType = ResponseType.JSON;
    private static MySQLAccess dao = MySQLAccess.getInstance();

    public UserServices() {

    }

    public static User getLoggedUser() {
        return loggedUser;
    }

    public static ResponseType getResponseType() { return responseType; }

    @GET
    public String ping() {
        return "pong";
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response upload(String base64img) {
        if(loggedUser == null)
            return errorResponse("Attualmente non sei connesso.");

        try {
            int affectedRows = dao.saveFile(base64img, loggedUser);

            if(affectedRows > 0)
                return successResponse("Upload eseguito con successo.");

            return errorResponse("Upload fallito.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return errorResponse("Si è verificato un errore grave durante l'upload.");
        }
    }

    /*@GET
    @Path("/{userId}")
    public String selectById(@PathParam("userId") int userId) {
        if(loggedUser == null)
            return "Attualmente non sei connesso.";

        try {
            User user = dao.selectById(userId);

            if(user == null)
                return "Utente non trovato.";

            return user.toString();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return "Si è verificato un errore grave durante la ricerca utente.";
        }
    }*/

    @POST
    @Path("/login")
    public Response login(@QueryParam("username") String username, @QueryParam("password") String password, @QueryParam("responseType") String responseType) {
        if(loggedUser != null)
            return errorResponse("Sei già connesso.");

        try {
            User user = dao.login(username, password);

            if(user == null)
                return errorResponse("Dati di login e/o password errati.\n\nNuovo utente? Registrati andando su \"/users/signup\".");

            loggedUser = user;

            ResponseType rt = ResponseType.parse(responseType);

            if(rt != null)
                UserServices.responseType = rt;

            return successResponse("Login effettuato con successo.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return errorResponse("Si è verificato un errore grave durante la procedura di login.");
        }
    }

    @POST
    @Path("/logout")
    public Response logout() {
        if(loggedUser == null)
            return errorResponse("Sei già disconnesso.");

        loggedUser = null;
        responseType = ResponseType.JSON;

        return successResponse("Logout effettuato con successo.");
    }

    @POST
    @Path("/signup")
    public Response signup(@QueryParam("name") String name, @QueryParam("age") int age, @QueryParam("email") String email, @QueryParam("password") String password) {
        if(loggedUser != null)
            return errorResponse("Sei attualmente connesso. Non puoi creare nuovi account.");

        if(!validate(name, NAME_VALIDATOR))
            return errorResponse("Nome non valido.");

        if(!validate(age, AGE_VALIDATOR))
            return errorResponse("Età non valida");

        if(!validate(email, EMAIL_VALIDATOR))
            return errorResponse("Email non valida");

        if(!validate(password, PASSWORD_VALIDATOR))
            return errorResponse("Password non valida");

        User user = new User();
        user.setName(name);
        user.setAge(age);
        user.setEmail(email);

        try {
            int userId = dao.insert(user, password);

            if(userId == -1)
                return errorResponse("Creazione account fallita.");

            user.setId(userId);

            return successResponse("Creazione account eseguita con successo.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return errorResponse("Si è verificato un errore grave durante la procedura di creazione dell'account.");
        }
    }

    @POST
    @Path("/update/{password}")
    public Response update(@PathParam("password") String password) {
        if(loggedUser == null)
            return errorResponse("Attualmente non sei connesso.");

        if(!validate(password, PASSWORD_VALIDATOR))
            return errorResponse("Password non valida");

        try {
            int affectedRows = dao.update(loggedUser.getId(), password);

            if(affectedRows < 1)
                return errorResponse("Aggiornamento password fallito.)");

            return successResponse("Aggiornamento password avvenuto con successo.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return errorResponse("Si è verificato un errore grave durante la procedura di aggiornamento della password.");
        }
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(User user) {
        if(loggedUser == null)
            return errorResponse("Attualmente non sei connesso.");

        // Riempie i campi lasciati vuoti

        if(user.getId() == -1)
            user.setId(loggedUser.getId());

        if(user.getName() == null)
            user.setName(loggedUser.getName());

        if(user.getAge() == -1)
            user.setAge(loggedUser.getAge());

        if(user.getEmail() == null)
            user.setEmail(loggedUser.getEmail());

        // Valida i dati

        if(!validate(user.getName(), NAME_VALIDATOR))
            return errorResponse("Nome non valido.");

        if(!validate(user.getAge(), AGE_VALIDATOR))
            return errorResponse("Età non valida");

        if(!validate(user.getEmail(), EMAIL_VALIDATOR))
            return errorResponse("Email non valida");

        // Effettua l'aggiornamento

        try {
            int affectedRows = dao.update(user);

            if(affectedRows < 1)
                return errorResponse("Aggiornamento account fallito.");

            return successResponse("Aggiornamento account avvenuto con successo.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return errorResponse("Si è verificato un errore grave durante la procedura di aggiornamento dell'account.");
        }
    }

}
