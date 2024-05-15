import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import javax.imageio.ImageIO;


public class PNGLearningStripper
{

        public static void FindAndOutputIHDR(byte[] mybytes)
        {
            int foundIindex = -1;
            for (int ii=0; ii < mybytes.length; ii++)
            {
                if (mybytes[ii] == 'I')
                {
                    foundIindex = ii;
                    break;
                }
                 
            }

            if (mybytes[foundIindex] != 'I' ||
                mybytes[foundIindex+1] != 'H' ||
                mybytes[foundIindex+2] != 'D' ||
                mybytes[foundIindex+3] != 'R')
            {
                System.out.println("Could not find IHDR !!!!!\n");
                return;
            }

            System.out.println("IHDR");

            int width = 0;
            width |= (mybytes[foundIindex+4] & 0xFF);
            width <<= 8;
            width |= (mybytes[foundIindex+5] & 0xFF);
            width <<= 8;
            width |= (mybytes[foundIindex+6] & 0xFF);
            width <<= 8;
            width |= (mybytes[foundIindex+7] & 0xFF);
            System.out.println(String.format("width == 0x%X == %d", width, width));
            
            int height = 0;
            height |= (mybytes[foundIindex+8] & 0xFF);
            height <<= 8;
            height |= (mybytes[foundIindex+9] & 0xFF);
            height <<= 8;
            height |= (mybytes[foundIindex+10] & 0xFF);
            height <<= 8;
            height |= (mybytes[foundIindex+11] & 0xFF);
            System.out.println(String.format("height == 0x%X == %d\n", height, height));

            int bit_depth = (int)(mybytes[foundIindex+12] & 0xFF);
            System.out.println(String.format("bit depth, bits per sample or bits per palette index not bitsperpixel == 0x%X == %d\n", mybytes[foundIindex+12], mybytes[foundIindex+12]));

            int color_type = (int)(mybytes[foundIindex+13] & 0xFF);
            System.out.println(String.format("color type == 0x%X == %d", mybytes[foundIindex+13], mybytes[foundIindex+13]));
            System.out.println("0 == no color, no palette, no alpha");
            System.out.println("2 == color used, no palette, no alpha");
            System.out.println("3 == color used, palette used, no alpha");
            System.out.println("4 == no color, no palette, alpha channel");
            System.out.println("6 == color used, no palette, alpha channel");

System.out.println("");
System.out.println("Color Type    Allowed Bit Depths  Interpretation");
System.out.println("------------------------------------------------");
System.out.println("0             1,2,4,8,16    Each pixel is a grayscale sample");
System.out.println("2             8,16          Each pixel is an R,G,B triple");
System.out.println("3             1,2,4,8       Each pixel is a palette index;");
System.out.println("                            a PLTE chunk must appear");
System.out.println("4             8,16          Each pixel is a grayscale sample,");
System.out.println("                            followed by an alpha sample");
System.out.println("6             8,16          Each pixel is an R,G,B tiple,");
System.out.println("                            followed by an alpha sample");
System.out.println("");
System.out.println("The sample depth is the same as the bit depth");
System.out.println("except in the case of color type 3,");
System.out.println("in which the sample depth is always 8 bits\n");

            int compression = (int)(mybytes[foundIindex+14] & 0xFF);
            System.out.println(String.format("compression == 0x%X == %d", mybytes[foundIindex+14], mybytes[foundIindex+14]));
            System.out.println("0 == deflate/inflate compression with 32768 byte sliding window\n");
         
            int filter = (int)(mybytes[foundIindex+15] & 0xFF);
            System.out.println(String.format("filter == 0x%X == %d", mybytes[foundIindex+15], mybytes[foundIindex+15]));
            System.out.println("0 == adaptive filtering with 5 basic filter types\n");

            int interlace = (int)(mybytes[foundIindex+16] & 0xFF);
            System.out.println(String.format("interlace == 0x%X == %d", mybytes[foundIindex+16], mybytes[foundIindex+16]));
            System.out.println("0 == no interlace");
            System.out.println("1 == Adam7 interlace\n");


            int IHDR_chunk_CRC = 0;
            IHDR_chunk_CRC |= (mybytes[foundIindex+17] & 0xFF);
            IHDR_chunk_CRC <<= 8;
            IHDR_chunk_CRC |= (mybytes[foundIindex+18] & 0xFF);
            IHDR_chunk_CRC <<= 8;
            IHDR_chunk_CRC |= (mybytes[foundIindex+19] & 0xFF);
            IHDR_chunk_CRC <<= 8;
            IHDR_chunk_CRC |= (mybytes[foundIindex+20] & 0xFF);
            System.out.println(String.format("IHDR_chunk_CRC == 0x%X == %d\n", IHDR_chunk_CRC, IHDR_chunk_CRC));
            
            /***** shoot got to learn decompression
            [RFC-1950]
Deutsch, P. and J-L. Gailly, "ZLIB Compressed Data Format Specification version 3.3", RFC 1950, Aladdin Enterprises, May 1996.
ftp://ftp.isi.edu/in-notes/rfc1950.txt
[RFC-1951]
Deutsch, P., "DEFLATE Compressed Data Format Specification version 1.3", RFC 1951, Aladdin Enterprises, May 1996.
ftp://ftp.isi.edu/in-notes/rfc1951.txt

            *****/
        }//FindAndOutputIHDR

	
	public static Boolean TestDumpByteArray(byte[] bytearray)
        {
            try
            {
                int dumped = 0;
                FileWriter mywriter = new FileWriter("output.txt");
                for (int ii=0; ii < bytearray.length; ii++)
                    mywriter.write(String.format("%02X ", bytearray[ii]));
                return true;
            }
            catch (Exception ex) {
                System.out.println("TestDumpByteArray: Exception caught");
                ex.printStackTrace();
            }
            return false;
        }
	
    public static byte[] GetByteArray(String filename)
    {
        try
	{
            File myfile = new File(filename);
            System.out.println(String.format("file length in bytes == %d\n", myfile.length()));
            long myfilelen = myfile.length();
            int myfilelenint = (int)myfilelen;
            byte[] mybytearray = new byte[myfilelenint];
            FileInputStream mystream = new FileInputStream(myfile);
            int readlen = mystream.read(mybytearray);
            System.out.println(String.format("readlen == %d\n", readlen));
            mystream.close();
            return mybytearray;
	}
	catch (Exception ex)
	{
	    System.out.println("PNGLearningStripper: Exception caught");
            ex.printStackTrace();
	}
        return null;
    }


    public static void main(String[] args)
    {
        // open image
	System.out.println(String.format("args.length == %d\n", args.length));
	if (args.length < 1)
	    System.out.println("Usage: java.exe -cp . PNGLearningStripper <path-filename>");
	else
	{
	    System.out.println(String.format("args[0] == %s\n", args[0])); 
            String filename = args[0].toLowerCase().trim();
            byte[] mybytes = GetByteArray(filename);
	    //TestDumpByteArray(mybytes);
            FindAndOutputIHDR(mybytes);
        }
        System.out.println("DONE");
    }

}//class


