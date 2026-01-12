package PetShop.model;

public abstract class Animal {
    protected String name;
    protected int age;
    protected boolean gender;
    protected double buyPrice;
    protected double sellPrice;

    public Animal(String name,int age, boolean gender ,double buyPrice,double sellPrice) {
        this.name = name;
        this.age= age;
        this.gender=gender;
        this.buyPrice=buyPrice;
        this.sellPrice=sellPrice;
    }

    public abstract String toString();

}






