import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class KNN {
    static class Customer {
    	private int type;
    	private int lifeStyle;
    	private double vacation;
    	private double eCredit;
    	private double salary;
    	private double property;
    	private int product;
    	public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public int getLifeStyle() {
			return lifeStyle;
		}
		public void setLifeStyle(int lifeStyle) {
			this.lifeStyle = lifeStyle;
		}
		public double getVacation() {
			return vacation;
		}
		public void setVacation(double vacation) {
			this.vacation = vacation;
		}
		public double geteCredit() {
			return eCredit;
		}
		public void seteCredit(double eCredit) {
			this.eCredit = eCredit;
		}
		public double getSalary() {
			return salary;
		}
		public void setSalary(double salary) {
			this.salary = salary;
		}
		public double getProperty() {
			return property;
		}
		public void setProperty(double property) {
			this.property = property;
		}
		public int getProduct() {
			return product;
		}
		public void setProduct(int product) {
			this.product = product;
		}
		public String toString() {
			return "type:" + type + ",lifestyle:" + lifeStyle + ",vacation:" + vacation + ",eCredit:" + eCredit + ",Salary:" + salary + "Property:" + property + ",Product:" + product;
		}
    }
    
    private static int maxVacation = 0;
    private static int minVacation = 60;
    private static int maxeCredit = 5;
    private static int mineCredit = 3000;
    private static double maxSalary = 10;
    private static double minSalary = 40;
    private static double maxProperty = 0;
    private static double minProperty = 20;
      
    public static void main(String[] args) throws IOException {
        File trainFile = new File("trainProdSelection.arff");
        BufferedReader trainIn = new BufferedReader(new InputStreamReader(new FileInputStream(trainFile), "UTF-8"));
        
        File testFile = new File("testProdSelection.arff");
        BufferedReader testIn = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        
        for (String l = trainIn.readLine(); l != null; l = trainIn.readLine()) {
            if (!l.startsWith("@") && l.length() > 0) {
            	String[] str = l.split(",");
            	int vacation = Integer.parseInt(str[2]);
            	int eCredit = Integer.parseInt(str[3]);
            	double salary = Double.parseDouble(str[4]);
            	double property = Double.parseDouble(str[5]);
            	maxVacation = Math.max(maxVacation, vacation);
            	minVacation = Math.min(minVacation, vacation);
            	maxeCredit = Math.max(maxeCredit, eCredit);
            	mineCredit = Math.min(mineCredit,eCredit);
            	maxSalary = Math.max(maxSalary, salary);
            	minSalary = Math.min(minSalary, salary);
            	maxProperty = Math.max(maxProperty, property);
            	minProperty = Math.min(minProperty, property);
            }
        }
        System.out.println(maxVacation);
        System.out.println(minVacation);
        System.out.println(maxeCredit);
        System.out.println(mineCredit);
        System.out.println(maxSalary);
        System.out.println(minSalary);
        System.out.println(maxProperty);
        System.out.println(minProperty);
        
        BufferedReader trainIn1 = new BufferedReader(new InputStreamReader(new FileInputStream(trainFile), "UTF-8"));
        ArrayList<Customer> trainList = new ArrayList<Customer>(); 
        for (String l = trainIn1.readLine(); l != null; l = trainIn1.readLine()) {
            if (!l.startsWith("@") && l.length() > 0) {
                trainList.add(parseCustomer(l));
            }
        }
        
        System.out.println(trainList.size());
        for (Customer customer : trainList) System.out.println(customer.toString());
        
        
//        
//        for (String l = testIn.readLine(); l != null; l = testIn.readLine()) {
//            if (!l.startsWith("@") && l.length() > 0) {
//                System.out.println(l);
//            }
//        }
    }
    
    private static Customer parseCustomer(String s) {
    	Customer customer = new Customer();
    	String[] str = s.split(",");
    	switch (str[0]) {
    	case "student": 
    		customer.setType(0);
    		break;
    	case "engineer": 
    		customer.setType(1);
    		break;
    	case "librarian": 
    		customer.setType(2);
    		break;
    	case "professor": 
    		customer.setType(3);
    		break;
    	case "doctor": 
    		customer.setType(4);
    		break;
    	}
    	switch (str[1]) {
    	case "spend<<saving": 
    		customer.setType(0);
    		break;
    	case "spend<saving": 
    		customer.setType(1);
    		break;
    	case "spend>saving": 
    		customer.setType(2);
    		break;
    	case "spend>>saving": 
    		customer.setType(3);
    		break;
    	}
    	customer.setVacation((Double.parseDouble(str[2]) - minVacation) / (maxVacation - minVacation));
    	customer.seteCredit((Double.parseDouble(str[3]) - mineCredit) / (maxeCredit -mineCredit));
    	customer.setSalary((Double.parseDouble(str[4]) - minSalary) / (maxSalary -minSalary));
    	customer.setProperty((Double.parseDouble(str[5]) - minProperty) / (maxProperty -minProperty));	
    	switch (str[6]) {
    	case "C1": 
    		customer.setType(1);
    		break;
    	case "C2": 
    		customer.setType(2);
    		break;
    	case "C3": 
    		customer.setType(3);
    		break;
    	case "C4": 
    		customer.setType(4);
    		break;
    	case "C5": 
    		customer.setType(5);
    		break;
    	}
    	return customer;
    }

}
