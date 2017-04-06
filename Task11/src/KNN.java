import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    
    private static ArrayList<Customer> trainList = new ArrayList<Customer>();
    
    static double[][] typeSim = {{1, 0, 0, 0, 0},
    		              {0, 1, 0, 0, 0},
    		              {0, 0, 1, 0, 0},
    		              {0, 0, 0, 1, 0},
    		              {0, 0, 0, 0, 1}};
    static double[][] lifeStyleSim = {{1, 0, 0, 0},
    		                   {0, 1, 0, 0},
    		                   {0, 0, 1, 0},
    		                   {0, 0, 0, 1}};
      
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
        
        BufferedReader trainIn1 = new BufferedReader(new InputStreamReader(new FileInputStream(trainFile), "UTF-8"));
        for (String l = trainIn1.readLine(); l != null; l = trainIn1.readLine()) {
            if (!l.startsWith("@") && l.length() > 0) {
                trainList.add(parseCustomer(l));
            }
        }
        
        
        String testStr = "librarian,spend>>saving,48,35,20,2";
        Customer testCus = parseCustomer(testStr);
        Map<Customer, Double> topK = getTopK(testCus, 3);
        for (Customer cus : topK.keySet()) {
        	System.out.println(cus);
        }
        System.out.print(getPredict(topK));
        
    }
    
    private static int getPredict(Map<Customer, Double> customers) {
    	HashMap<Integer, Double> map = new HashMap<Integer, Double>();
    	for (Customer cus : customers.keySet()) {
    		double score = customers.get(cus);
    		int cls = cus.getProduct();
    		if (!map.containsKey(cls)) map.put(cls, 0.0);
    		map.put(cls, map.get(cls) + score);
    	}
    	ArrayList<Integer> list = new ArrayList<Integer>(map.keySet());
    	list.sort(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return Double.compare(map.get(o2), map.get(o1));
			}
    		
    	});
    	return list.get(0);
    }
    
    private static Map<Customer, Double> getTopK(Customer target, int k) {
    	HashMap<Customer, Double> map = new HashMap<Customer, Double>();
    	for (Customer cus : trainList) {
    		map.put(cus, getSim(cus, target));
    	}	
    	ArrayList<Customer> list = new ArrayList<Customer>(map.keySet());
    	list.sort(new Comparator<Customer>() {

			@Override
			public int compare(Customer o1, Customer o2) {
				return Double.compare(map.get(o2), map.get(o1));
			}
    		
    	});
    	HashMap<Customer, Double> res = new HashMap<Customer, Double>();
    	for (int i = 0; i < k; i++) {
    		Customer cus = list.get(i);
    		res.put(cus, map.get(cus));
    	}
    	return res;
    }
    
    private static double getSim(Customer c1, Customer c2) {
    	double sum = 0;
    	sum += Math.pow((1 - typeSim[c1.getType()][c2.getType()]), 2);
    	sum += Math.pow(1 - lifeStyleSim[c1.getLifeStyle()][c2.getLifeStyle()], 2);
    	sum += Math.pow(c1.getVacation() - c2.getVacation(), 2);
    	sum += Math.pow(c1.geteCredit() - c2.geteCredit(), 2);
    	sum += Math.pow(c1.getSalary() - c2.getSalary(), 2);
    	sum += Math.pow(c1.getProperty() - c2.getProperty(), 2);
    	return 1 / Math.sqrt(sum);
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
    		customer.setLifeStyle(0);
    		break;
    	case "spend<saving": 
    		customer.setLifeStyle(1);
    		break;
    	case "spend>saving": 
    		customer.setLifeStyle(2);
    		break;
    	case "spend>>saving": 
    		customer.setLifeStyle(3);
    		break;
    	}
    	customer.setVacation((Double.parseDouble(str[2]) - minVacation) / (maxVacation - minVacation));
    	customer.seteCredit((Double.parseDouble(str[3]) - mineCredit) / (maxeCredit -mineCredit));
    	customer.setSalary((Double.parseDouble(str[4]) - minSalary) / (maxSalary -minSalary));
    	customer.setProperty((Double.parseDouble(str[5]) - minProperty) / (maxProperty -minProperty));
    	if (str.length == 7) {
    		switch (str[6]) {
        	case "C1": 
        		customer.setProduct(1);
        		break;
        	case "C2": 
        		customer.setProduct(2);
        		break;
        	case "C3": 
        		customer.setProduct(3);
        		break;
        	case "C4": 
        		customer.setProduct(4);
        		break;
        	case "C5": 
        		customer.setProduct(5);
        		break;
        	}
    	}
    	return customer;
    }

}
