import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class BitInputStream implements AutoCloseable {
    private BufferedInputStream bis;
    private String fileName;
    private int buffer;
    private int count;
    public BitInputStream(String fileName) throws IOException {
        this.fileName = fileName;
        bis = new BufferedInputStream(new FileInputStream(fileName));
        buffer = 0;
        count = 0;
    }
    public int bitRead() throws IOException {
        if (count == 0){
            buffer = bis.read();
            count = 8;
            if (buffer == -1) return -1;
        }
        int bit = (buffer >> (count - 1)) & 1;
        count--;
        return bit;
    }
    public int byteRead() throws IOException {
        return bis.read();
    }
    public void close() throws IOException {
        bis.close();
    }
    public static void main(String[] args){
        /*
            这个类创建的目的就是以位为单位从原文件中读出数据的能力
            但是，这个类还提供了以字节读出数据的能力，不过一定要注意，以字节读出和以位读出不能随意间隔调用
            当位读出时，读出的数据量不够一个字节，此时再调用以字节读出就会出现问题
            只要读出的位的数量是字节的整倍数，那么就可以在其后调用读出字节的操作
            在这个用例中，可以看到首先以字节的方式读出了0x25这个数据，然后以位的方式读出了0B1001010111000000
            当读出的数据为-1（不论是以字节读取还是以位读取）时，就代表文件读取结束了。
            这个用例中使用的输入原文件是BitOutputStream这个类中测试程序生成的文件，可以先看输出再学习这个输入
         */
        try{
            BitInputStream bis = new BitInputStream("bitoutput");
            int data = bis.byteRead();
            System.out.printf("0x%x\n", data);
            int bit = bis.bitRead();
            while(bit != -1){
                System.out.print(bit+"  ");
                bit = bis.bitRead();
            }
            System.out.println();
            bis.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
