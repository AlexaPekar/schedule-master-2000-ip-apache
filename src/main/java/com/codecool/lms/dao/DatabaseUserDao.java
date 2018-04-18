package com.codecool.lms.dao;

import com.codecool.lms.exception.UserNotFoundException;
import com.codecool.lms.exception.WrongPasswordException;
import com.codecool.lms.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUserDao extends AbstractDao implements UserDao {
    public DatabaseUserDao(Connection connection) {
        super(connection);
    }

    @Override
    public List<User> findUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email, password, connected, type FROM users;";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                users.add(fetchUser(resultSet));
            }
        }
        return null;
    }

    @Override
    public void register(String name, String email, String password, String type) throws SQLException {
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        String sql = "INSERT INTO users (name, email, password, connected, type) VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, password);
            statement.setBoolean(4, false);
            statement.setString(5, type);
            executeInsert(statement);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(autoCommit);
        }

    }

    @Override
    public User findUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return fetchUser(resultSet);
            }
        }
        return null;
    }

    @Override
    public User findUserByEmailAndPassword(String email, String password) throws SQLException, WrongPasswordException {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return fetchUser(resultSet);
            }
        }
        throw new WrongPasswordException();
    }


    @Override
    public User findUserByName(String name) throws SQLException, UserNotFoundException {
        String sql = "SELECT * FROM users WHERE name = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return fetchUser(resultSet);
            }
        }
        throw new UserNotFoundException();
    }

    @Override
    public User findUserById(int id) throws SQLException, UserNotFoundException {
        String sql = "SELECT * FROM users WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return fetchUser(resultSet);
            }
        }
        throw new UserNotFoundException();
    }

    @Override
    public void insertDay(String date) throws SQLException {
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        String sql = "INSERT INTO days (date) VALUES (?);";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, date);
            executeInsert(statement);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

    @Override
    public List<Day> findDays() throws SQLException {
        List<Day> days = new ArrayList<>();
        String sql = "SELECT id, \"date\" FROM days;";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                days.add(fetchDay(resultSet));
            }
        }
        return null;
    }

    @Override
    public Day findDayByDate(String date) throws SQLException {
        String sql = "SELECT id, \"date\" FROM days WHERE \"date\" = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, date);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return fetchDay(resultSet);
            }
        }
        return null;
    }

    @Override
    public void updateAttendance(Day day, List<Student> students) throws SQLException {
        deleteDayFromAttendance(day);
        insertAttendance(day, students);
    }

    public void insertAttendance(Day day, List<Student> students) throws SQLException {
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        String sql = "INSERT INTO attendance (day_id, student_id) VALUES (?, ?);";
        for (Student student : students) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, day.getId());
                statement.setInt(2, student.getId());
                executeInsert(statement);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        }
    }

    public void deleteDayFromAttendance(Day day) throws SQLException {
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        String sql = "DELETE FROM attendance WHERE day_id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, day.getId());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

    @Override
    public List<Student> findStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE type = 'Student';";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                students.add((Student) fetchUser(resultSet));
            }
        }
        return students;
    }

    @Override
    public void changeUserRole(User user, String type) throws SQLException {
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        String sql = "UPDATE users SET \"type\" = ? WHERE \"id\" = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, type);
            statement.setInt(2, user.getId());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(autoCommit);
        }

    }

    @Override
    public User changeUserName(User user, String newName) {
        return null;
    }

    @Override
    public User changeUserPassword(User user, String password) {
        return null;
    }

    public User fetchUser(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        if (email.equals("") || email == null) {
            return null;
        }
        String password = resultSet.getString("password");
        boolean connected = resultSet.getBoolean("connected");
        String type = resultSet.getString("type");
        if (type.equals("Mentor")) {
            return new Mentor(id, name, email, password);
        } else if (type.equals("Student")) {
            return new Student(id, name, email, password);
        }
        return null;
    }

    //TODO: implement fetchDay
    public Day fetchDay(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String date = resultSet.getString("date");
        List<Student> students = findStudentById(findStudentIdsByDayId(id));
        return new Day(id, students, date);
    }

    @Override
    public List<Integer> findStudentIdsByDayId(int dayId) throws SQLException {
        List<Integer> studentIds = new ArrayList<>();
        String sql = "SELECT student_id FROM attendance WHERE day_id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, dayId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                studentIds.add(resultSet.getInt("student_id"));
            }
        }
        return studentIds;
    }

    @Override
    public List<Student> findStudentById(List<Integer> studentIds) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE id = ? AND type = 'Student';";
        for (int studentId : studentIds) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, studentId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    students.add((Student) fetchUser(resultSet));
                }
            }
        }
        return students;
    }

    @Override
    public void connectUserWithGithub(User user, GitHub gitHub) {

    }

    @Override
    public void disconnectUserFromGithub(User user) {

    }
}
