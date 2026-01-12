package PetShop.model;

public class Cat extends Animal {
    public  Cat(String name,int age, boolean gender) {
        super(name, age, gender, 200,400);
    }
    @Override
    public String toString() {
        return "猫 {" +
                "名字='" + name + '\'' +
                ", 年龄=" + age +
                ", 性别='" + gender + '\'' +
                ", 购入价格=" + buyPrice +
                ", 售出价格=" + sellPrice +
                '}';
    }
}