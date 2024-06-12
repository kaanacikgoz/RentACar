package dao;

import core.Db;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import entity.Book;

public class BookDao {

    private final Connection connection;
    private final CarDao carDao;

    public BookDao() {
        this.connection = Db.getInstance();
        this.carDao = new CarDao();
    }

    public ArrayList<Book> findAll() {
        return this.selectByQuery("SELECT * FROM public.book ORDER BY book_id ASC");
    }

    public ArrayList<Book> selectByQuery(String query) {
        ArrayList<Book> books = new ArrayList<>();
        try {
            ResultSet resultSet =  this.connection.createStatement().executeQuery(query);
            while (resultSet.next()) {
                books.add(this.match(resultSet));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return books;
    }

    public Book match(ResultSet resultSet) throws SQLException {
        Book book = new Book();
        book.setId(resultSet.getInt("book_id"));
        book.setbCase(resultSet.getString("book_case"));
        book.setCar_id(resultSet.getInt("book_car_id"));
        book.setName(resultSet.getString("book_name"));
        book.setStrt_date(LocalDate.parse(resultSet.getString("book_strt_date")));
        book.setFnsh_date(LocalDate.parse(resultSet.getString("book_fnsh_date")));
        book.setCar(this.carDao.getById(resultSet.getInt("book_car_id")));
        book.setIdno(resultSet.getString("book_idno"));
        book.setMpno(resultSet.getString("book_mpno"));
        book.setMail(resultSet.getString("book_mail"));
        book.setNote(resultSet.getString("book_note"));
        book.setPrc(resultSet.getInt("book_prc"));
        return book;
    }

    public boolean save(Book book) {
        String query = "INSERT INTO public.book " +
                "(" +
                "book_car_id," +
                "book_name," +
                "book_idno," +
                "book_mpno," +
                "book_mail," +
                "book_strt_date," +
                "book_fnsh_date," +
                "book_prc," +
                "book_case," +
                "book_note" +
                ")" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setInt(1,book.getCar_id());
            preparedStatement.setString(2,book.getName());
            preparedStatement.setString(3,book.getIdno());
            preparedStatement.setString(4,book.getMpno());
            preparedStatement.setString(5,book.getMail());
            preparedStatement.setDate(6,Date.valueOf(book.getStrt_date()));
            preparedStatement.setDate(7,Date.valueOf(book.getFnsh_date()));
            preparedStatement.setInt(8,book.getPrc());
            preparedStatement.setString(9,book.getbCase());
            preparedStatement.setString(10,book.getNote());
            return preparedStatement.executeUpdate() != -1;
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

    public boolean delete(int id) {
        String query = "DELETE FROM public.book " +
                        "WHERE book_id=?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setInt(1,id);
            return preparedStatement.executeUpdate() != -1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public Book getById(int id) {
        Book book = null;
        String query = "SELECT * FROM public.book " +
                        "WHERE book_id=?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                book = this.match(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return book;
    }

}
