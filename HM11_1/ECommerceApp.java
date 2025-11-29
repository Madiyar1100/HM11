import java.util.*;

abstract class User {
    protected String id;
    protected String name;
    protected String email;
    protected String address;
    protected String phone;
    protected String role;

    public User(String id, String name, String email, String address, String phone, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.role = role;
    }

    public abstract void register();
    public abstract boolean login(String email, String name);
    public abstract void updateData(String newName, String newAddress, String newPhone);

    public String getName() { return name; }
    public String getRole() { return role; }
}

class Client extends User {
    private int loyaltyPoints = 0;
    private List<Order> myOrders = new ArrayList<>();

    public Client(String id, String name, String email, String address, String phone) {
        super(id, name, email, address, phone, "client");
    }

    public void addPoints(double amount) {
        loyaltyPoints += (int)(amount / 10);
    }

    public int getPoints() { return loyaltyPoints; }

    public void register() {}
    public boolean login(String email, String name) { return this.email.equals(email) && this.name.equals(name); }
    public void updateData(String newName, String newAddress, String newPhone) {
        name = newName;
        address = newAddress;
        phone = newPhone;
    }
}

class Admin extends User {
    private static List<String> logs = new ArrayList<>();

    public Admin(String id, String name, String email, String address, String phone) {
        super(id, name, email, address, phone, "admin");
    }

    public static void log(String text) {
        logs.add(text);
    }

    public static List<String> getLogs() { return logs; }

    public void register() {}
    public boolean login(String email, String name) { return this.email.equals(email) && this.name.equals(name); }
    public void updateData(String newName, String newAddress, String newPhone) {
        name = newName;
        address = newAddress;
        phone = newPhone;
    }
}

class Category {
    String name;

    public Category(String name) { this.name = name; }
}

class Product {
    String id;
    String name;
    String desc;
    double price;
    int stock;
    Category category;

    public Product(String id, String name, String desc, double price, int stock, Category category) {
        this.id = id; this.name = name; this.desc = desc; this.price = price; this.stock = stock; this.category = category;
    }
}

class Payment {
    String id;
    String type;
    double amount;
    String status;

    public Payment(String id, String type, double amount) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.status = "pending";
    }

    public void process() { status = "paid"; }
    public void refund() { status = "refunded"; }
}

class Delivery {
    String id;
    String address;
    String status;
    String courier;

    public Delivery(String id, String address, String courier) {
        this.id = id; this.address = address; this.courier = courier; this.status = "processing";
    }

    public void send() { status = "shipped"; }
    public void track() {}
    public void finish() { status = "delivered"; }
}

class Order {
    String id;
    Client client;
    List<Product> items = new ArrayList<>();
    double total = 0;
    String status = "created";
    Payment payment;
    Delivery delivery;

    public Order(String id, Client client) {
        this.id = id;
        this.client = client;
    }

    public void addProduct(Product p) {
        items.add(p);
        total += p.price;
    }

    public void applyPromo(String code) {
        if (code.equals("SALE10")) total *= 0.9;
    }

    public void place() { status = "placed"; }
    public void cancel() { status = "cancelled"; }
    public void pay(String method) {
        payment = new Payment(UUID.randomUUID().toString(), method, total);
        payment.process();
        status = "paid";
        client.addPoints(total);
    }

    public void ship() {
        delivery = new Delivery(UUID.randomUUID().toString(), client.address, "Courier A");
        delivery.send();
        status = "shipped";
    }
}

public class ECommerceApp {

    static Scanner sc = new Scanner(System.in);
    static List<Client> clients = new ArrayList<>();
    static List<Admin> admins = new ArrayList<>();
    static List<Product> products = new ArrayList<>();

    public static void main(String[] args) {
        initData();
        mainMenu();
    }

    static void initData() {
        admins.add(new Admin("1", "admin", "admin@mail.com", "HQ", "000"));
        Category c1 = new Category("Электроника");
        products.add(new Product("p1", "Телефон", "Смартфон", 150000, 10, c1));
        products.add(new Product("p2", "Ноутбук", "Игровой", 450000, 5, c1));
    }

    static void mainMenu() {
        while (true) {
            System.out.println("1. Регистрация клиента");
            System.out.println("2. Вход клиента");
            System.out.println("3. Вход администратора");
            System.out.println("0. Выход");
            String choice = sc.nextLine();

            if (choice.equals("1")) registerClient();
            else if (choice.equals("2")) clientLogin();
            else if (choice.equals("3")) adminLogin();
            else if (choice.equals("0")) return;
        }
    }

    static void registerClient() {
        System.out.print("Имя: "); String name = sc.nextLine();
        System.out.print("Email: "); String email = sc.nextLine();
        System.out.print("Адрес: "); String addr = sc.nextLine();
        System.out.print("Телефон: "); String tel = sc.nextLine();
        Client c = new Client(UUID.randomUUID().toString(), name, email, addr, tel);
        clients.add(c);
        System.out.println("Готово");
    }

    static void clientLogin() {
        System.out.print("Email: "); String email = sc.nextLine();
        System.out.print("Имя: "); String name = sc.nextLine();

        for (Client c : clients) {
            if (c.login(email, name)) {
                clientMenu(c);
                return;
            }
        }
        System.out.println("Неверные данные");
    }

    static void adminLogin() {
        System.out.print("Email: "); String email = sc.nextLine();
        System.out.print("Имя: "); String name = sc.nextLine();

        for (Admin a : admins) {
            if (a.login(email, name)) adminMenu(a);
        }
    }

    static void clientMenu(Client c) {
        while (true) {
            System.out.println("1. Просмотр товаров");
            System.out.println("2. Создать заказ");
            System.out.println("3. Баллы: " + c.getPoints());
            System.out.println("0. Назад");
            String ch = sc.nextLine();

            if (ch.equals("1")) showProducts();
            else if (ch.equals("2")) createOrder(c);
            else if (ch.equals("0")) return;
        }
    }

    static void showProducts() {
        for (Product p : products) {
            System.out.println(p.id + " | " + p.name + " | " + p.price);
        }
    }

    static void createOrder(Client c) {
        Order o = new Order(UUID.randomUUID().toString(), c);
        while (true) {
            showProducts();
            System.out.print("Введите ID товара или 0 для завершения: ");
            String id = sc.nextLine();
            if (id.equals("0")) break;

            for (Product p : products) {
                if (p.id.equals(id)) o.addProduct(p);
            }
        }

        System.out.print("Промокод: ");
        o.applyPromo(sc.nextLine());

        o.place();
        System.out.print("Метод оплаты (card/wallet): ");
        o.pay(sc.nextLine());
        o.ship();

        System.out.println("Заказ оформлен. Итог: " + o.total);
    }


    static void adminMenu(Admin a) {
        while (true) {
            System.out.println("1. Логи");
            System.out.println("2. Добавить товар");
            System.out.println("0. Назад");
            String ch = sc.nextLine();

            if (ch.equals("1")) {
                for (String log : Admin.getLogs()) System.out.println(log);
            }
            else if (ch.equals("2")) addProduct(a);
            else if (ch.equals("0")) return;
        }
    }

    static void addProduct(Admin a) {
        System.out.print("Название: "); String n = sc.nextLine();
        System.out.print("Описание: "); String d = sc.nextLine();
        System.out.print("Цена: "); double p = Double.parseDouble(sc.nextLine());
        products.add(new Product(UUID.randomUUID().toString(), n, d, p, 10, new Category("Новое")));
        Admin.log("Админ " + a.getName() + " добавил товар " + n);
    }
}
  
