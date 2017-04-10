import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CheckAccuracyAndAdjustWeightforSelection {

    // Initialize data
    private static int maxVacation = 0;
    private static int minVacation = 60;
    private static int maxeCredit = 5;
    private static int mineCredit = 3000;
    private static double maxSalary = 10;
    private static double minSalary = 40;
    private static double maxProperty = 0;
    private static double minProperty = 20;
    private static ArrayList<Customer> allList = new ArrayList<Customer>();
    private static ArrayList<Customer> trainList;
    private static ArrayList<Customer> testList;
    private static double[][] typeSim = {{1, 0, 0, 0, 0},
                                 {0, 1, 0, 0, 0},
                                 {0, 0, 1, 0, 0},
                                 {0, 0, 0, 1, 0},
                                 {0, 0, 0, 0, 1}};
    private static double[][] lifeStyleSim = {{1, 0, 0, 0},
                                      {0, 1, 0, 0},
                                      {0, 0, 1, 0},
                                      {0, 0, 0, 1}};
    private static double[] w = {1, 1, 1, 1, 1, 1};

    /**
     * load data
     * @throws IOException
     */
    public static void initialize() throws IOException {
        //Go over the train list and set the max and min value for each part
        File trainFile = new File("trainProdSelection.arff");
        BufferedReader trainIn = new BufferedReader(new InputStreamReader(new FileInputStream(trainFile), "UTF-8"));
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
        
        //Read in original data and store them as customers after initialization done
        BufferedReader trainIn1 = new BufferedReader(new InputStreamReader(new FileInputStream(trainFile), "UTF-8"));
        for (String l = trainIn1.readLine(); l != null; l = trainIn1.readLine()) {
            if (!l.startsWith("@") && l.length() > 0) {
                allList.add(parseCustomer(l));
            }
        }
    }
    
    /**
     * Check the accuracy of the current test bulk
     * @param start
     * @return
     */
    public static int checkAccuracy(int start) {
        int right = 0;
        for (Customer customer : testList) {
            Map<Customer, Double> topK = getTopK(customer, 3);
            int curr = getPredict(topK);
            if (curr == allList.get(start).getProduct()) {
                right++;
            }
            start++;
        }
        return right;
    }
    
    /**
     * Set the new test and train lists
     * @param start
     * @param end
     */
    public static void setTestAndTrain(int start, int end) {
        trainList = new ArrayList<Customer>();
        testList = new ArrayList<Customer>();
        //System.out.println("0, " + start);
        for (int i = 0; i < start; i++) {
            trainList.add(allList.get(i));
        }
        //System.out.println(start + ", " + end);
        for (int i = start; i < end; i++) {
            testList.add(allList.get(i));
        }
        //System.out.println(end + ", " + allList.size());
        for (int i = end; i < allList.size(); i++) {
            trainList.add(allList.get(i));
        }
    }

    /**
     * Calculate the accuracy for all data
     * @return
     */
    public static double calculateAccuracy() {
        //Calculate the accuracy
        double total = 0;
        for (int i = 0; i < 31; i++) {
            //Set test and train list
            setTestAndTrain(i * 6, i * 6 + 6);
            int currAccuracy = checkAccuracy(i * 6);
            total += currAccuracy;
        }
        return total / 186;
    }

    public static void main(String[] args) throws IOException {
        
        //Initialize the data
        initialize();
        //Shuffle the original list
        Collections.shuffle(allList);
        //Set initial data
        int increase = 0;
        int decrease = 0;
        double oldAccuracy = calculateAccuracy();
        double currAccuracy = calculateAccuracy();
        
        while (calculateAccuracy() < 0.9) {
            for (int i = 0; i < 6; i++) {
                oldAccuracy = calculateAccuracy();
                currAccuracy = calculateAccuracy();
                double oldW = w[i];
                while (calculateAccuracy() <= currAccuracy && increase < 10) {
                    currAccuracy = calculateAccuracy();
                    w[i] *= 2;
                    increase++;
//                    System.out.println("w" + i + ": " + w[i]);
//                    System.out.println(calculateAccuracy());
                }
                if (calculateAccuracy() > oldAccuracy) {
                    increase = 0;
                    decrease = 0;
                    continue;
                } else {
                    w[i] = oldW;
                    while (calculateAccuracy() <= currAccuracy && decrease < 10) {
                        w[i] /= 2;
                        decrease++;
//                        System.out.println("w" + i + ": " + w[i]);
//                        System.out.println(calculateAccuracy());
                    }
                    if (calculateAccuracy() > oldAccuracy) {
                        increase = 0;
                        decrease = 0;
                    } else {
                        increase = 0;
                        decrease = 0;
                        w[i] = oldW;
                    }
                }
            }
        }
        System.out.println(w[0] + ", " + w[1] + ", " + w[2] + ", " + w[3] + ", " + w[4] + ", " +w[5]);
        System.out.println(calculateAccuracy());
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
    	sum += Math.pow(w[0] * (1 - typeSim[c1.getType()][c2.getType()]), 2);
    	sum += Math.pow(w[1] * (1 - lifeStyleSim[c1.getLifeStyle()][c2.getLifeStyle()]), 2);
    	sum += Math.pow(w[2] * c1.getVacation() - w[2] * c2.getVacation(), 2);
    	sum += Math.pow(w[3] * c1.geteCredit() - w[3] * c2.geteCredit(), 2);
    	sum += Math.pow(w[4] * c1.getSalary() - w[4] * c2.getSalary(), 2);
    	sum += Math.pow(w[5] * c1.getProperty() - w[5] * c2.getProperty(), 2);
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

    /**
     * Inner class for Customer.
     * @author Youjia
     */
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
        public void setW(double a, double b, double c, double d, double e, double f) {
            w[0] = a;
            w[1] = b;
            w[2] = c;
            w[3] = d;
            w[4] = e;
            w[5] = f;
        }
        public double[] getW() {
            return w;
        }
    }
}

