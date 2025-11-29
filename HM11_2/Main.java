import java.util.*;

interface OrderAPI {
    void createOrder(String clientId, List<String> products);
    void cancelOrder(String orderId);
}

interface DeliveryAPI {
    void updateStatus(String orderId, String status);
    String track(String orderId);
}

interface WarehouseAPI {
    boolean reserveProduct(String productId, int amount);
}

interface RouteOptimizationAPI {
    String getOptimalRoute(String from, String to);
}

interface CourierIntegrationAPI {
    String getCourierStatus(String trackingId);
}

interface NotificationAPI {
    void notifyClient(String clientId, String message);
}

interface AnalyticsAPI {
    void generateReport();
}

class OrderService implements OrderAPI {
    Map<String, String> orders = new HashMap<>();
    public void createOrder(String clientId, List<String> products) {
        orders.put(UUID.randomUUID().toString(), "created");
    }
    public void cancelOrder(String orderId) {
        orders.put(orderId, "cancelled");
    }
}

class DeliveryTrackingService implements DeliveryAPI {
    Map<String, String> statuses = new HashMap<>();
    public void updateStatus(String orderId, String status) {
        statuses.put(orderId, status);
    }
    public String track(String orderId) {
        return statuses.getOrDefault(orderId, "unknown");
    }
}

class WarehouseService implements WarehouseAPI {
    Map<String, Integer> stock = new HashMap<>();
    public boolean reserveProduct(String productId, int amount) {
        int qty = stock.getOrDefault(productId, 0);
        if (qty >= amount) {
            stock.put(productId, qty - amount);
            return true;
        }
        return false;
    }
}

class RouteOptimizationAdapter implements RouteOptimizationAPI {
    public String getOptimalRoute(String from, String to) {
        return "Route " + from + " -> " + to;
    }
}

class CourierIntegrationAdapter implements CourierIntegrationAPI {
    public String getCourierStatus(String trackingId) {
        return "In transit";
    }
}

class NotificationService implements NotificationAPI {
    public void notifyClient(String clientId, String message) {
        System.out.println("Notify " + clientId + ": " + message);
    }
}

class AnalyticsService implements AnalyticsAPI {
    public void generateReport() {
        System.out.println("Generating report");
    }
}

public class Main {
    public static void main(String[] args) {
        OrderService orderService = new OrderService();
        DeliveryTrackingService tracking = new DeliveryTrackingService();
        WarehouseService warehouse = new WarehouseService();
        RouteOptimizationAdapter routes = new RouteOptimizationAdapter();
        CourierIntegrationAdapter courier = new CourierIntegrationAdapter();
        NotificationService notify = new NotificationService();
        AnalyticsService analytics = new AnalyticsService();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1 Создать заказ");
            System.out.println("2 Обновить доставку");
            System.out.println("3 Отслеживать заказ");
            System.out.println("4 Оптимальный маршрут");
            System.out.println("5 Статус курьера");
            System.out.println("6 Отчет");
            System.out.println("0 Выход");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                orderService.createOrder("client1", List.of("p1", "p2"));
                notify.notifyClient("client1", "Заказ создан");
            } else if (choice.equals("2")) {
                System.out.print("ID заказа: ");
                String id = scanner.nextLine();
                System.out.print("Новый статус: ");
                String status = scanner.nextLine();
                tracking.updateStatus(id, status);
            } else if (choice.equals("3")) {
                System.out.print("ID заказа: ");
                String id = scanner.nextLine();
                System.out.println(tracking.track(id));
            } else if (choice.equals("4")) {
                System.out.println(routes.getOptimalRoute("Warehouse A", "Client B"));
            } else if (choice.equals("5")) {
                System.out.println(courier.getCourierStatus("track123"));
            } else if (choice.equals("6")) {
                analytics.generateReport();
            } else if (choice.equals("0")) {
                break;
            }
        }
    }
}
