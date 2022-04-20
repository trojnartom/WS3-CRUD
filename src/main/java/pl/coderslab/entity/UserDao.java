package pl.coderslab.entity;

import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDao {

    private static final String CREATE_USER_QUERY = "INSERT INTO users (username, email, password) VALUES (?, ?, ?) ";
    private static final String CHANGE_USER_QUERY = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";
    private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String USERS_WHERE_ID = "SELECT id FROM users WHERE id = ?";
    private static final String DELETE_USER_BY_ID_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users";

    public User create(User user) {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement createStmt = conn.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            createStmt.setString(1, user.getUserName());
            createStmt.setString(2, user.getEmail());
            createStmt.setString(3, hashPassword(user.getPassword()));
            createStmt.executeUpdate();
            ResultSet rs = createStmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
            return user;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private User[] addToArray(User u, User[] users) {
        User[] tmpUsers = Arrays.copyOf(users, users.length + 1);
        tmpUsers[users.length] = u;
        return tmpUsers;
    }


    public User read(int userId) {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement preStmt = conn.prepareStatement(USERS_WHERE_ID);
            preStmt.setInt(1, userId);
            ResultSet rs = preStmt.executeQuery();
            if (rs.isBeforeFirst()) {
                User user = new User();
                PreparedStatement preStmt2 = conn.prepareStatement(FIND_USER_BY_ID_QUERY);
                preStmt2.setInt(1, userId);
                ResultSet result = preStmt2.executeQuery();
                if (result.next()) {
                    user.setId(userId);
                    user.setUserName(result.getString("username"));
                    user.setEmail(result.getString("email"));
                    user.setPassword(result.getString("password"));
                }
                return user;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(User user) {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement updateStmt = conn.prepareStatement(CHANGE_USER_QUERY);
            updateStmt.setString(1, user.getUserName());
            updateStmt.setString(2, user.getEmail());
            updateStmt.setString(3, user.getPassword());
            updateStmt.setString(4, String.valueOf(user.getId()));
            updateStmt.executeUpdate();
            System.out.println("User updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int userId) {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement deleteStmt = conn.prepareStatement(DELETE_USER_BY_ID_QUERY);
            deleteStmt.setString(1, String.valueOf(userId));
            if (deleteStmt.executeUpdate() != 0) {
                System.out.println("User ID: " + userId + " deleted successfully");
            } else {
                System.out.println("Ups...something went wrong!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void findAll () {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement findAllStmt = conn.prepareStatement(FIND_ALL_USERS_QUERY);
            ResultSet findAllUsers = findAllStmt.executeQuery();
            User[] users = new User[0];
                while (findAllUsers.next()) {
                    int userId = findAllUsers.getInt("id");
                    User u = read(userId);
                    users = addToArray(u, users);
                }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> findAllUsersList() {
        try (Connection conn = DBUtil.getConnection()) {
        PreparedStatement findAllUserStmt = conn.prepareStatement(FIND_ALL_USERS_QUERY);
        ResultSet resultSet = findAllUserStmt.executeQuery();
        List<User> userList = new ArrayList<>();
        while (resultSet.next()) {
            int userId = resultSet.getInt("id");
            User u = read(userId);
            userList.add(u);
        }
        return userList;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}