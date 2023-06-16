package com.yuan.thread;

import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;


/**
 * @author yuanguozhi
 * @version V1.0
 * @date 2023/6/16 14:28
 * @Description null
 */
@Data
public class DocHandleThread implements  Callable<Boolean>{
    private BufferedImage bufferedImage;
    private String format;
    private File file;
    private InputStream inputStream;
    public DocHandleThread(BufferedImage bufferedImage, String format,File file,InputStream inputStream) {
        this.bufferedImage = bufferedImage;
        this.format = format;
        this.file = file;
        this.inputStream = inputStream;
    }
    @Override
    public Boolean call() throws IOException {
        Boolean flag=new Boolean(true);
        try {
            ImageIO.write(bufferedImage,format, file);
        } catch (IOException e) {
            flag=false;
            throw new RuntimeException(e);
        }
        finally {
            inputStream.close();
        }
        return flag;
    }


}
