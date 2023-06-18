import com.liyh.model.entity.Comment;
import com.liyh.system.CommunityApplication;
import com.liyh.system.mapper.BillBoardMapper;
import com.liyh.system.mapper.CommentMapper;
import com.liyh.system.mapper.SysDeptMapper;
import com.liyh.system.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Author LiYH
 * @Description TODO
 * @Date 2023/5/25 0:30
 **/
@SpringBootTest(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    CommentMapper commentMapper;

    @Test
    public void test() {
        System.out.println(commentMapper.isFavor(1L, 1L));
    }
}
