package com.thinkopen.restful.api;

import com.thinkopen.restful.api.dtos.Message;
import com.thinkopen.restful.api.dtos.MessageIO;
import com.thinkopen.restful.api.dtos.User;

import javax.ws.rs.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.thinkopen.restful.api.Utils.*;

@Path("/messages")
public class MessageServices {
    private static MySQLAccess dao = MySQLAccess.getInstance();

    public MessageServices() {

    }

    @GET
    public String ping() {
        return "pong";
    }

    @POST
    @Path("/trash/{id}")
    public String trash(@PathParam("id") String msgId) {
        final User loggedUser = UserServices.getLoggedUser();

        if(loggedUser == null)
            return "Attualmente non sei connesso.";

        try {
            final MessageIO msgio = dao.selectMessageIOById(loggedUser.getId(), msgId);

            if(msgio == null)
                return "Messaggio non trovato.";

            if(msgio.isDeleted())
                return "Messaggio già cestinato.";

            int ar = dao.setTrashFlag(msgio, true);

            if(ar < 1)
                return "Cestinamento del messaggio fallito.";

            return "Messaggio cestinato correttamente.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Si è verificato un errore grave durante la procedura di cestinamento del messaggio.";
        }
    }

    @POST
    @Path("/untrash/{id}")
    public String untrash(@PathParam("id") String msgId) {
        final User loggedUser = UserServices.getLoggedUser();

        if(loggedUser == null)
            return "Attualmente non sei connesso.";

        try {
            final MessageIO msgio = dao.selectMessageIOById(loggedUser.getId(), msgId);

            if(msgio == null)
                return "Messaggio non trovato.";

            if(!msgio.isDeleted())
                return "Messaggio ancora non cestinato.";

            int ar = dao.setTrashFlag(msgio, false);

            if(ar < 1)
                return "Recupero del messaggio fallito.";

            return "Messaggio recuperato correttamente.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Si è verificato un errore grave durante la procedura di recupero del messaggio.";
        }
    }

    @POST
    @Path("/read/{id}")
    public String read(@PathParam("id") String msgId) {
        final User loggedUser = UserServices.getLoggedUser();

        if(loggedUser == null)
            return "Attualmente non sei connesso.";

        try {
            final MessageIO msgio = dao.selectMessageIOById(loggedUser.getId(), msgId);
            final Message msg = dao.selectMessageById(msgId);

            if(msgio == null || msg == null)
                return "Messaggio non trovato.";

            if(!msgio.isRead()) {
                int ar = dao.setReadFlag(msgio, true);

                if(ar < 1)
                    return "Impossibile leggere il messaggio.";
            }

            return msg.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Si è verificato un errore grave durante la lettura del messaggio.";
        }
    }

    @POST
    @Path("/unread/{id}")
    public String unread(@PathParam("id") String msgId) {
        final User loggedUser = UserServices.getLoggedUser();

        if(loggedUser == null)
            return "Attualmente non sei connesso.";

        try {
            final MessageIO msgio = dao.selectMessageIOById(loggedUser.getId(), msgId);
            final Message msg = dao.selectMessageById(msgId);

            if(msgio == null || msg == null)
                return "Messaggio non trovato.";

            if(msg.getSenderId() == loggedUser.getId())
                return "Non puoi marcare come 'non letto' un messaggio inviato da te stesso.";

            if(!msgio.isRead())
                return "Messaggio ancora non letto.";

            int ar = dao.setReadFlag(msgio, false);

            if(ar < 1)
                return "Impossibile marcare il messaggio come 'non letto'.";

            return "Messaggio marcato con successo come 'non letto'.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Si è verificato un errore grave durante la marcatura del messaggio come 'non letto'.";
        }
    }

    @GET
    @Path("/sent")
    public String sentMessages(@QueryParam("deleted") Boolean deleted) {
        final User loggedUser = UserServices.getLoggedUser();

        if (loggedUser == null)
            return "Attualmente non sei connesso.";

        try {
            List<Message> msgs = dao.selectAllSentMessages(loggedUser.getId(), deleted, null, null, -1);

            if(msgs.isEmpty())
                return "Non ci sono messaggi inviati.";

            return msgs.toString();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return "Si è verificato un errore grave durante la procedura di recupero dei messaggi inviati.";
        }
    }

    @GET
    @Path("/received")
    public String receivedMessages(@QueryParam("read") Boolean read, @QueryParam("deleted") Boolean deleted) {
        final User loggedUser = UserServices.getLoggedUser();

        if (loggedUser == null)
            return "Attualmente non sei connesso.";

        try {
            List<Message> msgs = dao.selectAllReceivedMessages(loggedUser.getId(), read, deleted,null, null, -1);

            if(msgs.isEmpty())
                return "Non ci sono messaggi ricevuti.";

            return msgs.toString();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return "Si è verificato un errore grave durante la procedura di recupero dei messaggi ricevuti.";
        }
    }

    @POST
    @Path("/send")
    //http://localhost:8080/api/messages/send?msg=MESSAGGIO&rcv=ID1&rcv=ID2&rcv=ID3...
    public String send(@QueryParam("msg") String msgContent, @QueryParam("rcv") List<Integer> receiverIds) {
        final User loggedUser = UserServices.getLoggedUser();

        if(loggedUser == null)
            return "Attualmente non sei connesso.";

        if(!validate(msgContent, MSG_VALIDATOR))
            return "Messaggio non valido.";

        if(receiverIds.size() < 1)
            return "Destinatario non specificato.";

        Message msg = new Message();
        msg.setContent(msgContent);
        msg.setSenderId(loggedUser.getId());

        List<User> receivers = new ArrayList<>(receiverIds.size());

        try {
            for(Integer id : receiverIds) {
                User user = dao.selectById(id);

                if(user != null)
                    receivers.add(user);
            }

            msg = dao.send(msg, receivers);

            if(msg.getId() != null)
                return "Messaggio inviato con successo.";

            return "Invio del messaggio fallito.";
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return "Si è verificato un errore grave durante la procedura di invio del messaggio.";
        }
    }
}
