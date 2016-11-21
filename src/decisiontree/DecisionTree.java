/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decisiontree;

import static decisiontree.Tree.buildTree;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author masud
 */

class Data{
    int[]ara=new int[8];
    int Class;
    
    Data()
    {
        
    }
    Data(int Ara[],int Class)
    {
       this.ara=Ara;
       this.Class=Class;
    }
    
    void print()
    {
        for(int i=0;i<this.ara.length;i++){
            System.out.print(this.ara[i]+" ");
        }
        System.out.println(" --->"+this.Class);
    }
    int getValue(int index)
    {
        return this.ara[index];
    }
    int getType(){
        return this.Class;
    }
}



public class DecisionTree {

    /**
     * @param args the command line arguments
     */
    static ArrayList<Data>dataSet;
    static double entropy;
    static int totaldata,trainSize,testSize,truePos=0,trueNeg=0,falsePos=0,falseNeg=0;
    static double recall, accuracy, precision;
    
    public static void getData() throws IOException{
        dataSet = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("assignment1_data_set.csv"));
        String line = br.readLine();
        while((line = br.readLine()) !=null){
             String[] b = line.split(",");
             int[]tempAra=new int[b.length-1];
             for(int i=0;i<b.length-1;i++){
                 tempAra[i]=Integer.valueOf(b[i]);
             }
             int Class=Integer.valueOf(b[b.length-1]);
             Data d =new Data(tempAra,Class);
             dataSet.add(d);
             //System.out.println();
        }
        br.close();
        for(int i=0;i<dataSet.size();i++)
        {
            Data ob =dataSet.get(i);
//            ob.print();
        }
        
    }
    
    static void shuffleData()
    {
        
        Random rand = new Random();
        double scale = rand.nextDouble();
        int it=0;
        while(it<totaldata)
        {
            double r = rand.nextDouble();
            if(r > scale)
            {
                int pos1 = rand.nextInt(totaldata);
                int pos2 = rand.nextInt(totaldata);
                Collections.swap(dataSet, pos1, pos2);
                //System.out.println("swapping");
            }
            it++;
        }
    }
    
    static double calculateEntropy(ArrayList<Data> traindata){
        int pos=0,neg=0;
        int i=0;
        while(i<traindata.size())
        {
            Data ob =traindata.get(i);
            if(ob.Class!=1)  neg++;
            else pos++;
            i++;
        }
        //System.out.println(pos+" "+neg);
        double a = (double)pos/(double)traindata.size();
        double b = (double)neg/(double)traindata.size();
        double Entropy = -(a * (log(a)/log (2))) - (b * (log (b)/log (2)));
        return Entropy;
    }
    
    static int getClass(Node n, Data ob)
   {
       if(n.leaf==1)
       {
           //System.out.println("Decision "+n.label);
           return n.label;
       }
       int index = n.attribute;
       int i=0;
       while(i<n.child.size())
       {
           Branch b = n.child.get(i);
           if(b.valueChosen!=ob.getValue(index))
           {
//               System.out.println("Here");
           }
           if(b.valueChosen==ob.getValue(index))
           {
               return getClass(b.node, ob);
           }
           i++;
       }
       return 0;
   }
    
    public static void Test(Node n){
        trueNeg=0;
        truePos=0;
        falseNeg=0;
        falsePos=0;
        int i=trainSize;
        while(i<dataSet.size())
        {
            Data ob = dataSet.get(i);
            int type = getClass(n, ob);

            if(ob.Class==type)
            {
                if(type==0)trueNeg++;
                else truePos++;
            }
            else
            {
                if(type==0)falseNeg++;
                else falsePos++;
            }
            i++;
        }
        accuracy = (double)(truePos+trueNeg)/(double)(truePos+trueNeg+falsePos+falseNeg);
        recall = (double)truePos/(double) (truePos+falseNeg);
        precision = (double)truePos/(double) (truePos+falsePos);
        
        System.out.println("recall = "+recall+",  precision = "+precision+",  accuracy = "+accuracy);
    }
    
    public static void k_foldTest(ArrayList<Data>testdata,Node n){
        trueNeg=0;
        truePos=0;
        falseNeg=0;
        falsePos=0;
        int i=0;
        while(i<testdata.size())
        {
            Data ob = testdata.get(i);
            int type = getClass(n, ob);

            if(ob.Class==type)
            {
                if(type==0)trueNeg++;
                else truePos++;
            }
            else
            {
                if(type==0)falseNeg++;
                else falsePos++;
            }
            i++;
        }
        accuracy = (double)(truePos+trueNeg)/(double)(truePos+trueNeg+falsePos+falseNeg);
        recall = (double)truePos/(double) (truePos+falseNeg);
        precision = (double)truePos/(double) (truePos+falsePos);
        
        System.out.println("recall = "+recall+",  precision = "+precision+",  accuracy = "+accuracy);
    }
    
    public static ArrayList<Data> validationTest(Node n,int intPos,int lastPos){
        ArrayList<Data> temp = new ArrayList<>();
        for(int i=intPos;i<lastPos;i++)
        {
            Data ob = dataSet.get(i);
            int type = getClass(n, ob);
            ob.Class=type;
            temp.add(ob);
        }
        return temp;
    }
    
    public static void supervisedLearning() throws IOException{
        getData();
        totaldata = dataSet.size();
        testSize = (int)(.2*totaldata);
        trainSize = totaldata-testSize;
        System.out.println("Test set = "+testSize+" Training set = "+trainSize);
        double trecall=0, taccuracy=0, tprecision=0;
        int numberOfIteration=100;
        for(int I=0;I<numberOfIteration;I++){
            shuffleData();
            ArrayList<Data> traindata = new ArrayList<>();
            int i=0;
            while(i<trainSize)
            {
                Data d = dataSet.get(i);
                traindata.add(d);
                i++;
            }
            entropy=calculateEntropy(traindata);
            int attr[] = new int [8];
            for(i=0;i<8;i++)
                attr[i]=1;
            Node n =buildTree(traindata, attr);
            Test(n);
            trecall += recall;
            taccuracy += accuracy;
            tprecision += precision;
        }
        double avgRecall = trecall/(double)numberOfIteration;
        double avgAccuracy=taccuracy/(double)numberOfIteration;
        double avgPrecisions =tprecision/(double)numberOfIteration;
        double f1Score=2*((avgPrecisions*avgRecall)/(avgPrecisions+avgRecall));
        
        System.out.println("Avg Recall = "+avgRecall);
        System.out.println("Avg Precision = "+avgPrecisions);
        System.out.println("Avg Accuracy = "+avgAccuracy);
        System.out.println("Avg F1-Score = "+f1Score);
    }
    
    
    public static void semiSupervisedLearning() throws IOException{
        getData();
        totaldata = dataSet.size();
        testSize = (int)(.2*totaldata);
        trainSize = totaldata-testSize;
        System.out.println("Test set = "+testSize+" Training set = "+trainSize);
        double trecall=0, taccuracy=0, tprecision=0;
        for(int J=0;J<10;J++){
            shuffleData();
            int numberOfIteration=10;
            ArrayList<Data> traindata = new ArrayList<>();
            for(int i=0;i<trainSize;i++)
            {
                Data d = dataSet.get(i);
                traindata.add(d);
            }
            int dataSize = traindata.size();
            int validationSize = (int)(.5*dataSize);
            ArrayList<Data> validationData = new ArrayList<>();
            for(int i=0;i<validationSize;i++)
            {
                Data d = traindata.get(i);
                validationData.add(d);
            }
    //        System.out.println("Valid set = "+validationData.size()+" Training set = "+trainSize);
            ArrayList<Data> Temp = new ArrayList<>();
            double initialpos=.5,lastpos=.55;
            for(int I=0;I<numberOfIteration;I++){
                int start=(int)(initialpos*dataSize);
                int end=(int)(lastpos*dataSize);
                entropy=calculateEntropy(validationData);
                int attr[] = new int [8];
                for(int i=0;i<8;i++)
                    attr[i]=1;
                Node n =buildTree(traindata, attr);
                Temp=validationTest(n,start, end);
                for(int i=0;i<Temp.size();i++)
                {
                    Data d = Temp.get(i);
                    validationData.add(d);
                }
                initialpos+=.05;
                lastpos+=.05;
//                System.out.println("I "+I+" Valid set = "+validationData.size()+" Training set = "+trainSize);
            }
            entropy=calculateEntropy(validationData);
            int attr[] = new int [8];
            for(int i=0;i<8;i++)
                attr[i]=1;
            Node n =buildTree(traindata, attr);
            Test(n);

//            System.out.println(J+" th test Done");
            trecall += recall;
            taccuracy += accuracy;
            tprecision += precision;
        }
        
        double avgRecall = trecall/10;
        double avgAccuracy=taccuracy/10;
        double avgPrecisions =tprecision/10;
        double f1Score=2*((avgPrecisions*avgRecall)/(avgPrecisions+avgRecall));
        
        System.out.println("Avg Recall = "+avgRecall);
        System.out.println("Avg Precision = "+avgPrecisions);
        System.out.println("Avg Accuracy = "+avgAccuracy);
        System.out.println("Avg F1-Score = "+f1Score);
    }
    
    public static void k_foldValidation(int K) throws IOException {
        getData();
        totaldata = dataSet.size();
        int lenOfFold=totaldata/K;
        int startPos=0,endPos=lenOfFold;
        double trecall=0, taccuracy=0, tprecision=0;
        int numberOfIteration=100;
        for(int I=0;I<K;I++){
            ArrayList<Data> traindata = new ArrayList<>();
            ArrayList<Data> testdata = new ArrayList<>();
            for(int i=0;i<startPos;i++)
            {
                Data d = dataSet.get(i);
                traindata.add(d);
            }
            for(int i=startPos;i<endPos;i++)
            {
                Data d = dataSet.get(i);
                testdata.add(d);
            }
            for(int i=endPos;i<totaldata;i++)
            {
                Data d = dataSet.get(i);
                traindata.add(d);
            }
            entropy=calculateEntropy(traindata);
            int attr[] = new int [8];
            for(int i=0;i<8;i++)
                attr[i]=1;
            Node n =buildTree(traindata, attr);
            k_foldTest(testdata, n);
            trecall += recall;
            taccuracy += accuracy;
            tprecision += precision;
            startPos=endPos;
            endPos=endPos+lenOfFold;
        }
        double avgRecall = trecall/K;
        double avgAccuracy=taccuracy/K;
        double avgPrecisions =tprecision/K;
        
        System.out.println("Avg Recall = "+avgRecall);
        System.out.println("Avg Precision = "+avgPrecisions);
        System.out.println("Avg Accuracy = "+avgAccuracy);
    }
    
    public static void leave_one_out() throws IOException {
        getData();
        totaldata = dataSet.size();
//        System.out.println("Size "+totaldata);
        double trecall=0, taccuracy=0, tprecision=0;
        int totalCorrect=0;
        for(int I=0;I<totaldata;I++){
            
            ArrayList<Data> traindata = new ArrayList<>();
            for(int J=0;J<totaldata;J++){
                Data ob = dataSet.get(J);
                traindata.add(ob);
            }
//            System.out.println("Size "+traindata.size());
//            Data ob = traindata.get(I);
            Data ob=traindata.remove(I);
//            System.out.println("Size "+traindata.size());
            entropy=calculateEntropy(traindata);
            int attr[] = new int [8];
            for(int i=0;i<8;i++)
                attr[i]=1;
            Node n =buildTree(traindata, attr);
            int type = getClass(n, ob);
//            System.out.println("Type "+type);
//            System.out.println("Class "+ob.Class);
            if(ob.Class==type)totalCorrect++;
        }
        System.out.println("Correct "+totalCorrect);
        double avgAccuracy=(double)totalCorrect/totaldata;
        System.out.println("Avg Accuracy = "+avgAccuracy);
    }
    
    
    
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
//        getData();
//        totaldata = dataSet.size();
//        testSize = (int)(.2*totaldata);
//        trainSize = totaldata-testSize;
//        System.out.println("Test set = "+testSize+" Training set = "+trainSize);
//        double trecall=0, taccuracy=0, tprecision=0;
//        int numberOfIteration=100;
//        for(int I=0;I<numberOfIteration;I++){
//            shuffleData();
//            ArrayList<Data> traindata = new ArrayList<>();
//            for(int i=0;i<trainSize;i++)
//            {
//                Data d = dataSet.get(i);
//                traindata.add(d);
//            }
//            entropy=calculateEntropy(traindata);
//            int attr[] = new int [8];
//            for(int i=0;i<8;i++)
//                attr[i]=1;
//            Node n =buildTree(traindata, attr);
//            Test(n);
//            trecall += recall;
//            taccuracy += accuracy;
//            tprecision += precision;
//        }
//        double avgRecall = trecall/(double)numberOfIteration;
//        double avgAccuracy=taccuracy/(double)numberOfIteration;
//        double avgPrecisions =tprecision/(double)numberOfIteration;;
//        
//        System.out.println("Avg Recall = "+avgRecall);
//        System.out.println("Avg Precision = "+avgPrecisions);
//        System.out.println("Avg Accuracy = "+avgAccuracy);
        supervisedLearning();
//        semiSupervisedLearning();
        k_foldValidation(20);
//        leave_one_out();
        
    }
    
}
