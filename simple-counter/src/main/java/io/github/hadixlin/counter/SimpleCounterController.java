package io.github.hadixlin.counter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hadix
 */
@SpringBootApplication
@RestController
@RequestMapping
public class SimpleCounterController {

    private final JdbcTemplate jdbc;
    private final StringRedisTemplate redis;

    public SimpleCounterController(JdbcTemplate jdbc, StringRedisTemplate redis) {
        this.jdbc = jdbc;
        this.redis = redis;
    }

    @GetMapping
    public Map<String, Object> index() {
        Long counter = redis.opsForValue().increment("counter");
        List<Map<String, String>> result = jdbc.query("select * from host_summary", (rs, i) -> {
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();
            Map<String, String> row = new HashMap<>(count);
            for (int j = 1; j <= count; j++) {
                String columnName = metaData.getColumnName(j);
                row.put(columnName, rs.getString(columnName));
            }
            return row;
        });
        Map<String, Object> resp = new HashMap<>(2);
        resp.put("hostSummary", result.get(0));
        resp.put("counter", counter);
        return resp;
    }

    public static void main(String[] args) {
        new SpringApplication(SimpleCounterController.class).run(args);
    }
}
