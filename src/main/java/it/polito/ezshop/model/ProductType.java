package it.polito.ezshop.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "products")
public class ProductType implements it.polito.ezshop.data.ProductType {

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(unique = true, canBeNull = false)
    private String code;
    @DatabaseField(canBeNull = false)
    private String description;
    @DatabaseField()
    private double salePrice;
    @DatabaseField()
    private int quantity;
    @DatabaseField()
    private String notes;
    @DatabaseField()
    private String position;
    @DatabaseField()
    private double pricePerUnit;

    ProductType() { }

    public ProductType(String description, String productCode, double pricePerUnit, String note) {

    }

    @Override
    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(Integer quantity) {

    }

    @Override
    public String getLocation() {
        return null;
    }

    @Override
    public void setLocation(String location) {

    }

    @Override
    public String getNote() {
        return null;
    }

    @Override
    public void setNote(String note) {

    }

    @Override
    public String getProductDescription() {
        return null;
    }

    @Override
    public void setProductDescription(String productDescription) {

    }

    @Override
    public String getBarCode() {
        return null;
    }

    @Override
    public void setBarCode(String barCode) {

    }

    @Override
    public Double getPricePerUnit() {
        return null;
    }

    @Override
    public void setPricePerUnit(Double pricePerUnit) {

    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public void setId(Integer id) {

    }
}
