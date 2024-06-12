package view;

import business.BookManager;
import business.BrandManager;
import business.CarManager;
import business.ModelManager;
import core.ComboItem;
import core.Helper;
import entity.Brand;
import entity.Car;
import entity.Model;
import entity.User;
import entity.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.event.*;
import java.text.ParseException;
import java.util.ArrayList;

public class AdminView extends Layout {

    private JPanel container;
    private JLabel lbl_welcome;
    private JPanel pnl_top;
    private JTabbedPane tab_menu;
    private JButton btn_logout;
    private JPanel pnl_brand;
    private JScrollPane scrl_brand;
    private JTable tbl_brand;
    private JPanel pnl_model;
    private JScrollPane scrl_model;
    private JTable tbl_model;
    private JLabel lbl_brand;
    private JLabel lbl_brand_type;
    private JLabel lbl_brand_vites;
    private JLabel lbl_brand_fuel;
    private JComboBox<Model.Type> cmb_s_model_type;
    private JComboBox<Model.Gear> cmb_s_model_gear;
    private JComboBox<Model.Fuel> cmb_s_model_fuel;
    private JComboBox<ComboItem> cmb_s_model_brand;
    //private JComboBox<ComboItem> cmb_s_model_brand;
    private JButton btn_brand_filter;
    private JButton btn_brand_cancel;
    private JPanel pnl_car;
    private JTable tbl_car;
    private JPanel pnl_booking;
    private JScrollPane scrl_booking;
    private JTable tbl_booking;
    private JPanel pnl_booking_search;
    private JFormattedTextField fld_strt_date;
    private JFormattedTextField fld_fnsh_date;
    private JComboBox<Model.Gear> cmb_booking_gear;
    private JComboBox<Model.Fuel> cmb_booking_fuel;
    private JComboBox<Model.Type> cmb_booking_type;
    private JButton btn_booking_car;
    private JButton btn_booking_clear;
    private JTable tbl_book;
    private JComboBox<ComboItem> cmb_bookCar;
    private JButton btn_bookCar_search;
    private JButton btn_bookCar_clear;
    private User user;
    private final DefaultTableModel tmdl_brand = new DefaultTableModel();
    private final DefaultTableModel tmdl_model = new DefaultTableModel();
    private final DefaultTableModel tmdl_car = new DefaultTableModel();
    private final DefaultTableModel tmdl_booking = new DefaultTableModel();
    private final DefaultTableModel tmdl_book = new DefaultTableModel();
    private final BrandManager brandManager;
    private final ModelManager modelManager;
    private final CarManager carManager;
    private final BookManager bookManager;
    private final JPopupMenu brandMenu = new JPopupMenu();
    private final JPopupMenu modelMenu = new JPopupMenu();
    private final JPopupMenu car_menu = new JPopupMenu();
    private final JPopupMenu booking_menu = new JPopupMenu();
    private final JPopupMenu book_menu = new JPopupMenu();
    private Object[] col_model;
    private Object[] col_car;
    private Object[] col_book;

    public AdminView(User user) {
        this.brandManager = new BrandManager();
        this.modelManager = new ModelManager();
        this.carManager = new CarManager();
        this.bookManager = new BookManager();
        this.add(container);
        this.guiInitialize(1000,500);
        this.user = user;
        if (this.user==null) {
            this.dispose();
        }
        this.lbl_welcome.setText(STR."Hoşgeldiniz: \{this.user.getUsername()}");

        //General Code
        loadComponent();

        // Brand Tab Menu
        loadBrandTable();
        loadBrandComponent();

        // Model Tab Menu
        loadModelTable(null);
        loadModelComponent();
        loadModelFilter();

        // Car Tab Menu
        loadCarTable();
        loadCarComponent();

        // Booking Tab Menu
        loadBookingTable(null);
        loadBookingComponent();
        loadBookingFilter();

        // Book Tab Menu
        loadBookTable(null);
        loadBookComponent();
        loadBookFilterCar();
    }

    private void loadBookComponent() {
        tableRowSelect(this.tbl_book, this.book_menu);
        this.book_menu.add("İptal Et").addActionListener(_ -> {
            if (Helper.confirm("sure")) {
                int selectBookId = this.getTableSelectedRow(this.tbl_book, 0);
                if (this.bookManager.delete(selectBookId)) {
                    Helper.showMessage("done");
                    loadBookTable(null);
                } else {
                    Helper.showMessage("error");
                }
            }
        });
        this.tbl_book.setComponentPopupMenu(this.book_menu);

        btn_bookCar_search.addActionListener(_ -> {
            ComboItem selectedCar = (ComboItem) this.cmb_bookCar.getSelectedItem();
            int carId = 0;
            if (selectedCar != null) {
                carId = selectedCar.getKey();
            }
            ArrayList<Book> bookListBySearch = this.bookManager.searchForTable(carId);
            ArrayList<Object[]> bookRowListBySearch = this.bookManager.getForTable(this.col_book.length, bookListBySearch);
            loadBookTable(bookRowListBySearch);
        });

        btn_bookCar_clear.addActionListener(_ -> loadBookFilterCar());
    }

    private void loadBookTable(ArrayList<Object[]> bookList) {
        col_book = new Object[]{"ID", "Plaka", "Marka", "Model", "Müşteri", "Telefon", "Mail", "TC", "Başlangıç Tarihi", "Bitiş Tarihi", "Fiyat"};
        if (bookList==null) {
            bookList = this.bookManager.getForTable(col_book.length, this.bookManager.findAll());
        }
        createTable(this.tmdl_book, this.tbl_book, col_book, bookList);
    }

    private void loadBookFilterCar() {
        this.cmb_bookCar.removeAllItems();
        for (Car car : this.carManager.findAll()) {
            this.cmb_bookCar.addItem(new ComboItem(car.getId(), car.getPlate()));
        }
        this.cmb_bookCar.setSelectedItem(null);
    }

    private void loadComponent() {
        this.btn_logout.addActionListener(_ -> {
            dispose();
            LoginView _ = new LoginView();
        });
    }

    private void loadBookingTable(ArrayList<Object[]> bookingList) {
        Object[] col_booking_list = {"ID", "Marka", "Model", "Plaka", "Renk", "KM", "Yıl", "Tip", "Yakıt Türü", "Vites"};
        createTable(this.tmdl_booking, this.tbl_booking, col_booking_list, bookingList);
    }

    private void loadBookingComponent() {
        tableRowSelect(this.tbl_booking, booking_menu);
        this.booking_menu.add("Rezervasyon Yap").addActionListener(_ -> {
            int selectedCarId = this.getTableSelectedRow(this.tbl_booking, 0);
            BookingView bookingView = new BookingView(
                    this.carManager.getById(selectedCarId),
                    this.fld_strt_date.getText(),
                    this.fld_fnsh_date.getText()
            );
            bookingView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBookingTable(null);
                    loadBookingFilter();
                }
            });
        });
        this.tbl_booking.setComponentPopupMenu(this.booking_menu);

        btn_booking_car.addActionListener(_ -> {
            ArrayList<Car> carList = this.carManager.searchForBooking(
                    this.fld_strt_date.getText(),
                    this.fld_fnsh_date.getText(),
                    (Model.Type) this.cmb_booking_type.getSelectedItem(),
                    (Model.Gear) this.cmb_booking_gear.getSelectedItem(),
                    (Model.Fuel) this.cmb_booking_fuel.getSelectedItem()
            );
            ArrayList<Object[]> carBookingRow = this.carManager.getForTable(this.col_car.length, carList);
            loadBookingTable(carBookingRow);
        });

        btn_booking_clear.addActionListener(_ -> loadBookingFilter());
    }

    private void loadBookingFilter() {
        this.cmb_booking_type.setModel(new DefaultComboBoxModel<>(Model.Type.values()));
        this.cmb_booking_type.setSelectedItem(null);
        this.cmb_booking_gear.setModel(new DefaultComboBoxModel<>(Model.Gear.values()));
        this.cmb_booking_gear.setSelectedItem(null);
        this.cmb_booking_fuel.setModel(new DefaultComboBoxModel<>(Model.Fuel.values()));
        this.cmb_booking_fuel.setSelectedItem(null);
    }

    private void loadCarTable() {
        col_car = new Object[]{"ID", "Marka", "Model", "Plaka", "Renk", "KM", "Yıl", "Tip", "Yakıt Türü", "Vites"};
        ArrayList<Object[]> carList = this.carManager.getForTable(col_car.length, this.carManager.findAll());
        createTable(this.tmdl_car, this.tbl_car, col_car, carList);
    }

    private void loadCarComponent() {
        tableRowSelect(this.tbl_car, this.car_menu);
        this.car_menu.add("Yeni").addActionListener(_ -> {
            CarView carView = new CarView(new Car());
            carView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCarTable();
                }
            });
        });
        this.car_menu.add("Güncelle").addActionListener(_ -> {
            int selectModelId = this.getTableSelectedRow(this.tbl_car, 0);
            CarView carView = new CarView(this.carManager.getById(selectModelId));
            carView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCarTable();
                    loadBrandTable();
                }
            });
        });
        this.car_menu.add("Sil").addActionListener(_ -> {
            if (Helper.confirm("sure")) {
                int selectModelId = this.getTableSelectedRow(this.tbl_car, 0);
                if (this.carManager.delete(selectModelId)) {
                    Helper.showMessage("done");
                    loadModelTable(null);
                } else {
                    Helper.showMessage("error");
                }
            }
        });
        this.tbl_car.setComponentPopupMenu(car_menu);
    }

    private void loadModelFilter() {
        this.cmb_s_model_type.setModel(new DefaultComboBoxModel<>(Model.Type.values()));
        this.cmb_s_model_type.setSelectedItem(null);
        this.cmb_s_model_gear.setModel(new DefaultComboBoxModel<>(Model.Gear.values()));
        this.cmb_s_model_gear.setSelectedItem(null);
        this.cmb_s_model_fuel.setModel(new DefaultComboBoxModel<>(Model.Fuel.values()));
        this.cmb_s_model_fuel.setSelectedItem(null);
        loadModelFilterBrand();
    }

    public void loadModelFilterBrand() {
        this.cmb_s_model_brand.removeAllItems();
        for (Brand brand : brandManager.findAll()) {
            this.cmb_s_model_brand.addItem(new ComboItem(brand.getId(), brand.getName()));
        }
        this.cmb_s_model_brand.setSelectedItem(null);
    }

    private void loadModelComponent() {
        this.tableRowSelect(this.tbl_model, modelMenu);

        this.modelMenu.add("Yeni").addActionListener(_ -> {
            ModelView modelView = new ModelView(new Model());
            modelView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadModelTable(null);
                }
            });
        });
        this.modelMenu.add("Güncelle").addActionListener(_ -> {
            int selectModelId = this.getTableSelectedRow(this.tbl_model, 0);
            ModelView modelView = new ModelView(this.modelManager.getById(selectModelId));
            modelView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadModelTable(null);
                    loadCarTable();
                    loadBookingTable(null);
                    loadBookTable(null);
                }
            });
        });
        this.modelMenu.add("Sil").addActionListener(_ -> {
            if (Helper.confirm("sure")) {
                int selectModelId = this.getTableSelectedRow(this.tbl_model, 0);
                if (this.modelManager.delete(selectModelId)) {
                    Helper.showMessage("done");
                    loadModelTable(null);
                } else {
                    Helper.showMessage("error");
                }
            }
        });
        this.tbl_model.setComponentPopupMenu(modelMenu);

        this.btn_brand_filter.addActionListener(_ -> {
            ComboItem selectedBrand = (ComboItem) this.cmb_s_model_brand.getSelectedItem();
            int brandId = 0;
            if (selectedBrand != null) {
                brandId = selectedBrand.getKey();
            }
            ArrayList<Model> modelListBySearch = this.modelManager.searchForTable(
                    brandId,
                    (Model.Fuel) cmb_s_model_fuel.getSelectedItem(),
                    (Model.Gear) cmb_s_model_gear.getSelectedItem(),
                    (Model.Type) cmb_s_model_type.getSelectedItem()
            );

            ArrayList<Object[]> modelRowListBySearch = this.modelManager.getForTable(this.col_model.length, modelListBySearch);
            loadModelTable(modelRowListBySearch);
        });

        this.btn_brand_cancel.addActionListener(_ -> {
            this.cmb_s_model_type.setSelectedItem(null);
            this.cmb_s_model_gear.setSelectedItem(null);
            this.cmb_s_model_fuel.setSelectedItem(null);
            this.cmb_s_model_brand.setSelectedItem(null);
            loadModelTable(null);
        });

    }

    public void loadBrandTable() {
        Object[] col_brand = {"Marka ID", "Marka Adı"};
        ArrayList<Object[]> brandArrayList = this.brandManager.getForTable(col_brand.length);
        this.createTable(this.tmdl_brand, this.tbl_brand, col_brand, brandArrayList);
    }

    public void loadBrandComponent() {
        this.tableRowSelect(this.tbl_brand, this.brandMenu);

        this.brandMenu.add("Yeni").addActionListener(_ -> {
            BrandView brandView = new BrandView(null);
            brandView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBrandTable();
                    loadModelTable(null);
                    loadModelFilterBrand();
                }
            });
        });
        this.brandMenu.add("Güncelle").addActionListener(_ -> {
            int selectBrandId = this.getTableSelectedRow(this.tbl_brand, 0);
            BrandView brandView = new BrandView(this.brandManager.getById(selectBrandId));
            brandView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBrandTable();
                    loadModelTable(null);
                    loadModelFilterBrand();
                    loadCarTable();
                    loadBookTable(null);
                }
            });
        });
        this.brandMenu.add("Sil").addActionListener(_ -> {
            if (Helper.confirm("sure")) {
                int selectBrandId = this.getTableSelectedRow(this.tbl_brand, 0);
                if (this.brandManager.delete(selectBrandId)) {
                    Helper.showMessage("done");
                    loadBrandTable();
                    loadModelTable(null);
                    loadModelFilterBrand();
                    loadCarTable();
                    loadBookTable(null);
                } else {
                    Helper.showMessage("error");
                }
            }
        });
        this.tbl_brand.setComponentPopupMenu(brandMenu);
    }

    public void loadModelTable(ArrayList<Object[]> modelList) {
        this.col_model = new Object[]{"Model ID", "Marka", "Model Adı", "Tip", "Yıl", "Yakıt Türü", "Vites"};
        if (modelList==null) {
            modelList = this.modelManager.getForTable(this.col_model.length, this.modelManager.findAll());
        }
        createTable(this.tmdl_model, this.tbl_model, col_model, modelList);
    }

    private void createUIComponents() throws ParseException {
        // TODO: place custom component creation code here
        this.fld_strt_date = new JFormattedTextField(new MaskFormatter("##/##/####"));
        this.fld_strt_date.setText("10/10/2023");
        this.fld_fnsh_date = new JFormattedTextField(new MaskFormatter("##/##/####"));
        this.fld_fnsh_date.setText("16/10/2023");
    }
}
