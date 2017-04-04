import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class KNN {
    class Customer {
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
    }
    
    private static int maxVacation;
    private static int minVacation;
    private static int maxeCredit;
    private static int mineCredit;
    private static int maxSalary;
    private static int minSalary;
    private static int maxproperty;
    private static int minproperty;
    
  
    
    
    
    public static void main(String[] args) throws IOException {
        File trainFile = new File("trainProdSelection.arff");
        BufferedReader trainIn = new BufferedReader(new InputStreamReader(new FileInputStream(trainFile), "UTF-8"));
        
        File testFile = new File("testProdSelection.arff");
        BufferedReader testIn = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        
        for (String l = trainIn.readLine(); l != null; l = trainIn.readLine()) {
            if (!l.startsWith("@") && l.length() > 0) {
                System.out.println(l);
            }
        }
        
        ArrayList<Customer> trainList = new ArrayList<Customer>(); 
        for (String l = trainIn.readLine(); l != null; l = trainIn.readLine()) {
            if (!l.startsWith("@") && l.length() > 0) {
                System.out.println(l);
            }
        }
        
        for (String l = testIn.readLine(); l != null; l = testIn.readLine()) {
            if (!l.startsWith("@") && l.length() > 0) {
                System.out.println(l);
            }
        }
    }
    
    private static Customer parseCustomer(String s) {
    	Customer customer = new Customer();
    	String[] str = s.split(s);
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
    	customer.setVacation(Double.parseDouble(str[2]) / 60);
    	customer.seteCredit((Double.parseDouble(str[3]) - 5) / (3000 -5));
    	
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
    }

}
