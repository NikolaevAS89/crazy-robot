package ru.timestop.robot;

/**
 * @author t.i.m.e.s.t.o.p
 * @version 1.0.0
 * @since 29.09.2018
 */
public class CrezyRobot {

    private volatile boolean isGo = false;

    private void go() {
        if (isGo) {
            System.out.println("I alredy go");
        } else {
            isGo = true;
            Runtime.getRuntime().addShutdownHook(new Thread(new RobotMind()));
            RobotLeg leftLeg = new RobotLeg("Left");
            RobotLeg rightLeg = new RobotLeg("Right");
            leftLeg.setLock(rightLeg.lock);
            rightLeg.setLock(leftLeg.lock);
            Thread left = new Thread(leftLeg);
            Thread right = new Thread(rightLeg);
            left.start();
            right.start();
            try {
                left.join();
                right.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class RobotMind implements Runnable {
        public void run() {
            System.out.println("I complete my path");
        }
    }

    private class RobotLeg implements Runnable {
        private final String lable;
        private Object lock = new Object();
        private int stepCount = 3;

        RobotLeg(String lable) {
            this.lable = lable;
        }

        void setLock(Object obj) {
            this.lock = obj;
        }

        public void run() {
            try {
                while (stepCount > 0) {
                    synchronized (lock) {
                        lock.notify();
                        lock.wait();
                        System.out.println(this.lable + " leg move");
                        Thread.sleep(1000L);
                        --stepCount;
                    }
                }
                synchronized (lock) {
                    lock.notify();
                }
            } catch (InterruptedException e) {
                System.out.println(lable + " leg is broken");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            new CrezyRobot().go();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
