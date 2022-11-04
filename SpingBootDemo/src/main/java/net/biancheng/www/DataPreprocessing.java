package net.biancheng.www;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

//该类用于处理指纹数据库文件的预处理
public class DataPreprocessing {
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        read();
        //testRead("C:\\Users\\asus\\Desktop\\train09_27.txt");
    }
    public static void read() {
        ArrayList<ArrayList<String>> MainList = new ArrayList<>();
        ArrayList<ArrayList<String>> WriteMainList = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#.00");
        try{
            String fileName = "C:\\Users\\asus\\Desktop\\train09_27.txt";
            File file = new File(fileName);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            ArrayList<String> SecondList = new ArrayList<>();
            ArrayList<String> TempReadList ;
            ArrayList<String> TempWriteList = new ArrayList<>();
            while((line = br.readLine()) != null){
                if(!line.equals("")) {
                    //System.out.println(line);
                    SecondList.add(line);
                }else {


                    MainList.add(SecondList);
                    SecondList=new ArrayList<>();
                }
            }
            for(int i=0;i<MainList.size();i++) {
                //System.out.println(i);
                TempReadList = MainList.get(i);
                //System.out.println(TempReadList.size());
                String[] split1 = TempReadList.get(0).split(" ");

                TempWriteList.add(split1[0]+" "+split1[1]);
                for(int j = 1;j<TempReadList.size();j++) {
                    //System.out.println(TempReadList.get(j));
                    String[] split2 = TempReadList.get(j).split("\\|");

                    String[] split3 = split2[0].split(" ");
                    String[] split4 = split2[1].split(" ");
                    //System.out.println(split4.length);
//					for(int s=0;s<split4.length;s++) {
//						System.out.print(split4[s]);
//					}
                    double sum = 0;
                    for(int k=1;k<split4.length;k++) {
                        sum = sum + Integer.parseInt(split4[k]);
                    }
                    String level = String.valueOf(df.format((double)sum/(double)(split4.length-1)));
                    TempWriteList.add(split3[1]+"   "+level);
                }
                WriteMainList.add(TempWriteList);
                TempWriteList = new ArrayList<>();
            }
            writeFile(WriteMainList);
        }catch(FileNotFoundException e) {
            System.out.print("文件不存在！");
        }catch(IOException d) {
            System.out.print(d.getMessage());
        }
    }
    public static void writeFile(ArrayList<ArrayList<String>> context) {
        try {
            File writeName = new File("C:\\Users\\asus\\Desktop\\data\\Train0228.txt"); // 相对路径，如果没有则要建立一个新的output.txt文件
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                String content = "";
                System.out.println("size:"+context.size());
                for(int i = 0;i<context.size();i++) {
                    //System.out.println(context.get(i).size());
                    for(int j = 0;j<context.get(i).size();j++) {
                        content=content+context.get(i).get(j)+"\n";
                    }
                    System.out.println(context.get(i).get(0));
                    content=content+"\n";
                }
                out.write(content); // \r\n即为换行
                out.write("\n"); // \r\n即为换行
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testRead(String fileName) {
        try{
            File file = new File(fileName);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            int min=Integer.MAX_VALUE;
            int max=Integer.MIN_VALUE;
            String line;
            int count = 0;
            while((line = br.readLine()) != null){
                if(!line.equals("")&&!line.contains(":")) {
                    count++;
                }
                if(line.contains("   ")){
                    String[] sss = line.split("   ");
                    int cu =Integer.valueOf(sss[1]);
                    if(cu>max) {
                        max=cu;
                    }
                    if(cu<min) {
                        min=cu;
                    }
                }
            }
            System.out.print(count);
            System.out.print("min:"+min+",max:"+max);
        }catch(FileNotFoundException e) {
            System.out.print("文件不存在！");
        }catch(IOException d) {
            System.out.print(d.getMessage());
        }
    }
}
