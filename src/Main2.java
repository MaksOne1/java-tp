import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

class Contract {
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

    public void displayContractDetails() {
        System.out.println("Contract Number: " + this.getLabel());
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

class Control {
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

    public void displayControlInfo() {
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

class Country {
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

    public void displayCountryInfo() {
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


public class Main2 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Contract contract = new Contract(null, 0, new Date());

        // Input and display Contract
        System.out.println("Enter Contract Number:");
        contract.setLabel(scanner.nextLine());
        System.out.println("Enter Amount:");
        contract.setAmount(scanner.nextDouble());
        System.out.println("Enter Start Date (yyyy-MM-dd):");
        String dateString = scanner.next();
        try {
            contract.setStartDate(sdf.parse(dateString));
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            System.exit(1);
        }


        contract.displayContractDetails();

        // Input and display Control
        Control control = new Control(null, 0, false);
        
        scanner.nextLine();
        System.out.println("Enter Controlled by:");
        control.setControlledBy(scanner.nextLine());
        System.out.println("Enter Priority:");
        control.setPriority(scanner.nextInt());
        System.out.println("Is Active (true/false):");
        control.setActive(scanner.nextBoolean());

        control.displayControlInfo();
        System.out.println(control.getControlledBy());


        Country country = new Country(null, 0, 0);
        
        scanner.nextLine();
        System.out.println("Enter Country Name:");
        country.setName(scanner.nextLine());
        System.out.println("Enter Population:");
        country.setPopulation(scanner.nextInt());
        System.out.println("Enter Area (sq km):");
        country.setArea(scanner.nextDouble());

        country.displayCountryInfo();

        scanner.close();
    }
}