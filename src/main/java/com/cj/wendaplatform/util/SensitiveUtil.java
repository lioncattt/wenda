package com.cj.wendaplatform.util;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cj
 * @date 2019/7/22
 * 敏感词过滤
 * 词典树/前缀树
 * 特殊字符只针对空格或者&*……之类
 * 若敏感词中间插字母或数字则无效
 */
@Component
public class SensitiveUtil implements InitializingBean {

    /**
     * 初始化完该类的属性后会执行以下方法
     * 读取文件创建词典树
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();
        try {
            //创建字节流
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("SensitiveWords.txt");
            //创建字符流
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                lineTxt = lineTxt.trim();
                addWord(lineTxt);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //默认敏感词替换符
    private static final String DEFAULT_REPLACEMENT = "***";

    //节点类
    private class TrieNode {

        //true:关键词终结, false:继续
        private boolean end = false;

        //子节点  key:下一个字符, value:对应的节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();


        /**
         * 添加子节点
         *
         * @param key  关键词中的字符
         * @param node 对应的节点
         */
        void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);//添加到map中
        }

        /**
         * 获取下一个节点
         *
         * @param key
         * @return
         */
        TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        /**
         * 判断当前字符是否未关键词的结束字符(最后一个字符)
         *
         * @return
         */
        boolean isKeywordEnd() {
            return end;
        }

        /**
         * 构建树时 标记关键词最后一个字符
         *
         * @param end
         */
        void setKeywordEnd(boolean end) {
            this.end = end;
        }

        /**
         * 获取子节点总数
         *
         * @return
         */
        public int getSubNodeCount() {
            return subNodes.size();
        }
    }

    //根节点
    private TrieNode rootNode = new TrieNode();

    /**
     * 判断是否是一个特殊符号或空格
     * 比如某人传了类似这样的字段: 想嫖@#⚪娼  吗
     * 用该方法过滤
     *
     * @param c
     * @return true：特殊字符需跳过，false：正常字符
     */
    private boolean isSymbol(char c) {
        int ic = (int) c;
        //0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    /**
     * 过滤敏感词
     *
     * @param text
     * @return
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        String replacement = DEFAULT_REPLACEMENT;
        StringBuilder result = new StringBuilder();

        TrieNode tempNode = rootNode;
        int begin = 0;//回滚数 指向有效字符串(除去特殊字符)的头部
        int position = 0;//当前比较的位置 遍历字符串的指针
        while (position < text.length()) {
            Character c = text.charAt(position);//拿出当前指向的字符
            if (isSymbol(c)) { //为特殊字符
                //指向rootNode表示还没开始比较或每次比较完都会被重置为rootNode 此时特殊字符一定出现敏感词前后
                if (tempNode == rootNode) { //特殊字符出现在敏感词之前或者之后是有效的字符
                    result.append(c);
                    begin++;
                }
                //出现在敏感词中间 忽略特殊字符
                position++;
                continue;
            }

            tempNode = tempNode.getSubNode(c);//查找当前字符的子节点
            if (tempNode == null) { //没有以begin开头的敏感词
                result.append(c);//当前字符不是敏感词,有效
                position = ++begin;//begin先往后走 继续判断下一个字符
                tempNode = rootNode;//重置为根节点
            } else { //不为空:
                if (tempNode.isKeywordEnd()) { //如果为true，则表示begin到position是敏感词
                    result.append(replacement);//打码
                    begin = ++position;//position先走，移动到下一个需要判断的字符
                    tempNode = rootNode;//重置为根节点
                } else {  //为false 表示可能是敏感词，position继续移动继续观察
                    position++;
                }
            }
        }
        //特殊情况: 敏感词：abcd  传入字符串：abc  begin指向a，position已遍历完但敏感词没遍历完
        // 即有效 加入到sb中
        result.append(text.substring(begin));

        return result.toString();
    }

    /**
     * 将自定义的敏感词构建成一颗前缀树
     * 从rootNode的子节点开始构建
     *
     * @param lineTxt
     */
    private void addWord(String lineTxt) {
        TrieNode tempNode = rootNode;//该指针指向当前节点
        for (int i = 0; i < lineTxt.length(); i++) {
            Character c = lineTxt.charAt(i);//获取字符串的字符

            if (isSymbol(c)) {
                continue;//跳过特殊字符
            }

            //查询当前树是否已存在相同的节点
            TrieNode node = tempNode.getSubNode(c);

            if (node == null) { //若不存在相同节点则创建当前节点的子节点
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            } //若存在则指针移动至该节点
            tempNode = node;

            if (i == lineTxt.length() - 1) { //当i循环到敏感词最后一位 标记
                //设置结束标志
                tempNode.setKeywordEnd(true);
            }
        }
    }


}
