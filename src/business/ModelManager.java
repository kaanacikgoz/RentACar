package business;

import core.Helper;
import dao.ModelDao;
import entity.Model;

import java.util.ArrayList;

public class ModelManager {

    private final ModelDao modelDao = new ModelDao();

    public Model getById(int id) {
        return this.modelDao.getById(id);
    }

    public ArrayList<Model> findAll() {
        return this.modelDao.findAll();
    }

    public ArrayList<Object[]> getForTable(int size, ArrayList<Model> models) {
        ArrayList<Object[]> modelList = new ArrayList<>();
        for (Model model:models) {
            int i = 0;
            Object[] rowObject = new Object[size];
            rowObject[i++] = model.getId();
            rowObject[i++] = model.getBrand().getName();
            rowObject[i++] = model.getName();
            rowObject[i++] = model.getType();
            rowObject[i++] = model.getYear();
            rowObject[i++] = model.getFuel();
            rowObject[i] = model.getGear();
            modelList.add(rowObject);
        }
        return modelList;
    }

    public boolean save(Model model) {
        if (this.getById(model.getId()) != null) {
            Helper.showMessage("error");
            return false;
        }
        return this.modelDao.save(model);
    }

    public boolean update(Model model) {
        if (this.getById(model.getId()) == null) {
            Helper.showMessage(STR."\{model.getId()} ID kayıtlı model bulunamadı");
            return false;
        }
        return this.modelDao.update(model);
    }

    public boolean delete(int id) {
        if (this.getById(id) == null) {
            Helper.showMessage(STR."\{id} ID kayıtlı model bulunamadı");
            return false;
        }
        return this.modelDao.delete(id);
    }

    public ArrayList<Model> getByListBrandId(int brandId) {
        return this.modelDao.getByListBrandId(brandId);
    }

    public ArrayList<Model> searchForTable(int brandId, Model.Fuel fuel, Model.Gear gear, Model.Type type) {
        String select = "SELECT * FROM public.model ";
        ArrayList<String> whereList = new ArrayList<>();

        if (brandId != 0) {
            whereList.add(STR." model_brand_id= \{brandId}");
        }
        if (fuel != null) {
            whereList.add(STR." model_fuel= '\{fuel}'");
        }
        if (gear != null) {
            whereList.add(STR." model_gear= '\{gear}'");
        }
        if (type != null) {
            whereList.add(STR." model_type= '\{type}'");
        }
        String whereStr = String.join(" AND ", whereList);
        String query = select;
        if (!whereStr.isEmpty()) {
            query += STR." WHERE \{whereStr}";
        }
        return this.modelDao.selectByQuery(query);
    }

}
