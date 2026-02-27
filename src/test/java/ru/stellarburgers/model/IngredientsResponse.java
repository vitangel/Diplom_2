package ru.stellarburgers.model;

import java.util.List;

public class IngredientsResponse {
    private boolean success;
    private List<Ingredient> data;

    public static class Ingredient {
        private String _id;
        private String name;
        private String type;
        private int price;

        // геттеры и сеттеры
        public String get_id() { return _id; }
        public void set_id(String _id) { this._id = _id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public int getPrice() { return price; }
        public void setPrice(int price) { this.price = price; }
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public List<Ingredient> getData() { return data; }
    public void setData(List<Ingredient> data) { this.data = data; }
}