package source;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.util.*;

public class FileIndexer {
    private static Directory dir = null;
    private static Analyzer analyzer = new IKAnalyzer(true);  //@required
    private static Analyzer parseanalyzer = new IKAnalyzer(false);
    private static String searchword = "等等";
    private static Document hitdoc;
    public static IndexSearcher searcher;

    private static TopDocs topDocs;

    private static String get_file_content(File f) {
        try {
            String str;
            StringBuilder content = new StringBuilder();
            FileInputStream fis = new FileInputStream(f);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            while((str = reader.readLine()) != null) {
                content.append(str);
                content.append("\r\n");
            }

            reader.close();
            fis.close();
            return content.toString();

        }catch (Exception e){
            System.out.println("Exception thrown  :" + e);
            e.printStackTrace();
            return null;
        }
    }
    public static void fileindex() {
        try{
            String dirname = "C:/Users/lly/Desktop/test/data";
            File files = new File(dirname);
            dir = FSDirectory.open(new File("C:/Users/lly/Desktop/test/index"));
            IndexWriterConfig writerconfig = new IndexWriterConfig(Version.LUCENE_44,analyzer);
            IndexWriter writer = new IndexWriter(dir,writerconfig);
            for(File f:files.listFiles()) {

                //Map<String,String> map= new HashMap<String, String>();
                GetInfo.get_info(f,writer);
            }
            writer.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static TopDocs filesearch(String searchword) {
        try{
            dir = FSDirectory.open(new File("C:/Users/lly/Desktop/test/index/"));
            DirectoryReader reader = DirectoryReader.open(dir);
            String[] fields = {"content","title","keyword","description"};
            MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_44,fields,parseanalyzer);
            Query query = parser.parse(searchword);
            searcher = new IndexSearcher(reader);
            topDocs = searcher.search(query,100);
            System.out.println("total hits: " + topDocs.totalHits);
            for(ScoreDoc doc: topDocs.scoreDocs) {
                hitdoc = searcher.doc(doc.doc);
                System.out.println("____________________________");
                System.out.println(hitdoc.get("title"));
                System.out.println(hitdoc.get("publishid"));
                //System.out.println(hitdoc.get("content").substring(0,100));
                System.out.println("____________________________");
            }
            return topDocs;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    private static void displayToken(String text)  {
        try {
            TokenStream stream = analyzer.tokenStream("contents", new StringReader(text));
            CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
            stream.reset();
            while (stream.incrementToken()) {
                System.out.print("[" + term.toString() + "] ");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        //fileindex();
        filesearch(searchword);
        displayToken("金正日");
    }

}
