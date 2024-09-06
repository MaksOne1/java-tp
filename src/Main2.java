import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Comparator;
import java.util.function.Supplier;

abstract class BaseModel {
    public abstract void displayInfo();
}

class Contract extends BaseModel {
    private String label;
    private double amount;
    private Date startDate;

    public Contract(String label, double amount, Date startDate) {
        this.label = label;
        this.amount = amount;
        this.startDate = startDate;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public void displayInfo() {
        System.out.println("Contract label: " + this.getLabel());
        System.out.println("Amount: " + this.getAmount());
        System.out.println("Start Date: " + this.getStartDate());
    }

    public boolean isStarted() {
        return startDate.after(new Date());
    }

    public void extendContract(Date newEndDate) {
        // Assuming end date is another field, this is just a placeholder.
        System.out.println("Contract extended to: " + newEndDate);
    }
}

class Control extends BaseModel {
    private String controlledBy;
    private int priority;
    private boolean isActive;

    public Control(String controlledBy, int priority, boolean isActive) {
        this.controlledBy = controlledBy;
        this.priority = priority;
        this.isActive = isActive;
    }

    public String getControlledBy() {
        return this.controlledBy;
    }

    public void setControlledBy(String controlledBy) {
        this.controlledBy = controlledBy;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public void displayInfo() {
        System.out.println("Control ID: " + this.getControlledBy());
        System.out.println("Priority: " + this.getPriority());
        System.out.println("Active: " + this.isActive());
    }

    public String controlStatus() {
        return this.isActive ? "Control is active" : "Control is inactive";
    }

    public void activateControl() {
        this.isActive = true;
        System.out.println("Control activated");
    }
}

class Country extends BaseModel {
    private String name;
    private int population;
    private double area;

    public Country(String name, int population, double area) {
        this.name = name;
        this.population = population;
        this.area = area;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPopulation() {
        return this.population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public double getArea() {
        return this.area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    @Override
    public void displayInfo() {
        System.out.println("Country Name: " + this.getName());
        System.out.println("Population: " + this.getPopulation());
        System.out.println("Area: " + this.getArea() + " sq km");
        System.out.println("Density: " + this.populationDensity());
    }

    public double populationDensity() {
        return this.getPopulation() / this.getArea();
    }

    public void increasePopulation(int additionalPopulation) {
        this.population += additionalPopulation;
        System.out.println("Population increased by " + additionalPopulation);
    }
}

class Utils {
    public static <T extends BaseModel> void handleInputAndFindMaxMin(
            Supplier<T> inputSupplier,
            Comparator<T> comparator,
            Class<T> clazz
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
            element.displayInfo();
        }

        System.out.println("\nElement with max value:");
        maxElement.displayInfo();

        System.out.println("\nElement with min value:");
        minElement.displayInfo();
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
                Contract.class
        );

        Utils.handleInputAndFindMaxMin(
                Main2::inputControl,
                Comparator.comparingInt(Control::getPriority),
                Control.class
        );

        Utils.handleInputAndFindMaxMin(
                Main2::inputCountry,
                Comparator.comparingInt(Country::getPopulation),
                Country.class
        );

    }

    private static Contract inputContract() {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        System.out.println("Enter Contract label:");
        String label = scanner.nextLine();
        System.out.println("Enter Amount:");
        double amount = scanner.nextDouble();
        System.out.println("Enter Start Date (yyyy-MM-dd):");
        scanner.nextLine(); // Consume newline
        String dateString = scanner.nextLine();
        Date startDate = null;
        try {
            startDate = sdf.parse(dateString);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            System.exit(1);
        }

        return new Contract(label, amount, startDate);
    }

    private static Control inputControl() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Control ID:");
        String controlId = scanner.nextLine();
        System.out.println("Enter Priority:");
        int priority = scanner.nextInt();
        System.out.println("Is Active (true/false):");
        boolean isActive = scanner.nextBoolean();
        scanner.nextLine(); // Consume newline

        return new Control(controlId, priority, isActive);
    }

    private static Country inputCountry() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Country Name:");
        String name = scanner.nextLine();
        System.out.println("Enter Population:");
        int population = scanner.nextInt();
        System.out.println("Enter Area (sq km):");
        double area = scanner.nextDouble();
        scanner.nextLine();

        return new Country(name, population, area);
    }
}