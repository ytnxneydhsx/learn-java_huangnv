package PetShop.model;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
public class MyAnimalShop implements AnimalShop {
    private double money;
    private double baseMoney;
    private List<Animal> animalList = new ArrayList<>();
    private List<Customer> customerList = new ArrayList<>();
    private boolean status;

    @Override
    public void buyAnimal(Animal animal) {
        if (this.money > animal.buyPrice) {
            animalList.add(animal);
            this.money -= animal.buyPrice;
        } else {

        }
    }
    @Override
    public void treatCustomer(Customer customer,Animal animal) {
        customerList.add(customer);
        customer.latestVisitDate = LocalDate.now();
        System.out.println(animal.toString());
        animalList.remove(animal);
        this.money += animal.sellPrice;
    }

    @Override
    public void open() {
        this.status = true;
        this.baseMoney=this.money;
    }
    @Override
    public void close() {
        this.status=false;
        LocalDate today = LocalDate.now();
        System.out.println("--- " + today + " 歇业结算 ---");
        System.out.println("今日到店顾客：");
        for (Customer c : customerList) {
            if (c.getLatestVisitDate().equals(today)) {
                System.out.println(c.toString());
            }
        }
        double profit = this.money - this.baseMoney;
        System.out.println("今日利润：" + profit + " 元");

    }
}
