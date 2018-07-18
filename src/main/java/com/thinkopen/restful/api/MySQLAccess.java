package com.thinkopen.restful.api;

import com.sun.istack.internal.Nullable;
import com.thinkopen.restful.api.dtos.*;
import org.mariadb.jdbc.MySQLDataSource;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class MySQLAccess {
    private static final MySQLAccess instance = new MySQLAccess();

    private Connection con = null;
    //private PreparedStatement ps = null;

    private MySQLAccess() {

    }

    public static MySQLAccess getInstance() {
        return instance;
    }

    private Connection getConnection() throws SQLException {
        /* Metodo 1

        // Caricare il driver
        Class.forName("com.mysql.jdbc.Driver");

        // Setup della Connection
        con = DriverManager.getConnection("jdbc:mysql://localhost/restful?user=root&password=root");
        return con;

        */

        if(con == null) {
            // Metodo 2

            MySQLDataSource dataSource = new MySQLDataSource();
            dataSource.setServerName("localhost");
            dataSource.setPortNumber(3306);
            dataSource.setUser("root");
            dataSource.setPassword("root");
            dataSource.setDatabaseName("jdbctest");
            con = dataSource.getConnection();
        }

        return con;
    }

    public User selectById(int id) throws SQLException, ClassNotFoundException {
        // Scrivere una query
        final String query = "SELECT * FROM users WHERE id=?";

        // PreparedStatement
        PreparedStatement ps = getConnection().prepareStatement(query);

        // Settare eventuali parametri necessari per eseguire la query
        ps.setInt(1, id);

        // Eseguire la query (output: ResultSet)
        ResultSet rs = ps.executeQuery();

        // Leggere il ResultSet

        User user = null;

        while(rs.next()) {
            user = new User();
            user.setId(id);
            user.setName(rs.getString("nome"));
            user.setAge(rs.getInt("eta"));
            user.setEmail(rs.getString("email"));
        }

        rs.close();

        return user;
    }

    public MessageIO selectMessageIOById(int userId, String msgId) throws SQLException {
        final String query = "SELECT * FROM msgio WHERE msgId=? AND userId=?";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setString(1, msgId);
        ps.setInt(2, userId);

        ResultSet rs = ps.executeQuery();
        MessageIO msgio = null;

        while(rs.next()) {
            msgio = new MessageIO();
            msgio.setMsgId(msgId);
            msgio.setUserId(userId);
            msgio.setRead(rs.getBoolean("msgread"));
            msgio.setDeleted(rs.getBoolean("msgdel"));
        }

        return msgio;
    }

    public Message selectMessageById(String id) throws SQLException {
        final String query = "SELECT * FROM messages WHERE id=?";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        Message msg = null;

        while(rs.next()) {
            msg = new Message();
            msg.setId(rs.getString("id"));
            msg.setSenderId(rs.getInt("senderId"));
            msg.setContent(rs.getString("content"));
            msg.setDate(rs.getLong("date"));
        }

        rs.close();

        return msg;
    }

    public Comment selectCommentById(int id) throws SQLException, ClassNotFoundException {
        final String query = "SELECT * FROM comments WHERE id=?";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        Comment comment = null;

        while(rs.next()) {
            comment = new Comment();
            comment.setId(id);
            comment.setPostId(rs.getInt("postId"));
            comment.setUserId(rs.getInt("userId"));
            comment.setContent(rs.getString("content"));
            comment.setDate(rs.getLong("date"));
        }

        rs.close();

        return comment;
    }

    public Post selectPostById(int id) throws SQLException, ClassNotFoundException {
        // Scrivere una query
        final String query = "SELECT * FROM posts WHERE id=?";

        // PreparedStatement
        PreparedStatement ps = getConnection().prepareStatement(query);

        // Settare eventuali parametri necessari per eseguire la query
        ps.setInt(1, id);

        // Eseguire la query (output: ResultSet)
        ResultSet rs = ps.executeQuery();

        // Leggere il ResultSet

        Post post = null;

        while(rs.next()) {
            post = new Post();
            post.setId(id);
            post.setUserId(rs.getInt("userId"));
            post.setTitle(rs.getString("title"));
            post.setContent(rs.getString("content"));
            post.setClosed(rs.getBoolean("isClosed"));
            post.setDate(rs.getLong("date"));
        }

        rs.close();

        return post;
    }

    public int insert(User user, String password) throws SQLException, ClassNotFoundException {
        final String query = "INSERT INTO users(email, password, nome, eta) VALUES(?, md5(?), ?, ?)";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setString(1, user.getEmail());
        ps.setString(2, password);
        ps.setString(3, user.getName());
        ps.setInt(4, user.getAge());

        ps.executeUpdate();

        return getLastInsertId();
    }

    public int insert(Post post) throws SQLException, ClassNotFoundException {
        final String query = "INSERT INTO posts(userId, title, content, date, isClosed) VALUES(?, ?, ?, ?, ?)";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setInt(1, post.getUserId());
        ps.setString(2, post.getTitle());
        ps.setString(3, post.getContent());
        ps.setLong(4, post.getDate());
        ps.setBoolean(5, post.isClosed());

        ps.executeUpdate();

        return getLastInsertId();
    }

    public int insert(Comment comment) throws SQLException, ClassNotFoundException {
        String query = "insert into comments (postId, userId, content, date) values (?, ?, ?, ?)";
        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setInt(1, comment.getPostId());
        ps.setInt(2, comment.getUserId());
        ps.setString(3, comment.getContent());
        ps.setLong(4, comment.getDate());
        ps.executeUpdate();

        return getLastInsertId();
    }

    public Message send(Message message, List<User> receivers) throws SQLException {
        final String msgQuery = "INSERT INTO messages (id, senderId, content, date) VALUES (?, ?, ?, ?)";
        final String ioQuery = "INSERT INTO msgio (msgId, userId, msgread, msgdel) VALUES (?, ?, ?, ?)";
        final String newMsgId = UUID.randomUUID().toString();

        message.setDate(System.currentTimeMillis());

        // Crea il messaggio
        PreparedStatement ps = getConnection().prepareStatement(msgQuery);
        ps.setString(1, newMsgId);
        ps.setInt(2, message.getSenderId());
        ps.setString(3, message.getContent());
        ps.setLong(4, message.getDate());

        if(ps.executeUpdate() < 1)
            return message;

        message.setId(newMsgId);

        // Relaziona il messaggio al mittente
        ps = getConnection().prepareStatement(ioQuery);
        ps.setString(1, newMsgId);
        ps.setInt(2, message.getSenderId());
        ps.setInt(3, 1);
        ps.setInt(4, 0);
        ps.executeUpdate();

        // Relaziona il messaggio ai destinatari
        for(User receiver : receivers) {
            if(receiver.getId() == message.getSenderId())
                continue;

            ps.setString(1, newMsgId);
            ps.setInt(2, receiver.getId());
            ps.setInt(3, 0);
            ps.setInt(4, 0);
            ps.executeUpdate();
        }


        return message;
    }

    private int getLastInsertId() throws SQLException, ClassNotFoundException {
        PreparedStatement ps = getConnection().prepareStatement("SELECT LAST_INSERT_ID() AS lastId");
        ResultSet rs = ps.executeQuery();

        int lastInsertedId = (rs.next()) ? rs.getInt("lastId") : -1;
        rs.close();

        return lastInsertedId;
    }

    public int update(User user) throws SQLException, ClassNotFoundException {
        final String query = "UPDATE users SET email=?, nome=?, eta=? WHERE id=?";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setString(1, user.getEmail());
        ps.setString(2, user.getName());
        ps.setInt(3, user.getAge());
        ps.setInt(4, user.getId());

        return ps.executeUpdate();
    }

    public int update(Post post) throws SQLException, ClassNotFoundException {
        final String query = "UPDATE posts SET title=?, content=?, date=?, isClosed=? WHERE id=? AND userId=?";

        PreparedStatement ps = getConnection().prepareStatement(query);

        ps.setString(1, post.getTitle());
        ps.setString(2, post.getContent());
        ps.setLong(3, post.getDate());
        ps.setBoolean(4, post.isClosed());

        ps.setInt(5, post.getId());
        ps.setInt(6, post.getUserId());

        return ps.executeUpdate();
    }

    public int update(Comment comment) throws SQLException, ClassNotFoundException {
        final String query = "UPDATE comments SET content=?, date=? WHERE id=? AND postId=? AND userId=?";

        PreparedStatement ps = getConnection().prepareStatement(query);

        ps.setString(1, comment.getContent());
        ps.setLong(2, comment.getDate());

        ps.setInt(3, comment.getId());
        ps.setInt(4, comment.getPostId());
        ps.setInt(5, comment.getUserId());

        return ps.executeUpdate();
    }

    public int update(int id, String password) throws SQLException, ClassNotFoundException {
        final String query = "UPDATE users SET password=md5(?) WHERE id=?";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setString(1, password);
        ps.setInt(2, id);

        return ps.executeUpdate();
    }

    public int delete(int id) throws SQLException, ClassNotFoundException {
        final String query = "DELETE FROM users WHERE id=?";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setInt(1, id);

        return ps.executeUpdate();
    }

    public int delete(Post post) throws SQLException, ClassNotFoundException {
        final String query = "DELETE FROM posts WHERE id=? AND userId=?";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setInt(1, post.getId());
        ps.setInt(2, post.getUserId());

        return ps.executeUpdate();
    }

    public int delete(Comment comment) throws SQLException, ClassNotFoundException {
        final String query = "DELETE FROM comments WHERE id=? AND postId=? AND userId=?";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setInt(1, comment.getId());
        ps.setInt(2, comment.getPostId());
        ps.setInt(3, comment.getUserId());

        return ps.executeUpdate();
    }

    public int setTrashFlag(MessageIO msgio, boolean trash) throws SQLException {
        final String query = "UPDATE msgio SET msgdel=? WHERE msgId=? AND userId=?";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setInt(1, trash ? 1 : 0);
        ps.setString(2, msgio.getMsgId());
        ps.setInt(3, msgio.getUserId());
        return  ps.executeUpdate();
    }

    public int setReadFlag(MessageIO msgio, boolean trash) throws SQLException {
        final String query = "UPDATE msgio SET msgread=? WHERE msgId=? AND userId=?";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setInt(1, trash ? 1 : 0);
        ps.setString(2, msgio.getMsgId());
        ps.setInt(3, msgio.getUserId());
        return  ps.executeUpdate();
    }

    @Nullable
    public User login(String email, String password) throws SQLException, ClassNotFoundException {
        final String query = "SELECT * FROM users WHERE email=? AND password=md5(?)";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setString(1, email);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();

        User user = null;

        while(rs.next()) {
            user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("nome"));
            user.setAge(rs.getInt("eta"));
            user.setEmail(rs.getString("email"));
        }

        rs.close();

        return user;
    }

    public List<User> selectAll() throws SQLException, ClassNotFoundException {
        // Scrivere una query
        final String query = "SELECT * FROM users";

        // PreparedStatement
        PreparedStatement ps = getConnection().prepareStatement(query);

        // Eseguire la query (output: ResultSet)
        ResultSet rs = ps.executeQuery();

        // Leggere il ResultSet

        List<User> list = new ArrayList<>();

        while(rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("nome"));
            user.setAge(rs.getInt("eta"));
            user.setEmail(rs.getString("email"));

            list.add(user);
        }

        rs.close();

        return list;
    }

    public List<Post> selectAllPosts(Integer userId, Integer limit, Integer offset, int sortedByDate) throws SQLException, ClassNotFoundException {
        final StringBuilder queryBuilder = new StringBuilder("SELECT * FROM posts");

        if(userId != null)
            queryBuilder.append(" WHERE userId=?");

        if(sortedByDate < 0)
            queryBuilder.append(" ORDER BY date DESC");
        else if(sortedByDate > 0)
            queryBuilder.append(" ORDER BY date ASC");

        if(limit != null)
            queryBuilder.append(" LIMIT ?");

        if(offset != null)
            queryBuilder.append(" OFFSET ?");

        final String query = queryBuilder.toString();

        // PreparedStatement
        PreparedStatement ps = getConnection().prepareStatement(query);
        int phCounter = 1;

        if(query.contains("userId=?"))
            ps.setInt(phCounter++, userId);

        if(query.contains("LIMIT"))
            ps.setInt(phCounter++, limit);

        if(query.contains("OFFSET"))
            ps.setInt(phCounter, offset);

        // Eseguire la query (output: ResultSet)
        ResultSet rs = ps.executeQuery();

        // Leggere il ResultSet
        List<Post> posts = new ArrayList<>();

        while(rs.next()) {
            Post post = new Post();

            post.setId(rs.getInt("id"));
            post.setUserId(rs.getInt("userId"));
            post.setTitle(rs.getString("title"));
            post.setContent(rs.getString("content"));
            post.setClosed(rs.getBoolean("isClosed"));
            post.setDate(rs.getLong("date"));

            posts.add(post);
        }

        rs.close();

        return posts;
    }

    public List<Comment> selectAllComments(Integer postId, Integer limit, Integer offset, int sortedByDate) throws SQLException, ClassNotFoundException {
        final StringBuilder queryBuilder = new StringBuilder("SELECT * FROM comments");

        if(postId != null)
            queryBuilder.append(" WHERE postId=?");

        if(sortedByDate < 0)
            queryBuilder.append(" ORDER BY date DESC");
        else if(sortedByDate > 0)
            queryBuilder.append(" ORDER BY date ASC");

        if(limit != null)
            queryBuilder.append(" LIMIT ?");

        if(offset != null)
            queryBuilder.append(" OFFSET ?");

        final String query = queryBuilder.toString();

        // PreparedStatement
        PreparedStatement ps = getConnection().prepareStatement(query);
        int phCounter = 1;

        if(query.contains("postId=?"))
            ps.setInt(phCounter++, postId);

        if(query.contains("LIMIT"))
            ps.setInt(phCounter++, limit);

        if(query.contains("OFFSET"))
            ps.setInt(phCounter, offset);

        // Eseguire la query (output: ResultSet)
        ResultSet rs = ps.executeQuery();

        // Leggere il ResultSet
        List<Comment> comments = new ArrayList<>();

        while(rs.next()) {
            Comment comment = new Comment();

            comment.setId(rs.getInt("id"));
            comment.setPostId(rs.getInt("postId"));
            comment.setUserId(rs.getInt("userId"));
            comment.setContent(rs.getString("content"));
            comment.setDate(rs.getLong("date"));

            comments.add(comment);
        }

        rs.close();

        return comments;
    }

    public List<Message> selectAllSentMessages(Integer userId, Boolean deleted, Integer limit, Integer offset, int sortedByDate) throws SQLException, ClassNotFoundException {
        final StringBuilder queryBuilder = new StringBuilder("SELECT DISTINCT messages.*");
        queryBuilder.append(" FROM msgio INNER JOIN messages ON messages.id = msgio.msgId");
        queryBuilder.append(" WHERE messages.senderId = msgio.userId");

        if(userId != null)
            queryBuilder.append(" AND userId=?");

        if(deleted != null)
            queryBuilder.append(" AND msgio.msgdel=?");

        if(sortedByDate < 0)
            queryBuilder.append(" ORDER BY date DESC");
        else if(sortedByDate > 0)
            queryBuilder.append(" ORDER BY date ASC");

        if(limit != null)
            queryBuilder.append(" LIMIT ?");

        if(offset != null)
            queryBuilder.append(" OFFSET ?");

        final String query = queryBuilder.toString();

        // PreparedStatement
        PreparedStatement ps = getConnection().prepareStatement(query);
        int phCounter = 1;

        if(query.contains("userId=?"))
            ps.setInt(phCounter++, userId);

        if(deleted != null)
            ps.setInt(phCounter++, deleted ? 1 : 0);

        if(query.contains("LIMIT"))
            ps.setInt(phCounter++, limit);

        if(query.contains("OFFSET"))
            ps.setInt(phCounter, offset);

        // Eseguire la query (output: ResultSet)
        ResultSet rs = ps.executeQuery();

        // Leggere il ResultSet
        List<Message> messages = new ArrayList<>();

        while(rs.next()) {
            Message msg = new Message();

            msg.setId(rs.getString("id"));
            msg.setSenderId(rs.getInt("senderId"));
            msg.setContent(rs.getString("content"));
            msg.setDate(rs.getLong("date"));

            messages.add(msg);
        }

        rs.close();

        return messages;
    }

    public List<Message> selectAllReceivedMessages(Integer userId, Boolean read, Boolean deleted, Integer limit, Integer offset, int sortedByDate) throws SQLException, ClassNotFoundException {
        final StringBuilder queryBuilder = new StringBuilder("SELECT DISTINCT messages.*");
        queryBuilder.append(" FROM msgio INNER JOIN messages ON messages.id = msgio.msgId");
        queryBuilder.append(" WHERE messages.senderId <> msgio.userId");

        if(userId != null)
            queryBuilder.append(" AND userId=?");

        if(read != null)
            queryBuilder.append(" AND msgio.msgread=?");

        if(deleted != null)
            queryBuilder.append(" AND msgio.msgdel=?");

        if(sortedByDate < 0)
            queryBuilder.append(" ORDER BY date DESC");
        else if(sortedByDate > 0)
            queryBuilder.append(" ORDER BY date ASC");

        if(limit != null)
            queryBuilder.append(" LIMIT ?");

        if(offset != null)
            queryBuilder.append(" OFFSET ?");

        final String query = queryBuilder.toString();

        // PreparedStatement
        PreparedStatement ps = getConnection().prepareStatement(query);
        int phCounter = 1;

        if(query.contains("userId=?"))
            ps.setInt(phCounter++, userId);

        if(read != null)
            ps.setInt(phCounter++, read ? 1 : 0);

        if(deleted != null)
            ps.setInt(phCounter++, deleted ? 1 : 0);

        if(query.contains("LIMIT"))
            ps.setInt(phCounter++, limit);

        if(query.contains("OFFSET"))
            ps.setInt(phCounter, offset);

        // Eseguire la query (output: ResultSet)
        ResultSet rs = ps.executeQuery();

        // Leggere il ResultSet
        List<Message> messages = new ArrayList<>();

        while(rs.next()) {
            Message msg = new Message();

            msg.setId(rs.getString("id"));
            msg.setSenderId(rs.getInt("senderId"));
            msg.setContent(rs.getString("content"));
            msg.setDate(rs.getLong("date"));

            messages.add(msg);
        }

        rs.close();

        return messages;
    }

    public int countPosts(Integer userId) throws SQLException, ClassNotFoundException {
        final StringBuilder queryBuilder = new StringBuilder("SELECT COUNT(*) AS N FROM posts");

        if(userId != null)
            queryBuilder.append(" WHERE userId=?");

        final String query = queryBuilder.toString();

        // PreparedStatement
        PreparedStatement ps = getConnection().prepareStatement(query);

        if(query.contains("userId=?"))
            ps.setInt(1, userId);

        // Eseguire la query (output: ResultSet)
        ResultSet rs = ps.executeQuery();
        int count = -1;

        if(rs.next())
            count = rs.getInt("N");

        rs.close();

        return count;
    }

    public int countComments(Integer postId) throws SQLException, ClassNotFoundException {
        final StringBuilder queryBuilder = new StringBuilder("SELECT COUNT(*) AS N FROM comments");

        if(postId != null)
            queryBuilder.append(" WHERE postId=?");

        final String query = queryBuilder.toString();

        // PreparedStatement
        PreparedStatement ps = getConnection().prepareStatement(query);

        if(query.contains("postId=?"))
            ps.setInt(1, postId);

        // Eseguire la query (output: ResultSet)
        ResultSet rs = ps.executeQuery();
        int count = -1;

        if(rs.next())
            count = rs.getInt("N");

        rs.close();

        return count;
    }

    public int saveFile(String base64img, User user) throws IOException, SQLException {
        Base64.Decoder base64Dec = Base64.getMimeDecoder();
        byte[] bytes = base64Dec.decode(base64img);

        File file = new File("C:\\Users\\Nicola\\Desktop\\Nuova cartella",
                user.getId() + ".png");

        try(OutputStream os = new FileOutputStream(file)) {
            os.write(bytes);
        }

        final String query = "UPDATE users SET avatar=? WHERE id=?";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setString(1, file.getAbsolutePath());
        ps.setInt(2, user.getId());

        return ps.executeUpdate();
    }
}
