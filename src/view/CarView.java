package view;

import business.CarManager;
import business.ModelManager;
import core.ComboItem;
import core.Helper;
import entity.Car;
import entity.Model;

import javax.swing.*;

public class CarView extends Layout {
    private JPanel container;
    private JLabel lbl_header;
    private JComboBox<ComboItem> cmb_model;
    private JComboBox<Car.Color> cmb_color;
    private JTextField fld_km;
    private JTextField fld_plate;
    private JButton btn_car_save;
    private JLabel lbl_car_model;
    private JLabel lbl_car_color;
    private JLabel lbl_car_km;
    private JLabel lbl_car_plate;
    private final Car car;
    private final CarManager carManager;

    public CarView(Car car) {
        this.car = car;
        this.carManager = new CarManager();
        ModelManager modelManager = new ModelManager();
        this.add(container);
        this.guiInitialize(300,400);

        this.cmb_color.setModel(new DefaultComboBoxModel<>(Car.Color.values()));
        for (Model model: modelManager.findAll()) {
            this.cmb_model.addItem(model.getComboItem());
        }

        if (this.car.getId()!=0) {
            ComboItem selectedItem = car.getModel().getComboItem();
            this.cmb_model.getModel().setSelectedItem(selectedItem);
            this.cmb_color.getModel().setSelectedItem(car.getColor());
            this.fld_plate.setText(car.getPlate());
            this.fld_km.setText(Integer.toString(car.getKm()));
        }

        this.btn_car_save.addActionListener(_ -> {
            if (Helper.isFieldListEmpty(new JTextField[]{this.fld_km, this.fld_plate})) {
                Helper.showMessage("fill");
            } else {
                boolean result;
                ComboItem selectedModel = (ComboItem) this.cmb_model.getSelectedItem();
                assert selectedModel != null;
                this.car.setModel_id(selectedModel.getKey());
                this.car.setColor((Car.Color) this.cmb_color.getSelectedItem());
                this.car.setPlate(this.fld_plate.getText());
                this.car.setKm(Integer.parseInt(this.fld_km.getText()));
                if (this.car.getId() != 0) {
                    result = this.carManager.update(this.car);
                } else {
                    result = this.carManager.save(this.car);
                }
                if (result) {
                    Helper.showMessage("done");
                    dispose();
                } else {
                    Helper.showMessage("error");
                }
            }
        });

    }
}
