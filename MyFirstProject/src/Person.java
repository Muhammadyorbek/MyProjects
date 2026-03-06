public class Person {
    private String name;
    private int age;
    private String ID;
    public Person(String name, String ID, int age) {
        this.name = name;
        this.ID = ID;
        this.age = age;
    }
    public void displayInfo() {
        System.out.println("Name: " + name + "\nID: " + ID + "\nAge: " + age);
    }

}
