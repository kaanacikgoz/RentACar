package dao;

import core.Db;
import entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDao {

    private final Connection connection;

    public UserDao() {
        this.connection = Db.getInstance();
    }

    public ArrayList<User> findAll() {
        ArrayList<User> userArrayList = new ArrayList<>();
        String query = "SELECT * FROM public.user";
        try {
            ResultSet resultSet = this.connection.createStatement().executeQuery(query);
            while (resultSet.next()) {
                userArrayList.add(match(resultSet));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return userArrayList;
    }

    public User findByLogin(String username, String password) {
        User object = null;
        String query = "SELECT * FROM public.user " +
                "WHERE user_name = ? AND user_password = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                object = match(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return object;
    }

    public User match(ResultSet resultSet) throws SQLException {
        User user = new User();

        user.setId(resultSet.getInt("user_id"));
        user.setUsername(resultSet.getString("user_name"));
        user.setPassword(resultSet.getString("user_password"));
        user.setRole(resultSet.getString("user_role"));

        return user;
    }

}
