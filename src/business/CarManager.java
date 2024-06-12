package business;

import core.Helper;
import dao.BookDao;
import dao.CarDao;
import entity.Car;
import entity.Model;
import entity.Book;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CarManager {

    private final CarDao carDao;
    private final BookDao bookDao;

    public CarManager() {
        this.carDao = new CarDao();
        this.bookDao = new BookDao();
    }

    public Car getById(int id) {
        return this.carDao.getById(id);
    }

    public ArrayList<Car> findAll() {
        return this.carDao.findAll();
    }

    public ArrayList<Object[]> getForTable(int size, ArrayList<Car> cars) {
        ArrayList<Object[]> carList = new ArrayList<>();
        for (Car car:cars) {
            int i = 0;
            Object[] rowObject = new Object[size];
            rowObject[i++] = car.getId();
            rowObject[i++] = car.getModel().getBrand().getName();
            rowObject[i++] = car.getModel().getName();
            rowObject[i++] = car.getPlate();
            rowObject[i++] = car.getColor();
            rowObject[i++] = car.getKm();
            rowObject[i++] = car.getModel().getYear();
            rowObject[i++] = car.getModel().getType();
            rowObject[i++] = car.getModel().getFuel();
            rowObject[i] = car.getModel().getGear();
            carList.add(rowObject);
        }
        return carList;
    }

    public boolean save(Car car) {
        if (this.getById(car.getId())!=null) {
            Helper.showMessage("error");
            return false;
        }
        return this.carDao.save(car);
    }

    public boolean update(Car car) {
        if (this.getById(car.getId())==null) {
            Helper.showMessage(STR."\{car.getId()}ID kayıtlı araç bulunamadı");
            return false;
        }
        return this.carDao.update(car);
    }

    public boolean delete(int id) {
        if (this.getById(id)==null) {
            Helper.showMessage(STR."\{id}ID kayıtlı araç bulunamadı");
            return false;
        }
        return this.carDao.delete(id);
    }

    public ArrayList<Car> searchForBooking(String strt_date, String fnsh_date, Model.Type type, Model.Gear gear, Model.Fuel fuel) {
        String query = "SELECT * FROM public.car as c LEFT JOIN public.model as m ";

        ArrayList<String> where = new ArrayList<>();
        ArrayList<String> joinWhere = new ArrayList<>();
        ArrayList<String> bookOrWhere = new ArrayList<>();

        joinWhere.add("c.car_model_id = m.model_id ");

        strt_date = LocalDate.parse(strt_date, DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString();
        fnsh_date = LocalDate.parse(fnsh_date, DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString();

        if (fuel != null) where.add(STR."m.model_fuel = '\{fuel}'");
        if (gear != null) where.add(STR."m.model_gear = '\{gear}'");
        if (type != null) where.add(STR."m.model_type = '\{type}'");

        String whereStr = String.join(" AND ", where);
        String joinStr = String.join(" AND ", joinWhere);

        if (!joinStr.isEmpty()) {
            query += STR." ON \{joinStr}";
        }
        if (!whereStr.isEmpty()) {
            query += STR." WHERE \{whereStr}";
        }

        ArrayList<Car> searchedCarList = this.carDao.selectByQuery(query);

        bookOrWhere.add(STR."('\{strt_date}' BETWEEN book_strt_date AND book_fnsh_date)");
        bookOrWhere.add(STR."('\{fnsh_date}' BETWEEN book_strt_date AND book_fnsh_date)");
        bookOrWhere.add(STR."(book_strt_date BETWEEN '\{strt_date}' AND '\{fnsh_date}')");
        bookOrWhere.add(STR."(book_fnsh_date BETWEEN '\{strt_date}' AND '\{fnsh_date}')");

        String bookOrWhereStr = String.join(" OR ", bookOrWhere);
        String bookQuery = STR."SELECT * FROM public.book WHERE \{bookOrWhereStr}";

        ArrayList<Book> bookList =  this.bookDao.selectByQuery(bookQuery);
        ArrayList<Integer> busyCarId = new ArrayList<>();
        for(Book book:bookList) {
            busyCarId.add(book.getCar_id());
        }
        searchedCarList.removeIf(car -> busyCarId.contains(car.getId()));

        return searchedCarList;
    }

}
