package Simulate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Measurements {
	public static void main(String[] args) throws IOException {
		BufferedReader f = new BufferedReader(new FileReader("Data/timinginfo"));
		String line;
		ArrayList<Long> nums = new ArrayList<Long>();
		while((line=f.readLine()) != null) {
			nums.add(Long.parseLong(line));
		}
		Collections.reverse(nums);
		int iter = 4;
		ArrayList list = new ArrayList();
		double covtmp = 0.02;
		for(int i = 0; i < nums.size(); i++) {
			double mean = 0;
			for(int j = 0; j < iter; j++) {
				mean = mean + nums.get(j);
			}
			mean = mean/iter;
			double s = 0;
			for(int l = 0; l < iter; l++) {
				s = s + (nums.get(l) - mean)*(nums.get(l) - mean);
			}
			s = Math.sqrt(s/(iter-1));
			double cov = s/mean;
			if(cov < covtmp && iter < nums.size()) {
				if(i == 0) {
					for(int length = 0; length < iter; length++) {
						list.add(nums.get(length));
					}
				}
				else {
					list.add(nums.get(iter-1));
				}
				covtmp = cov;
				iter++;
			}
			else if(i != nums.size()-1 && list.size() == 0){
				nums.remove(0);
				i = -1;
			}
			else {
				BufferedReader f2 = new BufferedReader(new FileReader("Data/means"));
				String line2;
				ArrayList ls = new ArrayList();
				while((line2 = f2.readLine()) != null) {
					ls.add(line2);
				}
				f2.close();
				long sum = 0;
				for(int m = 0; m < list.size(); m++) {
					sum = sum + (Long)list.get(m);
				}
				double mm = sum/list.size();
				ls.add(mm);
			    FileWriter fstream1 = new FileWriter("Data/means");
		        BufferedWriter out1 = new BufferedWriter(fstream1);
		        for(int len = 0; len < ls.size(); len++) {
		        	out1.write(ls.get(len) + "\n");
		        }
		        out1.close();
		        System.out.println(list.size());
		        System.out.println("done");
				break;
			}
		}
	}
}
