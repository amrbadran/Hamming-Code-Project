package com.example.hammingcode;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Hamming {

        public int k=0;
        public int n;
        public int total_errors;
        public int total_correcting;
        public String filePath = "test.txt";
        public String ParitySender = "";
        public String ParityReceive = "";

        public String outputPath = "output.";
        public String Extension;

        public String recData;

        public void HammingCode(){
            try{
                FileOutputStream fis = new FileOutputStream(outputPath+Extension);
                FileInputStream fr = new FileInputStream(filePath);
                Path path = Paths.get(filePath);

                byte[] data = Files.readAllBytes(path);

               // System.out.println(data.length);
                for(int i =0 ;i<data.length;i++){
                    int z = fr.read();
                    //System.out.println((char)z);
                    String binaryString = Integer.toBinaryString(z);
                    if (binaryString.length() > 8) {
                        binaryString = binaryString.substring(binaryString.length() - 8);
                    } else if (binaryString.length() < 8) {
                        binaryString = "0".repeat(8 - binaryString.length()) + binaryString;
                    }


                    String sent = transmitData(binaryString);


                    Random rnd = new Random();
                    int randomIndex = rnd.nextInt(10000)+ 1;
                    String noise;
                    if(randomIndex == 5) noise = AddBurstErrors(sent);
                    else noise = AddError(sent);
                    String receive = ReceiveData(noise); // 011100001001
                    int x = Integer.parseInt(this.recData,2);
                   // System.out.println((char)x + " " + this.recData);
                    fis.write((char)x);
                    String countErr = Integer.toBinaryString(Integer.parseInt(sent,2) ^ Integer.parseInt(noise,2));

                    for(int j=0;j<countErr.length();j++){
                        if(countErr.charAt(j) == '1') {
                            total_errors++;
                        }
                    }

                }

                System.out.println("Total Errors : "+total_errors);
                System.out.println("Total Correcting : "+total_correcting);

                System.out.println("Percentage : "+ (double)total_correcting/total_errors);
                fis.close();
            }

            catch (Exception e){
               e.printStackTrace();
            }
        }
        public int setBit(int n , int k){
            n = ((n) | (1<<k));
            return n;
        }
        public int ClrBit(int n, int k){
            n = ((n) & ~(1 << k));
            return n;
        }
        public boolean isBitSet(int n, int k){
            return ((((n) >> (k)) & 1) == 1);
        }
        public boolean isPowerOfTwo(int n){
            return ((n & (n-1)) == 0);
        }
        public String transmitData(String data){
            ParitySender = "";
            int m =  data.length(); // this is the length of the data

            //System.out.println(m);
            k=0;
            while(((1<<k)-1) < (m+k)) k++; // we know the size of parity bit
            //System.out.println(k);
            n = m + k; // the whole size of transimtted data (codeword)

            int count = 0;
            String transmittedData = "";
            char Data[] =new char[n+1];
            for(int i=1;i<=n;i++){
                if(isPowerOfTwo(i)){ // if the ith bit is power of two then we need to skip it
                    Data[i] = '?';
                    continue;
                }
               Data[i] = data.charAt(count++);
            }

            for(int i=0;i<k;i++){
                int idx = (int)Math.pow(2,i);
                int xor_result = 0;

                for(int j=1;j<=n;j++){
                    if((j & idx) != 0 && (j!= idx)) {
                        xor_result = xor_result ^ (Data[j] - '0');
                    }
                }

                Data[idx] = (char)(xor_result + '0');
                ParitySender = ParitySender + Data[idx];
            }
            //System.out.println(String.valueOf(Data));
           return new String(String.valueOf(Data)).substring(1);

        }

        public String AddError(String data){ // single error

            char Data[] = new char[n+1];
            for(int i=1;i<=n;i++) Data[i] = data.charAt(i-1);

            Random rnd = new Random();
            int randomIdx = rnd.nextInt(n) + 1;

            Data[randomIdx] = (char)(((Data[randomIdx]-'0')^1)+ '0'); // flip the bit

            return new String(String.valueOf(Data)).substring(1);

        }

        public String AddBurstErrors(String data){ // burst error
            char Data[] = new char[n+1];
            for(int i=1;i<=n;i++) Data[i] = data.charAt(i-1);
            Random rand = new Random();

            int no_errors = rand.nextInt(7) + 1; // generate number from 1 to 7

            Set<Integer> Set1 = new HashSet<>(); // the set is to ensure that the errors are distncit positions
            while(Set1.size() < no_errors){
                Random rnd = new Random();
                int randomIdx = rnd.nextInt(n) + 1;
                Set1.add(randomIdx);
            }

            for(Integer idx : Set1){
                Data[idx] = (char)(((Data[idx]-'0')^1)+ '0'); // flip the bit
            }

            return new String(String.valueOf(Data)).substring(1);
        }

        public String ReceiveData(String data){
            char Data[] = new char[n+1];
            for(int i=1;i<=n;i++) Data[i] = data.charAt(i-1);
            String ret = "";
            String recData = "";
            int count2 = 0;

            for(int i=0;i<k;i++){
                int idx = (int)Math.pow(2,i);
                int xor_result = 0;
                for(int j=1;j<=n;j++){
                    if((j & idx) != 0 && (j!= idx)) {
                        xor_result = xor_result ^ (Data[j] - '0');

                    }
                }
                xor_result = xor_result ^ (Data[idx] - '0');
                ret = ret + String.valueOf(xor_result);
            }
            StringBuilder obj = new StringBuilder(ret);
            obj.reverse();
            ret = obj.toString();
            ParityReceive = ret;
            int idx_correct = Integer.parseInt(ret,2);
            if(idx_correct < (n+1) && idx_correct>0){
                total_correcting++;
                Data[idx_correct] = (char) (((Data[idx_correct] - '0') ^ 1) + '0'); // flip the bit
            }
            for(int i=1;i<=n;i++){
                if(!isPowerOfTwo(i)){
                    recData += Data[i];
                }
            }
            this.recData = recData;

            return new String(String.valueOf(Data)).substring(1);
        }

        public void CountErrors(String s,String r){
            String countErr = Integer.toBinaryString(Integer.parseInt(s,2) ^ Integer.parseInt(r,2));
            for(int j=0;j<countErr.length();j++){
                if(countErr.charAt(j) == '1') {
                    total_errors++;
                }
            }
        }
}
