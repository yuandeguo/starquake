package com.yuan.task;

import com.yuan.myEnum.CommonConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author yuanguozhi
 * @version V1.0
 * @date 2023/6/17 15:59
 * @Description null
 */
@Data
@AllArgsConstructor
public class DocDeleteProcessor {

    private StringRedisTemplate stringRedisTemplate;

    public void deleteDoc(String fileList) {
        List<String> range = stringRedisTemplate.opsForList().range(fileList, 0, -1);
        stringRedisTemplate.delete(fileList);
        // 执行订单取消操作，例如向数据库更新订单状态、发送通知等
        for(String filePath : range){
            Path file = Paths.get(CommonConst.TEMP_PIC_DIR_PATh +filePath);
            try {
                if (Files.exists(file)) {
                    Files.delete(file);
                    System.out.println("Temporary file deleted successfully.");
                } else {
                    System.out.println("Temporary file does not exist.");
                }
            } catch (Exception e) {
                System.out.println("Failed to delete temporary file: " + e.getMessage());
            }
        }
        System.out.println("删除文件"+range.toString());

    }
}
