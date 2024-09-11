import java.io.*;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Scanner;
import java.util.function.Supplier;

abstract class BaseModel implements Serializable {
    private final int id;

    public int id() {
        return this.id;
    }

    public BaseModel(int id) {
        this.id = id;
    }

    public abstract String toString();
}

class Contract extends BaseModel {
    public String label;
    public double amount;
    public Date startDate;

    public Contract(int id, String label, double amount, Date startDate) {
        super(id);
        this.label = label;
        this.amount = amount;
        this.startDate = startDate;
    }

    public boolean isStarted() {
        return startDate.after(new Date());
    }

    public double getAmount() {
        return this.amount;
    }

    @Override
    public String toString() {
        return "Contract{" +
                "id='" + this.id() + '\'' +
                ", label='" + this.label + '\'' +
                ", amount=" + this.amount +
                ", startDate=" + this.startDate +
                ", started=" + this.isStarted() +
                '}';
    }
}

class Control extends BaseModel {
    public String controlledBy;
    public int priority;
    public boolean isActive;

    public Control(int id, String controlledBy, int priority, boolean isActive) {
        super(id);
        this.controlledBy = controlledBy;
        this.priority = priority;
        this.isActive = isActive;
    }

    public int getPriority() {
        return this.priority;
    }

    @Override
    public String toString() {
        return "Control{" +
                "id='" + super.id() + '\'' +
                ", priority=" + this.priority +
                ", isActive=" + this.isActive +
                ", status=" + this.controlStatus() +
                '}';
    }

    public String controlStatus() {
        return this.isActive ? "Control is active" : "Control is inactive";
    }
}

class Country extends BaseModel {
    public String name;
    public int population;
    public double area;

    public Country(int id, String name, int population, double area) {
        super(id);
        this.name = name;
        this.population = population;
        this.area = area;
    }

    public int getPopulation() {
        return this.population;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id='" + super.id() + '\'' +
                ", name='" + this.name + '\'' +
                ", population=" + this.population +
                ", area=" + this.area +
                ", density=" + this.getPopulationDensity() +
                '}';
    }

    public double getPopulationDensity() {
        return this.population / this.area;
    }

}

class Utils {
    public static <T extends BaseModel> void handleInputAndFindMaxMin(
            Supplier<T> inputSupplier,
            Comparator<T> comparator,
            Class<T> clazz,
            String fileName
    ) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the number of elements:");
        int count = scanner.nextInt();
        scanner.nextLine();

        @SuppressWarnings("unchecked")
        T[] elements = (T[]) Array.newInstance(clazz, count);

        for (int i = 0; i < count; i++) {
            elements[i] = inputSupplier.get();
        }

        T maxElement = findMax(elements, comparator);
        T minElement = findMin(elements, comparator);

        System.out.println("\nElements Information:");
        for (T element : elements) {
            System.out.println(element.toString());
        }

        System.out.println("\nElement with max value:");
        System.out.println(maxElement.toString());

        System.out.println("\nElement with min value:");
        System.out.println(minElement.toString());

        byte[] byteArray = serializeObjectsToByteArray(elements);

        try (PushbackInputStream pbis = new PushbackInputStream(new ByteArrayInputStream(byteArray))) {
            int firstByte = pbis.read();
            pbis.unread(firstByte);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream fos = new FileOutputStream(fileName + ".bin");
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
             SequenceInputStream sis = new SequenceInputStream(bais, bais)) {

            int data;
            while ((data = sis.read()) != -1) {
                bos.write(data);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] fileContent = readFileToByteArray(fileName + ".bin");

        T[] readElements = deserializeObjectsFromByteArray(fileContent, clazz);

        System.out.println("\nRead elements from file:");
        for (T element : readElements) {
            System.out.println(element.toString());
        }

        try (PrintStream ps = new PrintStream(new FileOutputStream("2_" + fileName + ".txt"))) {
            ps.println("Max Element:");
            ps.println(maxElement);
            ps.println("Min Element:");
            ps.println(minElement);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static <T extends BaseModel> byte[] serializeObjectsToByteArray(T[] objects) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(objects);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] readFileToByteArray(String fileName) {
        try (FileInputStream fis = new FileInputStream(fileName);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int data;
            while ((data = fis.read()) != -1) {
                baos.write(data);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static <T extends BaseModel> T[] deserializeObjectsFromByteArray(byte[] byteArray, Class<T> clazz) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            @SuppressWarnings("unchecked")
            T[] objects = (T[]) ois.readObject();
            return objects;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T findMax(T[] array, Comparator<T> comparator) {
        T max = array[0];
        for (T item : array) {
            if (comparator.compare(item, max) > 0) {
                max = item;
            }
        }
        return max;
    }

    public static <T> T findMin(T[] array, Comparator<T> comparator) {
        T min = array[0];
        for (T item : array) {
            if (comparator.compare(item, min) < 0) {
                min = item;
            }
        }
        return min;
    }
}

public class Main2 {

    public static void main(String[] args) {
        Utils.handleInputAndFindMaxMin(
                Main2::inputContract,
                Comparator.comparingDouble(Contract::getAmount),
                Contract.class,
                "contracts"
        );

        Utils.handleInputAndFindMaxMin(
                Main2::inputControl,
                Comparator.comparingInt(Control::getPriority),
                Control.class,
                "controls"

        );

        Utils.handleInputAndFindMaxMin(
                Main2::inputCountry,
                Comparator.comparingInt(Country::getPopulation),
                Country.class,
                "countries"

        );

    }

    private static Contract inputContract() {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        System.out.println("Enter ID:");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter Contract label:");
        String label = scanner.nextLine();
        System.out.println("Enter Amount:");
        double amount = scanner.nextDouble();
        System.out.println("Enter Start Date (yyyy-MM-dd):");
        scanner.nextLine();
        String dateString = scanner.nextLine();
        Date startDate = null;
        try {
            startDate = sdf.parse(dateString);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            System.exit(1);
        }

        return new Contract(id, label, amount, startDate);
    }

    private static Control inputControl() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter ID:");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter Control ID:");
        String controlId = scanner.nextLine();
        System.out.println("Enter Priority:");
        int priority = scanner.nextInt();
        System.out.println("Is Active (true/false):");
        boolean isActive = scanner.nextBoolean();
        scanner.nextLine();

        return new Control(id, controlId, priority, isActive);
    }

    private static Country inputCountry() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter ID:");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter Country Name:");
        String name = scanner.nextLine();
        System.out.println("Enter Population:");
        int population = scanner.nextInt();
        System.out.println("Enter Area (sq km):");
        double area = scanner.nextDouble();
        scanner.nextLine();

        return new Country(id, name, population, area);
    }
}
