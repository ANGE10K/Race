import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Main {
   public static int count = 0;
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3);
        CyclicBarrier cyclicBarrierStart = new CyclicBarrier(11);
        CyclicBarrier cyclicBarrierFinish = new CyclicBarrier(10);
        BlockingQueue blockingQueue = new LinkedBlockingQueue();
        System.out.println("Подготовка к гонке!");
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int place;
                    long racePreparation = (long) (Math.random() * 5000 + 1000);
                    String name = Thread.currentThread().getName();
                    try {
                        Thread.sleep(racePreparation);
                        System.out.println(name + " : Закончил подготовку");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        cyclicBarrierStart.await();
                        System.out.println(name + " поехал");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    long timeStart = System.currentTimeMillis();
                    regularRoad();
                    try {
                        semaphore.acquire();
                        tunnel();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    semaphore.release();
                    regularRoad();
                    place = finish();
                    long timeFinish = System.currentTimeMillis();
                    long time = timeFinish - timeStart;
                    if (count == 10) {
                        System.out.println("Подсчет результатов");
                    }
                    try {
                        Thread.sleep(80000 + (place * 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    leaderboard(place, time);
                }
            }).start();
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Обратный отсчет: ");
        for (int i = 1; i < 4; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println("Начало гонки!");
        try {
            cyclicBarrierStart.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    static void regularRoad() {
        long millis = (long) (Math.random() + 13000 + Math.random());
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    static void tunnel() {
        long millis = (long) (Math.random() + 1000 + Math.random());
        System.out.println(Thread.currentThread().getName() + " заехал в туннель");
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " выехал из туннеля");
    }
    static synchronized int finish() {
        System.out.println(Thread.currentThread().getName() + " завершил заезд");
        count++;
        return count;
    }
    static synchronized void leaderboard(int place, long time) {
        System.out.println(Thread.currentThread().getName() + " занял: " + place + "-место. Время: " + time);
    }
}
