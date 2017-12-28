package source;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.wltea.analyzer.dic.Dictionary;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetInfo {
    private static Dictionary dict;
    private static Map<String,String> map = new HashMap<String, String>();
    private static int total = 0;


    /*
        *get the details from given file which contains many docs.
        * details for each doc:title,url,publishid,keywords,description,content
     */
    public static void get_info(File f, IndexWriter writer) {
        try {
            FileInputStream fis = new FileInputStream(f);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String str;
            total = 0;
            while((str = reader.readLine()) != null && str.equals("<doc>")) {
                get_details(reader,writer);
                total++;
                System.out.println(total);
            }

            reader.close();
            fis.close();
            System.out.println("total docs: " + total + " of file: " + f.getName());


        } catch (Exception e) {
            System.out.println("Exception thrown  :" + e);
            e.printStackTrace();
        }

    }
    /*
        *the details from <doc> to </doc>
        * index the details of one doc
     */
    private static void get_details(BufferedReader reader,IndexWriter writer) throws IOException{
        String detail_str;
        map.clear();
        while((detail_str = reader.readLine()) != null && !(detail_str.equals("</doc>"))) {
            get_detail(detail_str);
        }
        Document doc = new Document();
        doc.add(new StringField("title",map.get("title"), Field.Store.YES));
        System.out.println(map.get("title"));
        doc.add(new StringField("url",map.get("url"), Field.Store.YES));
        if(map.get("publishid") == null )
            map.put("publishid","0,0,"+total);
        doc.add(new StringField("publishid",map.get("publishid"), Field.Store.YES));
        if(map.get("content") == null)
            map.put("content",map.get("description"));
        doc.add(new TextField("content",map.get("content"),Field.Store.YES));
        doc.add(new TextField("description",map.get("description"),Field.Store.YES));
        if(map.get("keywords") == null)
            map.put("keywords","");
        doc.add(new TextField("keywords",map.get("keywords"),Field.Store.YES));
        Term term = new Term("publishid",map.get("publishid"));
        writer.updateDocument(term,doc);
    }
    /*
        @required remove useless tags or &nbsp in content
        @param get the detail from the string
        *store the details in map
     */
    private static void get_detail(String detail_str) {
        String r_tag = "^<([a-zA-Z]+)>([u4E00-u9FA5]|.+)<(/[a-zA-Z]+)>$";
        String r_meta = "^<meta\\s*name=\"?([a-zA-z]+)\"?\\s*content=\"([u4E00-u9FA5]|.+)\".*>$";
        Pattern pattern_tag = Pattern.compile(r_tag);
        Matcher m_tag = pattern_tag.matcher(detail_str);
        Pattern pattern_meta = Pattern.compile(r_meta);
        Matcher m_meta = pattern_meta.matcher(detail_str);
        if(m_tag.find()) {                          //含有tag标签的行
            //System.out.println("tag matched");
            if(m_tag.group(1).equals("url") || m_tag.group(1).equals("Url"))
                map.put("url",m_tag.group(2));
            else if(m_tag.group(1).equals("title")||m_tag.group(1).equals("Title"))
                map.put("title",m_tag.group(2));
            else if(m_tag.group(1).equals("strong")||m_tag.group(1).equals("Strong")){
                if(map.containsKey("content")){
                    String temp = map.get("content");
                    map.put("content",temp+m_tag.group(2)+"\r\n");
                }
                else
                    map.put("content",m_tag.group(2)+"\r\n");
            }
        }
        else if(m_meta.find()) {
            //System.out.println("meta matched");
            if(m_meta.group(1).equals("keywords") || m_meta.group(1).equals("Keywords"))
                map.put("keywords",m_meta.group(2));
            else if(m_meta.group(1).equals("description") || m_meta.group(1).equals("Description"))
                map.put("description", m_meta.group(2));
            else if(m_meta.group(1).equals("publishid") || m_meta.group(1).equals("Publishid"))
                map.put("publishid",m_meta.group(2));
        }
        else if(detail_str.length() > 0 && detail_str.charAt(0) != '<'){
            //System.out.println("content matched");
            String clear_str = clear_content(detail_str);
            if(map.containsKey("content")){
                String temp = map.get("content");
                map.put("content",temp+clear_str+"\r\n");
            }
            else
                map.put("content",clear_str+"\r\n");
        }
        
    }

    private static String clear_content(String s) {
        String r = "<(.+)>([u4E00-u9FA5]|.+)<(/[a-zA-Z]+)>";
        String clean_str = s.replaceAll(r,"");
        clean_str = clean_str.replaceAll("&nbsp;","");
        return clean_str;
    }

    public static void main(String[] args) {
        map.clear();
//
        String c = clear_content("7日晚，首次参加亚洲足球俱乐部冠军联赛的广州恒大<a onmouseover=\"WeiboCard.show(1894798092, 'sports' , this)\" href=\"http://weibo.com/gzevergrandefc?zw=sports\" target=\"_blank\">(微博)</a>在亚冠小组赛首战中以5比1狂胜上赛季亚冠联赛亚军、韩国全北现代队，打破“恐韩症”，令中国足球人长舒一口闷气。&nbsp;");
        System.out.println(c);
    }
}
