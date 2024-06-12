package business;

import core.Helper;
import entity.Book;
import dao.BookDao;


import java.util.ArrayList;

public class BookManager {

    private final BookDao bookDao;

    public BookManager() {
        this.bookDao = new BookDao();
    }

    public boolean save(Book book) {
        return this.bookDao.save(book);
    }

    private Book getById(int id) {
        return this.bookDao.getById(id);
    }

    public boolean delete(int id) {
        if (this.getById(id)==null) {
            Helper.showMessage(STR."\{id}ID kayıtlı rezervasyon bulunamadı");
            return false;
        }
        return this.bookDao.delete(id);
    }

    public ArrayList<Book> findAll() {
        return this.bookDao.findAll();
    }

    public ArrayList<Object[]> getForTable(int size, ArrayList<Book> books) {
        ArrayList<Object[]> bookList = new ArrayList<>();
        for (Book book : books) {
            int i = 0;
            Object[] rowObject = new Object[size];
            rowObject[i++] = book.getId();
            rowObject[i++] = book.getCar().getPlate();
            rowObject[i++] = book.getCar().getModel().getBrand().getName();
            rowObject[i++] = book.getCar().getModel().getName();
            rowObject[i++] = book.getName();
            rowObject[i++] = book.getMpno();
            rowObject[i++] = book.getMail();
            rowObject[i++] = book.getIdno();
            rowObject[i++] = book.getStrt_date();
            rowObject[i++] = book.getFnsh_date();
            rowObject[i] = book.getPrc();
            bookList.add(rowObject);
        }
        return bookList;
    }

    public ArrayList<Book> searchForTable(int id) {
        String query = "SELECT * FROM public.book";
        ArrayList<String> whereList = new ArrayList<>();
        if (id != 0) {
            whereList.add(STR."book_car_id = \{id}");
        }
        String whereStr = String.join(" AND ", whereList);
        if (!whereStr.isEmpty()) {
            query += STR." WHERE \{whereStr}";
        }
        return this.bookDao.selectByQuery(query);
    }

}
