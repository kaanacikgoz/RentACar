package view;

import business.BrandManager;
import core.Helper;
import entity.Brand;

import javax.swing.*;

public class BrandView extends Layout {
    private JPanel container;
    private JLabel lbl_brand;
    private JLabel lbl_brand_name;
    private JTextField fld_brand_name;
    private JButton btn_brand_save;
    private final Brand brand;
    private final BrandManager brandManager;

    public BrandView(Brand brand) {
        this.brandManager = new BrandManager();
        this.brand = brand;
        this.add(container);
        this.guiInitialize(300,200);

        if (this.brand != null && this.brand.getName() != null) {
            this.fld_brand_name.setText(this.brand.getName());
        }

        btn_brand_save.addActionListener(_ -> {
            if (Helper.isFieldEmpty(this.fld_brand_name)) {
                Helper.showMessage("fill");
            } else {
                boolean result;
                if (this.brand == null) {
                    result = this.brandManager.save(new Brand(this.fld_brand_name.getText()));
                } else {
                    this.brand.setName(fld_brand_name.getText());
                    result = this.brandManager.update(this.brand);
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
