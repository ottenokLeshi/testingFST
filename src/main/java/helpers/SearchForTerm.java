package helpers;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

public class SearchForTerm {
    private static final String INDEX_PATH = "/Users/alivanov/Desktop/IndexWikipedia/Index";
    private static DirectoryReader reader;
    private static IndexSearcher searcher;

    public static void main(String[] args) throws IOException {
        FSDirectory directory = FSDirectory.open(new File(INDEX_PATH).toPath());
        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
        //Term term = new Term("body", "m[ ]?a[ ]?s[ ]?t[ ]?e[ ]?r[ ]?c[ ]?a[ ]?r[ ]?d");
        Term term = new Term("body", "mastercard");
        Query query = new FuzzyQuery(term, 2, 3, 10, true);
//        Query query = new RegexpQuery(term);
        ScoreDoc[] hits = searcher.search(query, 10).scoreDocs;
        System.out.println(searcher.doc(hits[0].doc).get("title"));
    }
}
