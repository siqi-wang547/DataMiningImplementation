import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CheckAccuracyforIntroBinary {

    //Initialize data
    private static double maxMF = 0.5;
    private static double minMF = 20;
    private static double maxAB = 0;
    private static double minAB = 10;
    private static double maxIR = 0;
    private static double minIR = 100;
    private static double maxP = 0;
    private static double minP = 120;
    private static ArrayList<Product> allList = new ArrayList<Product>();
    private static ArrayList<Product> trainList;
    private static ArrayList<Product> testList;
    private static double[][] typeSim = {{1, 0, 0.1, 0.3, 0.2},
                                 {0, 1, 0, 0, 0},
                                 {0.1, 0, 1, 0.2, 0.2},
                                 {0.3, 0, 0.2, 1, 0.1},
                                 {0.2, 0, 0.2, 0.1, 1}};
    private static double[][] cusSim = {{1, 0.2, 0.1, 0.2, 0},
                                {0.2, 1, 0.2, 0.1, 0},
                                {0.1, 0.2, 1, 0.1, 0},
                                {0.2, 0.1, 0.1, 1, 0},
                                {0, 0, 0, 0, 1}};
    private static double[][] sizeSim = {{1, 0.1, 0},
                                 {0.1, 1, 0.1},
                                 {0, 0.1, 1}};
    private static double[][] proSim = {{1, 0.8, 0, 0},
                                {0.8, 1, 0.1, 0.5},
                                {0, 0.1, 1, 0.4},
                                {0, 0.5, 0.4, 1}};
    private static double[] w = {1, 1, 1, 1, 1, 1, 1, 1};
    

    public static void main(String[] args) throws IOException {
        initialize();
        Collections.shuffle(allList);
        System.out.println(calculateAccuracy());
    }
    
    private static void initialize() throws IOException {
        File trainFile = new File("trainProdIntro.binary.arff");
        BufferedReader trainIn = new BufferedReader(new InputStreamReader(new FileInputStream(trainFile), "UTF-8"));
        
        for (String l = trainIn.readLine(); l != null; l = trainIn.readLine()) {
            if (!l.startsWith("@") && l.length() > 0) {
                String[] str = l.split(",");
                double mf = Double.parseDouble(str[2]);
                double ab = Double.parseDouble(str[3]);
                double ir = Double.parseDouble(str[6]);
                double period = Double.parseDouble(str[7]);
                maxMF = Math.max(maxMF, mf);
                minMF = Math.min(minMF, mf);
                maxAB = Math.max(maxAB, ab);
                minAB = Math.min(minAB, ab);
                maxIR = Math.max(maxIR, ir);
                minIR = Math.min(minIR, ir);
                maxP = Math.max(maxP, period);
                minP = Math.min(minP, period);
            }
        }
        
        BufferedReader trainIn1 = new BufferedReader(new InputStreamReader(new FileInputStream(trainFile), "UTF-8"));
        for (String l = trainIn1.readLine(); l != null; l = trainIn1.readLine()) {
            if (!l.startsWith("@") && l.length() > 0) {
                allList.add(parseProduct(l));
            }
        }
    }
    
    /**
     * Set the new test and train lists
     * @param start
     * @param end
     */
    public static void setTestAndTrain(int start, int end) {
        trainList = new ArrayList<Product>();
        testList = new ArrayList<Product>();
        for (int i = 0; i < start; i++) {
            trainList.add(allList.get(i));
        }
        for (int i = start; i < end; i++) {
            testList.add(allList.get(i));
        }
        for (int i = end; i < allList.size(); i++) {
            trainList.add(allList.get(i));
        }
    }
    
    /**
     * Check the accuracy of the current test bulk
     * @param start
     * @return
     */
    public static int checkAccuracy(int start) {
        int right = 0;
        for (Product product : testList) {
            Map<Product, Double> topK = getTopK(product, 5);
            int curr = getPredict(topK);
            if (curr == allList.get(start).getLabel()) {
                right++;
            }
            start++;
        }
        return right;
    }

    /**
     * Calculate the accuracy for all data
     * @return
     */
    public static double calculateAccuracy() {
        double total = 0;
        for (int i = 0; i < 16; i++) {
            //Set test and train list
            setTestAndTrain(i * 10, i * 10 + 10);
            int currAccuracy = checkAccuracy(i * 10);
            total += currAccuracy;
        }
        return total / 160;
    }

    private static int getPredict(Map<Product, Double> products) {
        HashMap<Integer, Double> map = new HashMap<Integer, Double>();
        for (Product pdt : products.keySet()) {
            double score = products.get(pdt);
            int cls = pdt.getLabel();
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
    
    private static Map<Product, Double> getTopK(Product target, int k) {
        HashMap<Product, Double> map = new HashMap<Product, Double>();
        for (Product pdt : trainList) {
            map.put(pdt, getSim(pdt, target));
        }   
        ArrayList<Product> list = new ArrayList<Product>(map.keySet());
        list.sort(new Comparator<Product>() {

            @Override
            public int compare(Product o1, Product o2) {
                return Double.compare(map.get(o2), map.get(o1));
            }
            
        });
        HashMap<Product, Double> res = new HashMap<Product, Double>();
        for (int i = 0; i < k; i++) {
            Product pdt = list.get(i);
            res.put(pdt, map.get(pdt));
        }
        return res;
    }
    
    private static double getSim(Product p1, Product p2) {
        double sum = 0;
        sum += Math.pow(w[0] * (1 - typeSim[p1.getService_type()][p2.getService_type()]), 2);
        sum += Math.pow(w[1] * (1 - cusSim[p1.getCustomer()][p2.getCustomer()]), 2);
        sum += Math.pow(w[2] * p1.getMonthly_fee() - w[2] * p2.getMonthly_fee(), 2);
        sum += Math.pow(w[3] * p1.getAdvertisement_budget() - w[3] * p2.getAdvertisement_budget(), 2);
        sum += Math.pow(w[4] * (1 - sizeSim[p1.getSize()][p2.getSize()]), 2);
        sum += Math.pow(w[5] * (1 - proSim[p1.getPromotion()][p2.getPromotion()]), 2);
        sum += Math.pow(w[6] * p1.getInterest_rate() - w[6] * p2.getInterest_rate(), 2);
        sum += Math.pow(w[7] * p1.getPeriod() - w[7] * p2.getPeriod(), 2);
        return 1 / Math.sqrt(sum);
    }
    
    private static Product parseProduct(String s) {
        Product product = new Product();
        String[] str = s.split(",");
        switch (str[0]) {
        case "Loan": 
            product.setService_type(0);
            break;
        case "Bank_Account":
            product.setService_type(1);
            break;
        case "CD":
            product.setService_type(2);
            break;
        case "Mortgage":
            product.setService_type(3);
            break;
        case "Fund":
            product.setService_type(4);
            break;
        }
        switch (str[1]) {
        case "Business": 
            product.setCustomer(0);
            break;
        case "Professional": 
            product.setCustomer(1);
            break;
        case "Student": 
            product.setCustomer(2);
            break;
        case "Doctor": 
            product.setCustomer(3);
            break;
        case "Other": 
            product.setCustomer(4);
            break;
        }
        product.setMonthly_fee((Double.parseDouble(str[2]) - minMF) / (maxMF - minMF));
        product.setAdvertisement_budget((Double.parseDouble(str[3]) - minAB) / (maxAB - minAB));
        switch (str[4]) {
        case "Small":
            product.setSize(0);
            break;
        case "Medium":
            product.setSize(1);
            break;
        case "Large":
            product.setSize(2);
            break;
        }
        switch (str[5]) {
        case "Full":
            product.setPromotion(0);
            break;
        case "Web&Email":
            product.setPromotion(1);
            break;
        case "Web":
            product.setPromotion(2);
            break;
        case "None":
            product.setPromotion(3);
            break;      
        }
        product.setInterest_rate((Double.parseDouble(str[6]) - minIR) / (maxIR - minIR));
        product.setPeriod((Double.parseDouble(str[7]) - minP) / (maxP - minP));
        if (str.length == 9) {
            product.setLabel(Integer.parseInt(str[8]));
        }
        return product;
    }
    
    /**
     * Inner class.
     */
    static class Product {
        private int service_type;
        private int customer;
        private double monthly_fee;
        private double advertisement_budget;
        private int size;
        private int promotion;
        private double interest_rate;
        private double period;
        private int label;
        public String toString() {
            return "Service Type:" + service_type + "\tCustomer:" + customer + "\tMonthly fee:" + monthly_fee + "\tAdvertisement Budget:" + advertisement_budget + "\tSize:" + size + "\tPromotion:" + promotion + "\tInterest Rate:" + interest_rate + "\tPeriod:" + period + "\tLabel:" + label;
        }
        public int getService_type() {
            return service_type;
        }
        public void setService_type(int service_type) {
            this.service_type = service_type;
        }
        public int getCustomer() {
            return customer;
        }
        public void setCustomer(int customer) {
            this.customer = customer;
        }
        public double getMonthly_fee() {
            return monthly_fee;
        }
        public void setMonthly_fee(double monthly_fee) {
            this.monthly_fee = monthly_fee;
        }
        public double getAdvertisement_budget() {
            return advertisement_budget;
        }
        public void setAdvertisement_budget(double advertisement_budget) {
            this.advertisement_budget = advertisement_budget;
        }
        public int getSize() {
            return size;
        }
        public void setSize(int size) {
            this.size = size;
        }
        public int getPromotion() {
            return promotion;
        }
        public void setPromotion(int promotion) {
            this.promotion = promotion;
        }
        public double getInterest_rate() {
            return interest_rate;
        }
        public void setInterest_rate(double interest_rate) {
            this.interest_rate = interest_rate;
        }
        public double getPeriod() {
            return period;
        }
        public void setPeriod(double period) {
            this.period = period;
        }
        public int getLabel() {
            return label;
        }
        public void setLabel(int label) {
            this.label = label;
        }
    }
}
