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
    private Integer quantity;
    @DatabaseField()
    private String notes;
    @DatabaseField(unique = true)
    private String position;
    @DatabaseField()
    private Double pricePerUnit;

    public ProductType() { }

    public ProductType(String description, String code, Double pricePerUnit, String notes) {
        this.code = code;
        this.description = description;
        this.notes = notes;
        this.pricePerUnit = pricePerUnit;
        this.quantity=0;
        this.position=null;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getBarCode() {
        return code;
    }

    @Override
    public void setBarCode(String code) {
        this.code = code;
    }

    @Override
    public String getProductDescription() {
        return description;
    }

    @Override
    public void setProductDescription(String description) {
        this.description = description;
    }


    @Override
    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String getNote() {
        return notes;
    }

    @Override
    public void setNote(String notes) {
        this.notes = notes;
    }

    @Override
    public String getLocation() {
        if (position==null) return "";
        return position;
    }

    @Override
    public void setLocation(String position) {
        this.position = position;
    }

    @Override
    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    @Override
    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

}
