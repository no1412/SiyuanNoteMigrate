import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Markdown 标签转换
 *
 * @program: MdConvert
 * @author: Malcolm Li
 * @create: 2022-05-16 12:29
 */
public class MarkdownTagConvert {

    /**
     * 需要忽略的文件夹
     */
    private static final String IGNORE_DIR = "_resources";

    /**
     * 允许处理的文件
     */
    private static final String ALLOW_HANDLE_FILE_PREFIX = "md";

    /**
     * 匹配的标签
     */
    private static final String REG_IMG = "(<img.*?src=\"(.*?)\".*?>)";

    private static final Pattern pattern = Pattern.compile(REG_IMG);

    /**
     * Markdown文件模板
     */
    private static final String MD_FILE_TEMPLATE = "![](%s)";

    /**
     * 递归处理文件夹下的文件
     * 将img标签图片转换为Markdown文件链接
     *
     * @param files 文件数组
     */
    private static void handleFiles(File[] files) {
        for (File file : files) {
            if (file.getName().equals(IGNORE_DIR)) {
                continue;
            }
            if (file.isDirectory()) {
                handleFiles(FileUtil.ls(file.getAbsolutePath()));
            }
            if (!ALLOW_HANDLE_FILE_PREFIX.equals(FileUtil.getSuffix(file))) {
                continue;
            }
            final String[] content = {FileUtil.readUtf8String(file)};
            boolean containsFlag = ReUtil.contains(REG_IMG, content[0]);
            if (!containsFlag) {
                continue;
            }
            ReUtil.findAll(pattern, content[0], (matcher) -> {
                content[0] = content[0].replace(matcher.group(1), String.format(MD_FILE_TEMPLATE, matcher.group(2)));
            });
            FileUtil.writeUtf8String(content[0], file);
        }
    }

    public static void main(String[] args) {
        System.out.println("Please input md folder path:");
        Scanner in = new Scanner(System.in);
        String next = in.next();
        File[] files = FileUtil.ls(next);
        handleFiles(files);
    }
}
