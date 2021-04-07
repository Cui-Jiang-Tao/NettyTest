import java.io.*;

public class Employee implements Serializable
{
    public String name;

    public Employee(String n)
    {
        name = n;
    }


    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                '}';
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        f1();
    }

    public static void f1() throws IOException {
        // 持久化到本地存储中
        Employee employee = new Employee("Harry Hacker");

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("D://test1.txt"));
        oos.writeObject(employee);
    }


    public static void f2() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("D://test1.txt"));
        Employee employee = (Employee) ois.readObject();

        System.out.println(employee.toString());

    }
}

