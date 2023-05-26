import com.liyh.system.CommunityApplication;
import com.liyh.system.mapper.SysDeptMapper;
import com.liyh.system.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author LiYH
 * @Description TODO
 * @Date 2023/5/25 0:30
 **/
@SpringBootTest(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Test
    public void test() {
        System.out.println(sysDeptMapper.findAll());
    }
}
