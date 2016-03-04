package augsburg.se.alltagsguide.utilities;

import android.content.Context;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.common.Page;

/**
 * Created by Daniel-L
 * on 28.02.2016
 */
public class FileHelper {

    public static File getPDFFileLink(Context context, String fileLink){
        return new File(new File(context.getExternalFilesDir(null), "pdf"), fileLink.hashCode()+".pdf");
    }
    public static void downloadPDfs(List<Page> pages, FileDownloadListener listener, Context context) {
        List<String> urls = new ArrayList<>();
        for(Page page : pages){
            urls.addAll(page.getPdfs());
        }
        final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(listener);

        final List<BaseDownloadTask> tasks = new ArrayList<>();
        for (String url : urls) {
            tasks.add(FileDownloader.getImpl().create(url).setTag(url).setPath(getPDFFileLink(context, url).getPath()));
        }

        queueSet.disableCallbackProgressTimes();
        queueSet.setAutoRetryTimes(1);
        queueSet.downloadTogether(tasks);

        queueSet.start();
    }
     /*
     * Returns a list with all links contained in the input
     */
    public static List<String> extractUrls(String text)
    {
        List<String> pdfUrls = new ArrayList<>();
        Document doc = Jsoup.parse(text);
        Elements links = doc.select("a");
        for (Element elem : links){
            String linkHref = elem.attr("href");
            if (linkHref.contains(".pdf")){
                pdfUrls.add(linkHref);
            }
        }
        return pdfUrls;
    }

}
