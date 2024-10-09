import java.io.*;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;

interface getId {
    public int id();
}

abstract class BaseModel implements Serializable {
    protected int id;

    public BaseModel(int id) {
        this.id = id;
    }

    public BaseModel() {
        this.id = -1;
    }

    public abstract String toString();
}

 class Contract extends BaseModel implements getId {
    public String label;
    public double amount;
    public Date startDate;

     @Override
     public int id() {
         return this.id;
     }

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

 class Control extends BaseModel implements getId {
    public String controlledBy;
    public int priority;
    public boolean isActive;

    public Control(int id, String controlledBy, int priority, boolean isActive) {
        super(id);
        this.controlledBy = controlledBy;
        this.priority = priority;
        this.isActive = isActive;
    }

     @Override
     public int id() {
         return this.id;
     }

    public int getPriority() {
        return this.priority;
    }

    @Override
    public String toString() {
        return "Control{" +
                "id='" + this.id() + '\'' +
                ", priority=" + this.priority +
                ", isActive=" + this.isActive +
                ", status=" + this.controlStatus() +
                '}';
    }

    public String controlStatus() {
        return this.isActive ? "Control is active" : "Control is inactive";
    }
}

 class Country extends BaseModel implements getId {
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
     public int id() {
         return this.id;
     }

    @Override
    public String toString() {
        return "Country{" +
                "id='" + this.id() + '\'' +
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

abstract class FileOperator<T extends BaseModel> {
    protected String fileName;

    public FileOperator(String fileName) {
        this.fileName = fileName;
    }

    public abstract void write(T[] elements);

    public abstract T[] read();
}

class ByteFileOperator<T extends BaseModel> extends FileOperator<T> {
    public ByteFileOperator(String fileName) {
        super(fileName);
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

    private static <T extends BaseModel> T[] deserializeObjectsFromByteArray(byte[] byteArray) {
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

    @Override
    public void write(T[] elements) {
        byte[] byteArray = serializeObjectsToByteArray(elements);

        try (PushbackInputStream pbis = new PushbackInputStream(new ByteArrayInputStream(byteArray))) {
            int firstByte = pbis.read();
            pbis.unread(firstByte);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream fos = new FileOutputStream(this.fileName + ".bin");
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

    }

    @Override
    public T[] read() {
        byte[] fileContent = readFileToByteArray(this.fileName + ".bin");

        return deserializeObjectsFromByteArray(fileContent);
    }
}



class TextFileOperator<T extends BaseModel> extends FileOperator<T> {
    public TextFileOperator(String fileName) {
        super(fileName);
    }

    @Override
    public void write(T[] elements) {
        try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(fileName + ".dat"))) {
            writer.writeObject(elements);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    @Override
    public T[] read() {
        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(fileName + ".dat"))) {
            return (T[]) reader.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading from file: " + e.getMessage());
            return null;
        }
    }
}


class Utils {
    public static <T extends BaseModel> void handleInputAndFindMaxMin(
            Supplier<T> inputSupplier,
            Comparator<T> comparator,
            Class<T> clazz,
            FileOperator<T> fileOperator
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

        fileOperator.write(elements);
        T[] readElements = fileOperator.read();

        System.out.println("\nRead elements from file:");
        for (T element : readElements) {
            System.out.println(element.toString());
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
                new TextFileOperator<>("contracts")
        );

        Utils.handleInputAndFindMaxMin(
                Main2::inputControl,
                Comparator.comparingInt(Control::getPriority),
                Control.class,
                new ByteFileOperator<>("controls")

        );

        Utils.handleInputAndFindMaxMin(
                Main2::inputCountry,
                Comparator.comparingInt(Country::getPopulation),
                Country.class,
                new ByteFileOperator<>("countries")

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
