import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitOutputStream implements AutoCloseable {
    private BufferedOutputStream bos;
    private String fileName;
    private int buffer;  // 文件的基本写入操作是按字节执行，为了支持按位写入，需将8位写入的数据凑够1个字节再实际写入，这个整数类型充当了按位写入的缓冲区
    private int count; // 用于记录写入的位数是否够8位
    public BitOutputStream(String fileName) throws IOException {
        this.fileName = fileName;
        bos = new BufferedOutputStream(new FileOutputStream(fileName));
        count = 0;
        buffer = 0;
    }
    public void bitWrite(int bit) throws Exception{
        if (bit != 0 && bit != 1)
            throw new IllegalArgumentException("写入的数据必须是按二进制位，取值只能是0或者1。");
        //将写入的二进制位追加到缓冲区buffer中
        buffer = (buffer << 1) | bit;
        //每增加一位写入计数器就自加1
        count++;
        //如果计数器的值为8时，表明就可以将这个完整的1个字节写入到文件中
        //当然，在写完之后，还需要清空缓冲区以及计数器的值
        if(count == 8){
            bos.write(buffer);
            buffer = 0;
            count = 0;
        }
    }
    public void byteWrite(int data) throws IOException {
        bos.write(data);
    }
    public void close() throws IOException{
        if (count > 0){
            buffer <<= (8 - count);
            bos.write(buffer);
            buffer = 0;
            count = 0;
        }
        bos.flush();
        bos.close();
    }
    public static void main(String[] args){
        int[] contents = {0x25,1, 0, 0, 1, 0, 1, 0, 1, 1, 1};
        /*
            这个类创建的目的就是以位为单位向目标文件中写入数据的能力
            但是，这个类还提供了以字节写入数据的能力，不过一定要注意，以字节写入和以位写入不能随意间隔调用
            当位写入时，有可能写入的数据量不够一个字节，此时再调用以字节写入就会出现问题
            只要写入的位的量是整字节的倍数，那么就可以在其后调用写入字节的操作
            在这个用例中，可以看到首先以字节的写入方式写入了0x25这个数据，然后以位的方式写入了0B1001010111
            当关闭被写入的文件时，这个类会将不够1个字节的剩余位以0补齐
            所以，当打开这个文件查看内容时，应该看到3个字节，其内容为：0x2595C0
         */
        try{
            BitOutputStream bos = new BitOutputStream("bitoutput");
            int i = 0;
            bos.byteWrite(contents[i++]);
            for(; i < contents.length; i++)
                bos.bitWrite(contents[i]);
            bos.close();
        } catch(Exception e){
            System.out.println(e);
        }
    }
}
