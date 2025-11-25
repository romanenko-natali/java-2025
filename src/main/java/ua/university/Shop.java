package ua.university;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Shop {
    private final BlockingQueue<Item> basket = new LinkedBlockingQueue<>(5);

    public void supply(Item item) throws InterruptedException {
        basket.put(item);  // blocks when full (max 10)
        System.out.println("Постачальник поклав: " + item);
    }

    public Item buy() throws InterruptedException {
        Item item = basket.take();  // blocks when empty
        System.out.println("Покупець забрав: " + item);
        return item;
    }
}


record Item(String name){}

class MainShop {
    public static void main(String[] args) {
        Shop shop = new Shop();

        Thread supplier1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    shop.supply(new Item("Товар-" + i));
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread buyer1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    shop.buy();
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        supplier1.start();
        buyer1.start();
    }
}