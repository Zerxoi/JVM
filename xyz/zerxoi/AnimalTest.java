package xyz.zerxoi;

public class AnimalTest {
    public void showAnimal(Animal animal) {
        animal.eat(); // 表现为晚期绑定
    }

    public void showHunt(Huntable h) {
        h.hunt(); // 表现为晚期邦迪
    }
}

interface Huntable {
    void hunt();
}

class Animal {
    public void eat() {
        System.out.println("动物进食");
    }
}

class Dog extends Animal implements Huntable {
    @Override
    public void eat() {
        super.eat(); // 表现为早期绑定 方法确定时 Animal 的 eat 你方法
        System.out.println("狗吃骨头");
    }
    @Override
    public void hunt() {
        System.out.println("狗拿耗子多管闲事");
    }
}

class Cat extends Animal implements Huntable {
    @Override
    public void eat() {
        System.out.println("猫吃鱼");
    }

    @Override
    public void hunt() {
        System.out.println("猫吃耗子天经地义");
    }
}