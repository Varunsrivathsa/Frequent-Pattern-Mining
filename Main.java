import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

class Main{
	
	HashMap<Integer, LinkedList<Double>> wine = new HashMap<Integer, LinkedList<Double>>();
	HashMap<Double, Integer> element = new HashMap<Double, Integer>();
	TreeMap<Double, Integer> sortedMap = new TreeMap<Double, Integer>();
	FPTree root = new FPTree(null);
	HashMap<Double, FPTree> objMap = new HashMap<Double, FPTree>();
	int minSup;
	private static Scanner sc;
	
	public void rdFile(){
		String fileName = "/Users/Varun/Documents/workspace/FPgrowth/wine.data";
        String line = null;

        try {
            FileReader fileReader = new FileReader(fileName);
            
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            int index = 0;

            while((line = bufferedReader.readLine()) != null) {
            	index++;
            	String[] arr = line.split(",");
            	LinkedList<Double> list = new LinkedList<Double>();
            	            	
            	for(int i=0; i<arr.length; i++){
            		list.add(Double.valueOf(arr[i]));
            	}
            	            	
            	wine.put(index, list);
            }
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                  
        }
	}
	
	public void calculateFrequency(){
		int count = 1;
		LinkedList<Double> temp = new LinkedList<Double>();
		
		
		for(Map.Entry<Integer, LinkedList<Double>> i : wine.entrySet()){
			temp = i.getValue();
			
			for(int j=0; j<temp.size(); j++){
				if(!element.containsKey(temp.get(j))){
					element.put(temp.get(j), count);
				}
				else{
					count = element.get(temp.get(j));
					element.put(temp.get(j),count+1);
				}
			}
		}
	}
	
	public void minSup(int percentage){
		int cnt=0;
		for(int i=0; i<wine.size(); i++){
			cnt++;
		}
		minSup = (cnt*percentage)/100;
//		System.out.println("----------------------------------------------");
//		System.out.println("MinSup Condition is" + minSup);
//		System.out.println("----------------------------------------------");
	}

	public void removeElements(){
		for(Map.Entry<Integer, LinkedList<Double>> i : wine.entrySet()){
			LinkedList<Double> temp = i.getValue();
			LinkedList<Double> newtemp = new LinkedList<Double>();
			int keyWine = i.getKey();
			
			for(Double j: temp){
				if(element.get(j) >= minSup){
					newtemp.add(j);
				}
			}
			
			wine.put(keyWine, newtemp);
		}
	}
	
	
	public void sortPriority(){
		sortedMap = sortMapByValue(element);
		
//		System.out.println("--------------------Sorted----------------");
//	
//		for(Map.Entry<Double, Integer> i : sortedMap.entrySet()){
//			System.out.print(i.getKey() + ": ");
//			System.out.println(i.getValue());
//			System.out.println();
//		}
	}
	
	public static TreeMap<Double, Integer> sortMapByValue(HashMap<Double, Integer> map){
		Comparator<Double> comparator = new ValueComparator(map);
		TreeMap<Double, Integer> result = new TreeMap<Double, Integer>(comparator);
		result.putAll(map);
		return result;
	}
	
	public void prioritizeList(){
		for(Map.Entry<Integer, LinkedList<Double>> i : wine.entrySet()){
			LinkedList<Double> temp = new LinkedList<Double>();
			int key = i.getKey();
			temp = i.getValue();
			
			for(int j=0; j<temp.size(); j++){
				for(int k = 1; k < (temp.size() - j); k++){
					if(element.get(temp.get(k-1)) < element.get(temp.get(k))){
						double var = temp.get(k-1);
						temp.set(k-1, temp.get(k));
						temp.set(k, var);
					}
				}
			}
			
			wine.put(key, temp);
		}
	}
	
	public void getRow(){
		for(Map.Entry<Integer, LinkedList<Double>> i : wine.entrySet()){
			createFPTree(i.getValue());
		}
	}
	

	public void createFPTree(LinkedList<Double> list){
		FPTree currentNode = root;
		
		//System.out.println("----------------------------------------------");
		for(int i=0; i<list.size(); i++){
			FPTree childExists = checkChild(currentNode, list.get(i));
			if(childExists == null){
				FPTree newNode = new FPTree(list.get(i));
				objMap.put(list.get(i), newNode);
				if(i == list.size()-1){
					newNode.isLeaf = true;
				}
				currentNode.children.add(newNode);
				newNode.parent.addAll(currentNode.parent);
				newNode.parent.add(currentNode);
				if(currentNode.children.size() >1){
					currentNode.hasSibling = true;
				}
				currentNode = newNode;
				System.out.println("Child added With Value " + newNode.value);
				System.out.print("Parent of This Child " + newNode.value + " is ");
				for(int j=0; j<newNode.parent.size(); j++){
					System.out.print(newNode.parent.get(j).value + ", ");
				}
				System.out.println();
			}
			else{
				currentNode = childExists;
				currentNode.count = currentNode.count+1;
				System.out.println("<----------Child Count Increased to "+ currentNode.count+" ---------->");
			}
		}
		System.out.println("----------------------------------------------");
	}

	public FPTree checkChild(FPTree root, Double child){
		
		LinkedList<FPTree> children = root.children;
		FPTree temp;
		
		if(!children.isEmpty()){
			for(int i=0; i<children.size(); i++){
				temp = children.get(i);
				if(temp.value.equals(child)){
					return temp;
				}
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		
		Main m = new Main();
		System.out.println("Enter the percentage of tuples to be considered");
		sc = new Scanner(System.in);
		int percent = sc.nextInt();
		m.rdFile();
		m.calculateFrequency();
		m.sortPriority();
		m.prioritizeList();
		m.getRow();
		m.minSup(percent);
		m.removeElements();
	
		for(Map.Entry<Integer, LinkedList<Double>> i : m.wine.entrySet()){
			System.out.print(i.getKey() + ": ");
			LinkedList<Double> t = i.getValue();
			for(int j=0; j<t.size(); j++){
				System.out.print(t.get(j)+ " ");
			}
			System.out.println();
		}
		System.out.println("----------------------------------------------");
		
		/*for(Map.Entry<Double, FPTree> i : m.objMap.entrySet()){
			System.out.print(i.getKey() + "-->");
			LinkedList<FPTree> lst = i.getValue().parent;
			for(FPTree n : lst){
				System.out.print(n.value + " ");
			}
			System.out.println();
		}
		System.out.println("----------------------------------------------");
		
		//System.out.println("--------------------Not Sorted----------------");
		
		for(Map.Entry<Double, Integer> i : m.element.entrySet()){
			System.out.print(i.getKey() + ": ");
			System.out.println(i.getValue());
			System.out.println();
		}*/
	}
	              
}