package sample;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;


public class DataParsing {
    public int getResult = 0;
    public ArrayList<String> namePage = new ArrayList<>();
    public ArrayList<String> descriptionPage = new ArrayList<>();
    public ArrayList<String> urlPage = new ArrayList<>();
    public ArrayList<String> imgPage = new ArrayList<>();
    public ArrayList<String> timePage = new ArrayList<>();
    public int viewPage = 0;

    public void getNews(String quary, String siteURL, LocalDate time, String genres, int more){
        clearArr();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String today = formatter.format(time);
        if(siteURL.equals("ТСН")) {
            tsn(quary, today, genres);
        }
        if(siteURL.equals("УНІАН")) {
            unian(quary, today, genres);
        }
        if(siteURL.equals("korrespondent.net")){
            korrespondent(quary, time, genres, more);
        }
    }


    private void korrespondent(String quary, LocalDate time, String genres, int more){
        Document doc = null;
        String date = time.getYear() + "/" + time.getMonth().toString().toLowerCase() + "/" + time.getDayOfMonth();
        String pr = "/p" + more + "/";
        try{
            if(quary.equals("")) {
                if (genres.equals("Всі")) doc = Jsoup.connect("https://ua.korrespondent.net/all/" + date + pr).get();
                if (genres.equals("Наука"))
                    doc = Jsoup.connect("https://ua.korrespondent.net/all/tech/science/" + date + pr).get();
                if (genres.equals("Економіка"))
                    doc = Jsoup.connect("https://ua.korrespondent.net/all/business/" + date + pr).get();
                if (genres.equals("Світ"))
                    doc = Jsoup.connect("https://ua.korrespondent.net/all/world/" + date + pr).get();
            }
            else{
                doc = Jsoup.connect("https://ua.korrespondent.net/Default.aspx?page_id=60&lang=ua&isd=1&roi=0&tp=0&st=1&stx=" + quary +"&y=&p=" + more).get();
            }
        }catch (IOException e){}

        Elements name = doc.getElementsByClass("article article_rubric_top");
        for (Element a : name) {
            a.getElementsByClass("article__rubric").remove();
            imgPage.add(a.getElementsByClass("article__img").attr("data-href"));
            namePage.add(a.getElementsByClass("article__title").text());
            urlPage.add(a.getElementsByClass("article__img-link").attr("href"));
            timePage.add(a.getElementsByClass("article__date").text().replace("- ", ""));
        }
        if(!doc.getElementsByClass("pagination pagination_center pagination_news").isEmpty()){
            String s = doc.getElementsByClass("pagination__link").text().replaceAll(" ", "\n");
            String[] lines = s.split("\r\n|\r|\n");
            viewPage = Integer.valueOf(lines.length);
        }
        else{
            viewPage = 0;
        }
        getResult = imgPage.size();
    }

    private void unian(String quary, String time, String genres){
        Document doc = null;
        try{

            if (genres.equals("Всі")) doc = Jsoup.connect("https://www.unian.ua/detail/all_news").get();
            if (genres.equals("Наука")) doc = Jsoup.connect("https://www.unian.ua/science").get();
            if (genres.equals("Економіка")) doc = Jsoup.connect("https://economics.unian.ua/detail/publications").get();
            if (genres.equals("Світ")) doc = Jsoup.connect("https://www.unian.ua/world").get();
        }catch (IOException e){}

        Elements name = doc.getElementsByClass("publication-title");
        for (Element a : name) {
            namePage.add(a.text());
            urlPage.add(a.select("a[href]").attr("href"));
        }

        name = doc.getElementsByClass("gallery-item news-inline-item");
        for (Element a : name) {
            imgPage.add(a.select("img").attr("data-src"));
        }

        name = doc.getElementsByClass("item time");
        for (Element a : name) {
            timePage.add(a.text());
        }

        getResult = imgPage.size();
    }

    private void tsn(String quary, String time, String genres){
        Document doc = null;
        boolean emptyQ = false;
        if(!quary.equals(" ") && !quary.equals(""))
            emptyQ = false;
        else
            emptyQ = true;

        try {
            if(!emptyQ){
                doc = Jsoup.connect("https://tsn.ua/search?query=" + quary + "&filter=").get();
            }
            else {
                if (genres.equals("Всі")) doc = Jsoup.connect("https://tsn.ua/novini-dnya?date=" + time).get();
                if (genres.equals("Наука")) doc = Jsoup.connect("https://tsn.ua/nauka_it").get();
                if (genres.equals("Економіка")) doc = Jsoup.connect("https://tsn.ua/groshi").get();
                if (genres.equals("Світ")) doc = Jsoup.connect("https://tsn.ua/svit").get();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements el = doc.getElementsByClass("o-jumbotron o-jumbotron-lg o-jumbotron-primary u-pa-0");
        el.remove();

        Elements name = null;

        if(emptyQ)
            name = doc.getElementsByClass("c-entry-embed");
        else
            name = doc.getElementsByClass("h-entry c-entry");
        for (Element a : name) {
            Elements s = a.getElementsByClass("u-url u-uid");
            namePage.add(s.text());
        }

        name = doc.getElementsByClass("dt-published c-post-time");
        for (Element a : name) {
            String s = a.text();
            timePage.add(s);
        }


        ArrayList<String> imgList = new ArrayList();
        if(emptyQ)
            name = doc.getElementsByClass("c-entry-embed");
        else
            name = doc.getElementsByClass("h-entry c-entry");
        for (Element a : name) {
            if (a.select("img").attr("src").contains("https"))
                imgList.add(a.select("img").attr("src"));
            imgList.add(a.select("img").attr("data-src"));
            urlPage.add(a.select("a[href]").attr("href"));
        }

        imgList.removeAll(Arrays.asList("", null));
        imgPage = imgList;

        getResult = imgPage.size();
    }

    public String getHTMLView(String url){
        if(url.contains("tsn.ua")) {
            Document doc = null;
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
            }

            Elements name = doc.getElementsByClass("e-content");
            String htmlpage = "";
            for (Element a : name) {
                a.select("c-share-extra-track js-share-extra is-shown").remove();
                a.select("o-jumbotron-row c-bar c-post-info c-entry-inverse js-gallery-bar").remove();
                a.select("div.c-bar.c-post-info").remove();
                htmlpage = "<html>\n" + "<head>\n" +
                        "\t\t<meta charset=\"utf8\">\n" +
                        "\t\t<title></title>\n" +
                        "\t</head>\n" +
                        "\t<style>\n" +
                        "\t\timg{\n" +
                        "\t\t\twidth: 400px;\n" +
                        "\t\t\theight: 400px;\n" +
                        "\t\t}\n" +
                        "</style>" +
                        "\t</head>\n" +
                        "\t<body>" + a.toString().replaceAll("href=", "") + "</body></html>";
            }

            return htmlpage;
        }
        if(url.contains("unian.ua")){
            Document doc = null;
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) { }
            Elements name = doc.getElementsByClass("article-text");
            String htmlpage = "";
            for (Element a : name) {
                a.select("div.mistake.padding-top-30").remove();
                a.getElementsByClass("bottom dflex    ").remove();
                a.getElementsByClass("other-lang margin-bottom-5").remove();
                htmlpage = "<html>\n" + "<head>\n" +
                        "\t\t<meta charset=\"utf8\">\n" +
                        "\t\t<title></title>\n" +
                        "\t</head>\n" +
                        "\t<style>\n" +
                        "\t\timg{\n" +
                        "\t\t\twidth: 400px;\n" +
                        "\t\t\theight: 400px;\n" +
                        "\t\t}\n" +
                        "</style>" +
                        "\t<body>" + a.toString().replaceAll("href=", "") + "</body></html>";
            }
            return htmlpage;
        }

        if(url.contains("korrespondent")){
            Document doc = null;
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) { }
            Elements name = doc.getElementsByClass("post-item clearfix");
            String htmlpage = "";
            for (Element a : name) {
                a.select("div.post-item__title").remove();
                a.getElementsByClass("post-item__info").remove();
                a.getElementsByClass("post-item__header clearfix").remove();
                htmlpage = "<html>\n" + "<head>\n" +
                        "\t\t<meta charset=\"utf8\">\n" +
                        "\t\t<title></title>\n" +
                        "\t</head>\n" +
                        "\t<style>\n" +
                        "\t\timg{\n" +
                        "\t\t\twidth: 400px;\n" +
                        "\t\t\theight: 400px;\n" +
                        "\t\t}\n" +
                        "</style>" +
                        "\t<body>" + a.toString().replaceAll("href=", "") + "</body></html>";
            }
            return htmlpage;

        }
        return "";
    }


    private void clearArr(){
        namePage.clear();
        descriptionPage.clear();
        urlPage.clear();
        imgPage.clear();
        timePage.clear();
        getResult = 0;
    }

    public String getCourse(){
        JSONArray jsonArray = null;

        try {
            jsonArray = readJsonFromUrl("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5");
        } catch (Exception e) {
            System.out.println("Валюта");
        }


        JSONObject jsonObject = jsonArray.getJSONObject(0);
        JSONObject jsonObject2 = jsonArray.getJSONObject(1);

        String usd = String.valueOf(jsonObject.get("buy"));
        String eur = String.valueOf(jsonObject2.get("buy"));

        String formatUSD = String.format("%.2f", Double.valueOf(usd));
        String formatEUR = String.format("%.2f", Double.valueOf(eur));

        String resultUSD = "1 USD : " + formatUSD + " UAH";
        String resultEUR = "1 EUR : " + formatEUR + " UAH";

        if(eur.equals(null)) resultEUR = " ";
        if(usd.equals(null)) resultUSD = " ";

        String result = resultUSD + "\n" + resultEUR;
        return result;
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public JSONArray readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsontext = readAll(rd);
            JSONArray json = new JSONArray(jsontext);
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        return null;
    }
}
